package be.helmo.planivacances.presenter

import be.helmo.planivacances.factory.interfaces.IAuthCallback
import be.helmo.planivacances.view.interfaces.IAuthPresenter

class AuthPresenter : IAuthPresenter {

    var authSucceedCallback: IAuthCallback? = null

    override fun authSucceded() {
        authSucceedCallback?.onAuthSucceeded()
    }

    override fun setAuthCallback(iAuthCallback: IAuthCallback?) {
        authSucceedCallback = iAuthCallback
    }
}