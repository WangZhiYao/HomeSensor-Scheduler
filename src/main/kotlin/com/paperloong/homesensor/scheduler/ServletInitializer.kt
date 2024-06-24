package com.paperloong.homesensor.scheduler

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/21
 */
class ServletInitializer : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        return application.sources(SchedulerApplication::class.java)
    }

}
