package com.paperloong.homesensor.scheduler.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.paperloong.homesensor.scheduler.api.SunriseSunsetApi
import com.paperloong.homesensor.scheduler.ext.logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/23
 */
@Configuration
class ApiConfig {

    private val logger by logger("Scheduler")

    @Bean
    fun okHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor { message -> logger.info(message) }
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

    @Bean
    fun sunriseSunsetRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(SunriseSunsetApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(JacksonConverterFactory.create(jacksonObjectMapper()))
            .build()

    @Bean
    fun sunriseSunsetApi(sunriseSunsetRetrofit: Retrofit) =
        sunriseSunsetRetrofit.create(SunriseSunsetApi::class.java)

}