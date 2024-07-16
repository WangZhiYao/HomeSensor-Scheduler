import logging

from beanie import init_beanie
from beanie.operators import In, Eq
from motor.motor_asyncio import AsyncIOMotorClient

from model.sensor import Sensor, SensorConfig, SensorType
from setting import settings

mongo_client = AsyncIOMotorClient(settings.mongo_url)


async def init_database():
    logging.info("Initializing Database")
    await init_beanie(database=mongo_client.get_default_database(),
                      document_models=[Sensor, SensorConfig])
    logging.info("Database initialized successfully.")


async def find_illuminance_sensors():
    logging.info("Finding illuminance sensor")
    sensors = await Sensor.find(In(Sensor.type, [SensorType.ILLUMINANCE])).to_list()
    return sensors


async def find_sensor_config(sensor):
    logging.info("Finding sensor config")
    sensor_config = await SensorConfig.find_one(Eq(SensorConfig.sensor_id, sensor.sensor_id))
    return sensor_config
