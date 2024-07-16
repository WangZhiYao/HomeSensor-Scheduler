import logging
from datetime import datetime

import requests
from requests import HTTPError


class SunriseSunsetAPI:
    """
    A class to interact with the sunrise-sunset API.
    """

    @staticmethod
    def get_sunrise_sunset(lat: float, lng: float, date: str, tz: str) -> tuple[datetime, datetime]:
        """
        Fetches sunrise and sunset times for a given latitude, longitude and date.

        Args:
            lat: The latitude.
            lng: The longitude.
            date: The date in YYYY-MM-DD format.
            tz: The timezone to use.

        Returns:
            A tuple containing the sunrise and sunset times as datetime objects.

        Raises:
            HTTPError: If there is an error with the API request.
            Exception: If there is an unexpected error.
        """
        logging.info(f"Fetching sunrise and sunset data for: {lat}, {lng}, {date}")
        url = f'https://api.sunrise-sunset.org/json?lat={lat}&lng={lng}&date={date}&formatted=0&tzid={tz}'
        try:
            response = requests.get(url)
            response.raise_for_status()
            data = response.json()
            logging.info(f"API response: {data}")
            sunrise = datetime.fromisoformat(data['results']['sunrise'])
            sunset = datetime.fromisoformat(data['results']['sunset'])
            return sunrise, sunset
        except HTTPError as http_err:
            logging.error(f"HTTP error occurred while fetching data: {http_err}")
            raise
        except Exception as err:
            logging.error(f"An unexpected error occurred while fetching data: {err}")
            raise
