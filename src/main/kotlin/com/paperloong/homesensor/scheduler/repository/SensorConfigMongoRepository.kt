package com.paperloong.homesensor.scheduler.repository

import com.paperloong.homesensor.scheduler.model.LightSensorConfig
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/22
 */
@Repository
interface SensorConfigMongoRepository : MongoRepository<LightSensorConfig, String> {

    @Query("{ 'sensor.id': ?0 }")
    fun findSensorConfigBySensorId(sensorId: ObjectId): List<LightSensorConfig>

}