package com.paperloong.homesensor.scheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/21
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoRepositories
class SchedulerApplication

fun main(args: Array<String>) {
    runApplication<SchedulerApplication>(*args)
}
