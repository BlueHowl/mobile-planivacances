package be.helmo.planivacances.factory

import be.helmo.planivacances.presenter.*
import be.helmo.planivacances.presenter.interfaces.*
import be.helmo.planivacances.view.interfaces.*

/**
 * Factory de singleton + stockage du token
 */
class AppSingletonFactory() {

    val authPresenter: AuthPresenter = AuthPresenter()

    val groupPresenter: GroupPresenter = GroupPresenter()

    val weatherPresenter: WeatherPresenter = WeatherPresenter(groupPresenter)

    val tchatPresenter : TchatPresenter = TchatPresenter(groupPresenter, authPresenter)

    val calendarPresenter : CalendarPresenter = CalendarPresenter(groupPresenter)

    fun getAuthPresenter(authView: IAuthView): IAuthPresenter {
        authPresenter.setIAuthView(authView)
        return authPresenter
    }

    //group presenter
    fun getGroupPresenter(groupView:IGroupView): IGroupPresenter {
        groupPresenter.setIGroupView(groupView)
        return groupPresenter
    }

    fun getGroupPresenter(createGroupView:ICreateGroupView): IGroupPresenter {
        groupPresenter.setICreateGroupView(createGroupView)
        return groupPresenter
    }

    fun getGroupPresenter(homeView:IHomeView): IGroupPresenter {
        groupPresenter.setIHomeView(homeView)
        return groupPresenter
    }

    //weather presenter
    fun getWeatherPresenter(weatherView: IWeatherView): IWeatherPresenter {
        weatherPresenter.setIWeatherView(weatherView)
        return weatherPresenter
    }

    fun getTchatPresenter(tchatView : ITchatView) : ITchatPresenter {
        tchatPresenter.setTchatView(tchatView)
        return tchatPresenter
    }
    fun getCalendarPresenter(calendarView: ICalendarView) : ICalendarPresenter {
        calendarPresenter.setCalendarView(calendarView)
        return calendarPresenter
    }

    companion object {
        var instance: AppSingletonFactory? = null
            get() {
                if (field == null) field = AppSingletonFactory()
                return field
            }
    }
}