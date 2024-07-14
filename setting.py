from pydantic_settings import (
    BaseSettings,
    SettingsConfigDict,
)


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file='.env', env_file_encoding='utf-8')

    mqtt_host: str
    mqtt_port: int
    mqtt_client_id: str
    mqtt_username: str
    mqtt_password: str
    mqtt_publish_topic: str

    mongo_url: str
    tz: str


settings = Settings()
