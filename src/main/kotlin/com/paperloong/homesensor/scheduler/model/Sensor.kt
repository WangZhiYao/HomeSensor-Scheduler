package com.paperloong.homesensor.scheduler.model

import com.paperloong.homesensor.scheduler.constant.SensorType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/21
 */
@Document(collection = "sensor")
data class Sensor(
    @Id
    val id: ObjectId = ObjectId.get(),
    @Indexed(unique = true)
    val mac: String,
    val type: SensorType
)