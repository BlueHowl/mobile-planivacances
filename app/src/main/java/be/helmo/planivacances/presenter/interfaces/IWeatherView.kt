package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.domain.WeatherForecast

interface IWeatherView : IShowToast {

    fun onForecastLoaded(weatherList: List<WeatherForecast>)

}