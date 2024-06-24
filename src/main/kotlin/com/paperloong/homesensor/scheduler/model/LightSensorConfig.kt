package com.paperloong.homesensor.scheduler.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/21
 */
@Document(collection = "sensor_config")
data class LightSensorConfig(
    @Id
    val id: ObjectId = ObjectId.get(),
    @DBRef
    val sensor: Sensor,
    val config: Config
) {

    data class Config(
        @Field(value = "col_intvl")
        val collectionInterval: Int,
        val latitude: Double,
        val longitude: Double,
    )

}