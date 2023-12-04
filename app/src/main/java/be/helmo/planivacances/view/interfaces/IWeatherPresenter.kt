package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.presenter.interfaces.IWeatherView

interface IWeatherPresenter {

    suspend fun getForecast()

    fun setIWeatherView(weatherView: IWeatherView)

}