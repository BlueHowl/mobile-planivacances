package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.presenter.viewmodel.WeatherForecastVM

interface IWeatherView : IShowToast {

    fun onForecastLoaded(weatherList: List<WeatherForecastVM>)

}