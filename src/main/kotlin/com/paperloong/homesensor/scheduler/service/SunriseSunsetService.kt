package com.paperloong.homesensor.scheduler.service

import com.paperloong.homesensor.scheduler.api.SunriseSunsetApi
import org.springframework.stereotype.Component

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/23
 */
@Component
class SunriseSunsetService(private val sunriseSunsetApi: SunriseSunsetApi) : ScopedService() {

    suspend fun getSunriseSunset(latitude: Double, longitude: Double, date: String) =
        sunriseSunsetApi.getSunriseSunset(latitude, longitude, date)

}