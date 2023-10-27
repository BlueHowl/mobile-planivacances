package be.helmo.planivacances.factory

import be.helmo.planivacances.presenter.AuthPresenter
import be.helmo.planivacances.presenter.GroupPresenter
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import be.helmo.planivacances.view.interfaces.IGroupPresenter

class AppSingletonFactory() {

    var token: String? = null

    val authPresenter: AuthPresenter = AuthPresenter()

    val groupPresenter: GroupPresenter = GroupPresenter()

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

    companion object {
        var instance: AppSingletonFactory? = null
            get() {
                if (field == null) field = AppSingletonFactory()
                return field
            }
    }
}