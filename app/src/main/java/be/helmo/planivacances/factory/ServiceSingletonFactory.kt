package be.helmo.planivacances.factory

import be.helmo.planivacances.presenter.AuthService
import be.helmo.planivacances.view.auth.interfaces.IAuthService

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