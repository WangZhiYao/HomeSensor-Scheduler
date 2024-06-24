package com.paperloong.homesensor.scheduler.repository

import com.paperloong.homesensor.scheduler.constant.SensorType
import com.paperloong.homesensor.scheduler.model.Sensor
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/21
 */
@Repository
interface SensorMongoRepository : MongoRepository<Sensor, String> {

    fun findSensorsByType(type: SensorType): List<Sensor>

}