package com.paperloong.homesensor.scheduler.ext

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/21
 */
inline fun <reified T> T.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(unwrapCompanionClass(T::class.java)) }
}

fun <T> T.logger(name: String): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(name) }
}

fun <T> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}
