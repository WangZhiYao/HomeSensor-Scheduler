import asyncio
import logging

import pytz
from apscheduler.schedulers.asyncio import AsyncIOScheduler

from databasae import init_database
from mqtt import MQTTClient
from scheduler import IlluminanceSensorScheduler
from setting import settings

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

tz = pytz.timezone(settings.tz)
scheduler = AsyncIOScheduler()


async def main():
    await init_database()

    mqtt_client = MQTTClient(
        settings.mqtt_client_id,
        settings.mqtt_host,
        settings.mqtt_port,
        settings.mqtt_username,
        settings.mqtt_password
    )

    await mqtt_client.connect()

    illuminance_sensor_scheduler = IlluminanceSensorScheduler(scheduler, tz, mqtt_client, settings.mqtt_publish_topic)
    await illuminance_sensor_scheduler.startup()

    scheduler.start()

    loop = asyncio.get_running_loop()
    stop_event = asyncio.Event()

    async def wait_for_stop():
        await stop_event.wait()
        scheduler.shutdown()
        await mqtt_client.disconnect()
        logging.info("stopped.")

    stop_task = loop.create_task(wait_for_stop())

    try:
        await stop_task
    except asyncio.CancelledError:
        pass


if __name__ == '__main__':
    asyncio.run(main())
