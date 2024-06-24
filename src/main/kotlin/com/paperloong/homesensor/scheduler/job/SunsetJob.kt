package com.paperloong.homesensor.scheduler.job

import com.paperloong.homesensor.scheduler.constant.SensorType
import com.paperloong.homesensor.scheduler.ext.logger
import com.paperloong.homesensor.scheduler.model.LightSensorConfig
import com.paperloong.homesensor.scheduler.model.isSuccess
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
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/22
 */
@Component
class SunsetJob(
    private val sensorService: SensorService,
    private val sensorConfigService: SensorConfigService,
    private val sunriseSunsetService: SunriseSunsetService,
    private val scheduler: Scheduler
) : QuartzJobBean() {

    private val logger by logger()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun executeInternal(context: JobExecutionContext) {
        runBlocking {
            sensorService.findSensorsByType(SensorType.LIGHT)
                .flatMapConcat { sensors -> sensors.asFlow() }
                .flatMapConcat { sensor -> sensorConfigService.findSensorConfigBySensorId(sensor.id) }
                .flatMapConcat { sensorConfigs -> sensorConfigs.map { sensorConfig -> sensorConfig.config }.asFlow() }
                .flatMapConcat { config ->
                    val now = LocalDate.now()
                    val tomorrow = now.plusDays(1)
                    getSunriseSunset(config, tomorrow.format(dateFormatter))
                }
                .flowOn(Dispatchers.IO)
                .catch {
                    logger.error("Failed to get sunrise sunset time", it)
                }
                .collect { result ->
                    logger.info(result.toString())
                    rescheduleJob(context, result.sunset)
                }
        }
    }

    private fun getSunriseSunset(config: LightSensorConfig.Config, date: String) =
        flow {
            emit(sunriseSunsetService.getSunriseSunset(config.latitude, config.longitude, date))
        }
            .filter { response -> response.isSuccess }
            .map { response -> response.results }

    private fun rescheduleJob(context: JobExecutionContext, sunset: Date) {
        val jobDetail = context.jobDetail
        val trigger = TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity("SunsetTrigger")
            .startAt(sunset)
            .withSchedule(SimpleScheduleBuilder.simpleSchedule())
            .build()

        scheduler.rescheduleJob(TriggerKey.triggerKey("SunsetTrigger"), trigger)
        logger.info("RescheduleJob: $sunset")
    }
}