package be.helmo.planivacances.factory

import be.helmo.planivacances.presenter.AuthPresenter
import be.helmo.planivacances.presenter.GroupPresenter
import be.helmo.planivacances.presenter.WeatherPresenter
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import be.helmo.planivacances.view.interfaces.IWeatherPresenter

/**
 * Factory de singleton + stockage du token
 */
class AppSingletonFactory() {

    var token: String? = null

    val authPresenter: AuthPresenter = AuthPresenter()

    val groupPresenter: GroupPresenter = GroupPresenter()

    val weatherPresenter: WeatherPresenter = WeatherPresenter(groupPresenter)

    //auth token
    fun getAuthToken(): String? {
        return token
    }

    fun setAuthToken(token: String?) {
        this.token = token
    }

    //auth presenter
    fun getAuthPresenter(): IAuthPresenter {
        return authPresenter
    }

    //group presenter
    fun getGroupPresenter(): IGroupPresenter {
        return groupPresenter
    }

    //weather presenter
    fun getWeatherPresenter(): IWeatherPresenter {
        return weatherPresenter;
    }

    companion object {
        var instance: AppSingletonFactory? = null
            get() {
                if (field == null) field = AppSingletonFactory()
                return field
            }
    }
}