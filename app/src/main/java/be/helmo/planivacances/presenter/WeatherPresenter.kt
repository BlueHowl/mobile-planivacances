package be.helmo.planivacances.presenter

import android.util.Log
import be.helmo.planivacances.domain.WeatherForecast
import be.helmo.planivacances.presenter.interfaces.IWeatherView
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import be.helmo.planivacances.view.interfaces.IWeatherPresenter

class WeatherPresenter(val groupPresenter: IGroupPresenter) : IWeatherPresenter {

    lateinit var weatherView: IWeatherView

    /**
     * get the weather forecast for the current group place
     */
    override suspend fun getForecast() {
        try {
            val latLng = groupPresenter.getCurrentGroupPlace()?.latLngString
            if(latLng == null) {
                weatherView.showToast(
                    "Erreur lors de la récupération du lieu de météo",
                    1
                )

                return
            }

            val response = ApiClient.weatherService.getForecast(latLng)

            if (!response.isSuccessful || response.body() == null) {
                Log.e("WeatherFragment","${response.message()}, ${response.isSuccessful}")

                weatherView.showToast("Erreur lors de la récupération " +
                        "des données météo ${response.message()}", 1)
            }

            val weatherData = response.body()

            val forecastList = weatherData?.forecast?.forecastday?.map { it ->
                WeatherForecast(
                    "https:${it.day.condition.icon}",
                    "${it.day.avgtemp_c}° C",
                    it.date,
                    "Humidité : ${it.day.avghumidity}%, " +
                            "Pluie : ${it.day.daily_chance_of_rain}%, " +
                            "Vent : ${it.day.maxwind_kph}km/h"
                )
            } ?: emptyList()

            Log.d("WeatherFragment", "Weather retrieved")

            weatherView.onForecastLoaded(forecastList)
        } catch (e: Exception) {
            Log.e("WeatherFragment",
                "Error while retrieving weather : ${e.message}")

            weatherView.showToast(
                "Erreur durant la récupération des données météo",
                1
            )
        }
    }

    /**
     * Assigne la WeatherView Interface
     */
    override fun setIWeatherView(weatherView: IWeatherView) {
        this.weatherView = weatherView
    }
}