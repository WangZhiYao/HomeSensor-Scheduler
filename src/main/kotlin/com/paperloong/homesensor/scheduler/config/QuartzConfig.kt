package com.paperloong.homesensor.scheduler.config

import com.paperloong.homesensor.scheduler.job.StartupJob
import org.quartz.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/23
 */
@Configuration
@EnableScheduling
class QuartzConfig {

    @Bean
    fun startupComplete() = AtomicBoolean(false)

    @Bean
    fun startupJobDetail(): JobDetail {
        return JobBuilder.newJob(StartupJob::class.java)
            .withIdentity("StartupJob")
            .storeDurably()
            .build()
    }

    @Bean
    fun startupTrigger(startupJobDetail: JobDetail): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(startupJobDetail)
            .withIdentity("StartupTrigger")
            .startNow()
            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
            .build()
    }
}