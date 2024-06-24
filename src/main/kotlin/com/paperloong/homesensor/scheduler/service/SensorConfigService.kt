package com.paperloong.homesensor.scheduler.service

import com.paperloong.homesensor.scheduler.repository.SensorConfigMongoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/22
 */
@Service
class SensorConfigService(private val mongoRepository: SensorConfigMongoRepository) : ScopedService() {

    fun findSensorConfigBySensorId(sensorId: ObjectId) =
        flow { emit(mongoRepository.findSensorConfigBySensorId(sensorId)) }
            .flowOn(Dispatchers.IO)

}