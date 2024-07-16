import logging
from typing import Union

import paho.mqtt.client as mqtt
from paho.mqtt.properties import Properties


class MQTTClient:
    def __init__(self, client_id, host, port, username, password):
        self.host = host
        self.port = port
        self.client = mqtt.Client(
            callback_api_version=mqtt.CallbackAPIVersion.VERSION2,
            client_id=client_id
        )
        self.client.username_pw_set(username, password)
        self.client.on_connect = self.on_connect

    def on_connect(self, client, userdata, flags, reason_code, properties):
        if reason_code == mqtt.MQTT_ERR_SUCCESS:
            logging.info(f"Connected to MQTT broker {self.host}:{self.port}")
        else:
            logging.info(f"Failed to connect to MQTT broker {self.host}:{self.port} {reason_code}")

    async def connect(self):
        logging.info("Connecting to MQTT broker")
        try:
            self.client.connect(self.host, port=self.port)
            self.client.loop()
        except Exception as e:
            logging.error(f"Error connecting to MQTT broker: {e}")
            raise

    async def publish(
            self,
            topic: str,
            payload: Union[str, bytes, bytearray, int, float, None] = None,
            qos: int = 0,
            retain: bool = False,
            properties: Properties | None = None
    ):
        logging.info(
            f"Publishing to MQTT broker: topic={topic} payload={payload} qos={qos} retain={retain} properties={properties}")
        try:
            result_code, mid = self.client.publish(
                topic=topic,
                payload=payload,
                qos=qos,
                retain=retain,
                properties=properties
            )
            if result_code == mqtt.MQTT_ERR_SUCCESS:
                logging.info(f"Published to topic: [{topic}]")
                return True
            else:
                logging.error(f"Error publishing to topic: [{topic}] {result_code} {mid}")
                return False
        except Exception as e:
            logging.error(f"Error publishing to MQTT broker: {e}")
            return False

    async def disconnect(self):
        logging.info("Disconnecting from MQTT broker")
        try:
            self.client.disconnect()
            logging.info("Disconnected from MQTT broker")
        except Exception as e:
            logging.error(f"Error disconnecting from MQTT broker: {e}")
            raise
