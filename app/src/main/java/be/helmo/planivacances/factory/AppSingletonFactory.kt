package be.helmo.planivacances.factory

import be.helmo.planivacances.presenter.AuthPresenter
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import be.helmo.planivacances.view.interfaces.IAuthSucceededCallback

class AppSingletonFactory() {

    var token: String? = null

    val authPresenter: AuthPresenter = AuthPresenter()

    fun getAuthToken(): String? {
        return token
    }

    fun setAuthToken(token: String?) {
        this.token = token
    }

    fun getAuthPresenter(): IAuthPresenter {
        return authPresenter
    }

    fun getAuthSucceededCallback(): IAuthSucceededCallback {
        return authPresenter
    }

    companion object {
        var instance: AppSingletonFactory? = null
            get() {
                if (field == null) field = AppSingletonFactory()
                return field
            }
    }
}