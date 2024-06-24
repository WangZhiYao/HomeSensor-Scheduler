package com.paperloong.homesensor.scheduler.job

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.paperloong.homesensor.scheduler.constant.SensorType
import com.paperloong.homesensor.scheduler.ext.logger
import com.paperloong.homesensor.scheduler.model.*
import com.paperloong.homesensor.scheduler.service.MqttService
import com.paperloong.homesensor.scheduler.service.SensorConfigService
import com.paperloong.homesensor.scheduler.service.SensorService
import com.paperloong.homesensor.scheduler.service.SunriseSunsetService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.quartz.*
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/23
 */
@Component
class StartupJob(
    private val sensorService: SensorService,
    private val sensorConfigService: SensorConfigService,
    private val sunriseSunsetService: SunriseSunsetService,
    private val mqttService: MqttService,
    private val scheduler: Scheduler
) : QuartzJobBean() {

    private val logger by logger()
    private val mapper = jacksonObjectMapper()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun executeInternal(context: JobExecutionContext) {
        runBlocking {
            sensorService.findSensorsByType(SensorType.LIGHT)
                .flatMapConcat { sensors -> sensors.asFlow() }
                .flatMapConcat { sensor ->
                    sensorConfigService.findSensorConfigBySensorId(sensor.id)
                        .map { sensorConfigs -> sensor to sensorConfigs }
                }
                .flatMapConcat { (sensor, sensorConfigs) ->
                    sensorConfigs.asFlow().map { sensorConfig -> sensor to sensorConfig.config }
                }
                .flatMapConcat { (sensor, config) ->
                    val now = LocalDate.now()
                    getSunriseSunset(config, now.format(dateFormatter))
                        .map { result -> StartupParam(sensor, config, result) }
                }
                .flowOn(Dispatchers.IO)
                .catch {
                    logger.error("Error while StartupJob:", it)
                }
                .collect { param ->
                    val now = Date()
                    if (param.result.sunrise < now && param.result.sunset > now) {
                        setScheduleJob(param.result.sunset)
                    } else if (param.result.sunrise > now) {
                        setScheduleJob(param.result.sunset)
                        val sleep = Sleep((param.result.sunset.time - now.time) / 1000)
                        mqttService.publish(
                            "sensor/${param.sensor.mac}/config",
                            mapper.writeValueAsString(sleep)
                        )
                    } else if (param.result.sunset < now) {
                        getTomorrowSunriseSunset(param.sensor, param.config)
                    }
                }
        }
    }

    private fun getSunriseSunset(
        config: LightSensorConfig.Config,
        date: String
    ): Flow<SunriseSunsetResponse.Result> =
        flow {
            emit(sunriseSunsetService.getSunriseSunset(config.latitude, config.longitude, date))
        }
            .filter { response -> response.isSuccess }
            .map { response -> response.results }

    private fun getTomorrowSunriseSunset(sensor: Sensor, config: LightSensorConfig.Config) {
        runBlocking {
            val tomorrow = LocalDateTime.now().plusDays(1)
            getSunriseSunset(config, tomorrow.format(dateFormatter))
                .flowOn(Dispatchers.IO)
                .catch {
                    logger.error("Error while getTomorrowSunriseSunset: ${it.message}", it)
                }
                .collect { result ->
                    setScheduleJob(result.sunset)
                    val now = Date()
                    val sleep = Sleep((result.sunrise.time - now.time) / 1000)
                    mqttService.publish("sensor/${sensor.mac}/config", mapper.writeValueAsString(sleep))
                }
        }
    }

    private fun setScheduleJob(sunset: Date) {
        val jobDetail = JobBuilder.newJob(SunsetJob::class.java)
            .withIdentity("SunsetJob")
            .storeDurably()
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("SunsetTrigger")
            .startAt(sunset)
            .withSchedule(SimpleScheduleBuilder.simpleSchedule())
            .build()

        scheduler.scheduleJob(jobDetail, setOf(trigger), true)
        logger.info("Set ScheduleJob: $sunset")
    }

    data class StartupParam(
        val sensor: Sensor,
        val config: LightSensorConfig.Config,
        val result: SunriseSunsetResponse.Result
    )

}