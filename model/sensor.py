from enum import StrEnum
from typing import List, Dict, Any

from beanie import Document, Indexed

from model.location import Location


class SensorType(StrEnum):
    THP = "thp"
    ILLUMINANCE = "illuminance"


class Sensor(Document):
    sensor_id: Indexed(str, unique=True)
    location: Location
    type: List[SensorType]

    class Settings:
        name = "sensor"


class SensorConfig(Document):
    sensor_id: Indexed(str, unique=True)
    config: Dict[str, Any]

    class Settings:
        name = "sensor_config"
