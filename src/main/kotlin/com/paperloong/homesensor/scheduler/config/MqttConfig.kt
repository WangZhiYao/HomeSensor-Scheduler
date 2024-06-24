package com.paperloong.homesensor.scheduler.config

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler
import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import org.springframework.messaging.handler.annotation.Header


/**
 *
 *
 * @author WangZhiYao
 * @since 2024/6/21
 */
@Configuration
data class MqttConfig(val properties: MqttProperties) {

    @Bean
    fun mqttClientFactory(): MqttPahoClientFactory {
        val factory = DefaultMqttPahoClientFactory()
        val options = MqttConnectOptions()
        options.serverURIs = arrayOf(properties.url)
        options.userName = properties.username
        options.password = properties.password.toCharArray()
        options.isCleanSession = true
        options.isAutomaticReconnect = true
        factory.connectionOptions = options
        return factory
    }

    @Bean
    fun mqttOutboundChannel(): MessageChannel {
        return DirectChannel()
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    fun outbound(): MessageHandler {
        val messageHandler = MqttPahoMessageHandler(properties.clientId, mqttClientFactory())
        messageHandler.setAsync(true)
        messageHandler.setDefaultTopic("test")
        return messageHandler
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    interface MqttGateway {
        fun sendToMqtt(@Header(MqttHeaders.TOPIC) topic: String, message: String)
    }
}