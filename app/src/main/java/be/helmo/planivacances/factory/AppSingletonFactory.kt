package be.helmo.planivacances.factory

import be.helmo.planivacances.presenter.AuthPresenter
import be.helmo.planivacances.presenter.GroupPresenter
import be.helmo.planivacances.presenter.TchatPresenter
import be.helmo.planivacances.presenter.WeatherPresenter
import be.helmo.planivacances.presenter.interfaces.*
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import be.helmo.planivacances.view.interfaces.ITchatPresenter
import be.helmo.planivacances.view.interfaces.IWeatherPresenter

/**
 * Factory de singleton + stockage du token
 */
class AppSingletonFactory() {

    val authPresenter: AuthPresenter = AuthPresenter()

    val groupPresenter: GroupPresenter = GroupPresenter()

    val weatherPresenter: WeatherPresenter = WeatherPresenter(groupPresenter)

    val tchatPresenter : TchatPresenter = TchatPresenter(groupPresenter, authPresenter)

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
    fun getWeatherPresenter(): IWeatherPresenter {
        return weatherPresenter
    }

    fun getTchatPresenter(tchatView : ITchatView) : ITchatPresenter {
        tchatPresenter.setTchatView(tchatView)
        return tchatPresenter
    }

    companion object {
        var instance: AppSingletonFactory? = null
            get() {
                if (field == null) field = AppSingletonFactory()
                return field
            }
    }
}