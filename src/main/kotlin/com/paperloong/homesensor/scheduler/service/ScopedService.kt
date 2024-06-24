package com.paperloong.homesensor.scheduler.service

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/22
 */
abstract class ScopedService {

    private val job = SupervisorJob()
    val serviceScope = CoroutineScope(EmptyCoroutineContext + job)

    @PreDestroy
    fun destroy() {
        job.cancel()
    }
}