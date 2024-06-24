package com.paperloong.homesensor.scheduler.api

import com.paperloong.homesensor.scheduler.model.SunriseSunsetResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/23
 */
interface SunriseSunsetApi {

    @GET("json")
    suspend fun getSunriseSunset(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("date") date: String,
        @Query("formatted") formatted: Int = 0,
        @Query("tzid") timezone: String = "Asia/Shanghai"
    ): SunriseSunsetResponse

    companion object {

        const val BASE_URL = "https://api.sunrise-sunset.org/"

    }
}