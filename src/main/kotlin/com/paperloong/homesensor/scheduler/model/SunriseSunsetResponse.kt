package com.paperloong.homesensor.scheduler.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/23
 */
data class SunriseSunsetResponse(
    val status: String,
    val tzid: String,
    val results: Result
) {

    data class Result(
        val sunrise: Date,
        val sunset: Date,
        @JsonProperty("solar_noon")
        val solarNoon: Date,
        @JsonProperty("day_length")
        val dayLength: Int,
        @JsonProperty("civil_twilight_begin")
        val civilTwilightBegin: Date,
        @JsonProperty("civil_twilight_end")
        val civilTwilightEnd: Date,
        @JsonProperty("nautical_twilight_begin")
        val nauticalTwilightBegin: Date,
        @JsonProperty("nautical_twilight_end")
        val nauticalTwilightEnd: Date,
        @JsonProperty("astronomical_twilight_begin")
        val astronomicalTwilightBegin: Date,
        @JsonProperty("astronomical_twilight_end")
        val astronomicalTwilightEnd: Date
    )
}

val SunriseSunsetResponse.isSuccess
    get() = status == "OK"