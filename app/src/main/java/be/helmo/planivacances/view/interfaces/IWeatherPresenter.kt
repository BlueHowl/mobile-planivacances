package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.util.ResultMessage

interface IWeatherPresenter {

    suspend fun getForecast(): ResultMessage

}