import logging
from datetime import datetime

from api.sunrise_sunset import SunriseSunsetAPI
from databasae import find_illuminance_sensors, find_sensor_config
from model.event import Event, EventType


class SunriseSunsetScheduler:
    """
    A class to manage the scheduling of illuminance sensor configuration updates.
    """

    def __init__(self, scheduler, tz, mqtt_client, publish_topic):
        self.scheduler = scheduler
        self.tz = tz
        self.mqtt_client = mqtt_client
        self.publish_topic = publish_topic

    async def startup(self):
        sensors = await find_illuminance_sensors()
        logging.info(f"Illuminance sensors found: {len(sensors)}")
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
                    await self._on_sunrise(sensor, sensor_config, sunrise)
                    self._add_job(False, sensor, sensor_config, sunset)
                else:
                    if sunrise > now:
                        self._add_job(True, sensor, sensor_config, sunrise)
                        self._add_job(False, sensor, sensor_config, sunset)
                    else:
                        await self._on_sunset(sensor, sensor_config, sunset)
            except Exception as e:
                logging.error(f"Error updating sensor config: {e}")

        self.scheduler.add_job(self.on_schedular, trigger='cron', month='*', day='*', hour='0', minute='0', second='30')

    async def on_schedular(self):
        sensors = await find_illuminance_sensors()
        logging.info(f"Illuminance sensors found: {len(sensors)}")
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
                    kwargs={'sensor': sensor, 'sensor_config': sensor_config, 'date': date},
                    id=f'sensor_on_sunrise_{sensor.sensor_id}',
                    run_date=date
                )
        else:
            if not self.scheduler.get_job(f'sensor_on_sunset_{sensor.sensor_id}'):
                self.scheduler.add_job(
                    self._on_sunset,
                    trigger='date',
                    kwargs={'sensor': sensor, 'sensor_config': sensor_config, 'date': date},
                    id=f'sensor_on_sunset_{sensor.sensor_id}',
                    run_date=date
                )

    async def _on_sunrise(self, sensor, sensor_config, date):
        logging.info(f'Sensor {sensor.sensor_id} on sunrise')
        collect_illuminance = sensor_config.config['collect_illuminance']
        if not collect_illuminance:
            sensor_config.config['collect_illuminance'] = True
            await sensor_config.save()
        await self.mqtt_client.publish(
            topic=self.publish_topic,
            payload=Event(
                type=EventType.SUNRISE,
                sensor_id=sensor.sensor_id,
                timestamp=int(date.timestamp())
            ).model_dump_json(),
            retain=True
        )
        self.scheduler.add_job()

    async def _on_sunset(self, sensor, sensor_config, date):
        logging.info(f'Sensor {sensor.sensor_id} on sunset')
        collect_illuminance = sensor_config.config['collect_illuminance']
        if collect_illuminance:
            sensor_config.config['collect_illuminance'] = False
            await sensor_config.save()
        await self.mqtt_client.publish(
            topic=self.publish_topic,
            payload=Event(
                type=EventType.SUNSET,
                sensor_id=sensor.sensor_id,
                timestamp=int(date.timestamp())
            ).model_dump_json(),
            retain=True
        )
