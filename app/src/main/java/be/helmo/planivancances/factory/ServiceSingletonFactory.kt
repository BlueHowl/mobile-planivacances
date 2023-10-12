package be.helmo.planivancances.factory

import be.helmo.planivancances.presenter.AuthService
import be.helmo.planivancances.view.auth.interfaces.IAuthService

class ServiceSingletonFactory() {

    val authService: AuthService = AuthService()

    //meeting
    fun getAuthService(): IAuthService {
        return authService
    }


    companion object {
        var instance: ServiceSingletonFactory? = null
            get() {
                if (field == null) field = ServiceSingletonFactory()
                return field
            }
    }
}