package com.paperloong.homesensor.scheduler.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/22
 */
@Configuration
class MongoConfig(private val mongoDbFactory: MongoDatabaseFactory, private val context: MongoMappingContext) {

    @Bean
    fun mappingMongoConverter(): MappingMongoConverter {
        val dbRefResolver = DefaultDbRefResolver(mongoDbFactory)
        val converter = MappingMongoConverter(dbRefResolver, context)
        converter.setTypeMapper(DefaultMongoTypeMapper(null))
        return converter
    }
}