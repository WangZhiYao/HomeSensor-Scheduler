package com.paperloong.homesensor.scheduler.service

import com.paperloong.homesensor.scheduler.constant.SensorType
import com.paperloong.homesensor.scheduler.ext.logger
import com.paperloong.homesensor.scheduler.repository.SensorMongoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.springframework.stereotype.Service

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/22
 */
@Service
class SensorService(
    private val mongoRepository: SensorMongoRepository
) : ScopedService() {

    private val logger by logger()

    fun findSensorsByType(type: SensorType) =
        flow {
            emit(mongoRepository.findSensorsByType(type))
        }
            .flowOn(Dispatchers.IO)

}