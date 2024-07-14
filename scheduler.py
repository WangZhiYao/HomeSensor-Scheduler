import logging
from datetime import datetime

from databasae import find_light_sensors, find_sensor_config
from api import SunriseSunsetAPI


class LightSensorScheduler:
    """
    A class to manage the scheduling of light sensor configuration updates.
    """

    def __init__(self, scheduler, tz, mqtt_client, publish_topic):
        self.scheduler = scheduler
        self.tz = tz
        self.mqtt_client = mqtt_client
        self.publish_topic = publish_topic

    async def startup(self):
        sensors = await find_light_sensors()
        logging.info(f"Light sensors found: {len(sensors)}")
        for sensor in sensors:
            logging.info(f"Processing sensor: {sensor}")
            sensor_config = await find_sensor_config(sensor)
            logging.info(f"Sensor config: {sensor_config}")
            now = datetime.now(self.tz)
            date = now.strftime("%Y-%m-%d")
            try:
                sunrise, sunset = SunriseSunsetAPI.get_sunrise_sunset(
                    sensor_config.config['latitude'],
                    sensor_config.config['longitude'],
                    date,
                    self.tz
                )
                logging.info(f'Sunrise: {sunrise}, Sunset: {sunset}')

                if sunrise < now < sunset:
                    await self._on_sunrise(sensor, sensor_config)
                    self._add_job(False, sensor, sensor_config, sunset)
                else:
                    if sunrise > now:
                        self._add_job(True, sensor, sensor_config, sunrise)
                        self._add_job(False, sensor, sensor_config, sunset)
                    else:
                        await self._on_sunset(sensor, sensor_config)
            except Exception as e:
                logging.error(f"Error updating sensor config: {e}")

        self.scheduler.add_job(self.on_schedular, trigger='cron', month='*', day='*', hour='0', minute='0', second='30')

    async def on_schedular(self):
        sensors = await find_light_sensors()
        logging.info(f"Light sensors found: {len(sensors)}")
        for sensor in sensors:
            logging.info(f"Processing sensor: {sensor}")
            sensor_config = await find_sensor_config(sensor)
            logging.info(f"Sensor config: {sensor_config}")
            now = datetime.now(self.tz)
            date = now.strftime("%Y-%m-%d")
            try:
                sunrise, sunset = SunriseSunsetAPI.get_sunrise_sunset(
                    sensor_config.config['latitude'],
                    sensor_config.config['longitude'],
                    date,
                    self.tz
                )
                logging.info(f'Sunrise: {sunrise}, Sunset: {sunset}')
                self._add_job(True, sensor, sensor_config, sunrise)
                self._add_job(False, sensor, sensor_config, sunset)
            except Exception as e:
                logging.error(f"Error updating sensor config: {e}")

    def _add_job(self, is_sunrise, sensor, sensor_config, date):
        if is_sunrise:
            if not self.scheduler.get_job(f'sensor_on_sunrise_{sensor.sensor_id}'):
                self.scheduler.add_job(
                    self._on_sunrise,
                    trigger='date',
                    kwargs={'sensor': sensor, 'sensor_config': sensor_config},
                    id=f'sensor_on_sunrise_{sensor.sensor_id}',
                    run_date=date
                )
        else:
            if not self.scheduler.get_job(f'sensor_on_sunset_{sensor.sensor_id}'):
                self.scheduler.add_job(
                    self._on_sunset,
                    trigger='date',
                    kwargs={'sensor': sensor, 'sensor_config': sensor_config},
                    id=f'sensor_on_sunset_{sensor.sensor_id}',
                    run_date=date
                )

    async def _on_sunrise(self, sensor, sensor_config):
        logging.info(f'Sensor {sensor.sensor_id} on sunrise')
        light_enable = sensor_config.config['light_enable']
        if not light_enable:
            sensor_config.config['light_enable'] = True
            await sensor_config.save()
        await self.mqtt_client.publish(
            topic=self.publish_topic,
            payload=sensor_config.model_dump_json(exclude={'id': True, 'config': {'latitude', 'longitude'}}),
            retain=True
        )

    async def _on_sunset(self, sensor, sensor_config):
        logging.info(f'Sensor {sensor.sensor_id} on sunset')
        light_enable = sensor_config.config['light_enable']
        if light_enable:
            sensor_config.config['light_enable'] = False
            await sensor_config.save()
        await self.mqtt_client.publish(
            topic=self.publish_topic,
            payload=sensor_config.model_dump_json(exclude={'id': True, 'config': {'latitude', 'longitude'}}),
            retain=True
        )
