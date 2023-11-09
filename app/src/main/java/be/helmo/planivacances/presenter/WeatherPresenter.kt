package be.helmo.planivacances.presenter

import android.util.Log
import be.helmo.planivacances.domain.WeatherForecast
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.util.ResultMessage
import be.helmo.planivacances.view.fragments.weather.WeatherFragment
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import be.helmo.planivacances.view.interfaces.IWeatherPresenter
import kotlinx.coroutines.coroutineScope

class WeatherPresenter(val groupPresenter: IGroupPresenter) : IWeatherPresenter {

    override suspend fun getForecast(): ResultMessage {
        return coroutineScope {
            try {
                val latLng = groupPresenter.getCurrentGroupPlace()?.latLngString
                    ?: return@coroutineScope ResultMessage(
                        false,
                        "Erreur lors de la récupération du lieu de météo")

                val response = ApiClient.weatherService.getForecast(latLng)

                if (response.isSuccessful && response.body() != null) {
                    val weatherData = response.body()

                    val forecastList: List<WeatherForecast> = weatherData?.forecast?.forecastday?.map { it ->
                        WeatherForecast(
                            "https:${it.day.condition.icon}",
                            "${it.day.avgtemp_c}° C",
                            it.date,
                            "Humidité : ${it.day.avghumidity}%, " +
                                    "Pluie : ${it.day.daily_chance_of_rain}%, " +
                                    "Vent : ${it.day.maxwind_kph}km/h"
                        )
                    } ?: emptyList()

                    Log.d(WeatherFragment.TAG, "Weather retrieved")

                    return@coroutineScope ResultMessage(true, forecastList)
                } else {
                    Log.e(
                        WeatherFragment.TAG,
                        "${response.message()}, ${response.isSuccessful}")

                    return@coroutineScope ResultMessage(
                        false,
                        "Erreur lors de la récupération " +
                                "des données météo ${response.message()}")
                }

            } catch (e: Exception) {
                Log.e(
                    WeatherFragment.TAG,
                    "Error while retrieving weather : ${e.message}")
                return@coroutineScope ResultMessage(
                    false,
                    "Erreur durant la récupération des données météo : ${e.message}")
            }
        }
    }
}