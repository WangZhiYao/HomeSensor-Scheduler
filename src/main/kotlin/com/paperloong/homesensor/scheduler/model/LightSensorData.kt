package com.paperloong.homesensor.scheduler.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/22
 */
@Document(collection = "sensor_data")
data class LightSensorData(
    @Id
    val id: ObjectId = ObjectId.get(),
    @DBRef
    val sensor: Sensor,
    val data: Data
) {

    data class Data(
        val lux: Int,
        val timestamp: Long
    )
}