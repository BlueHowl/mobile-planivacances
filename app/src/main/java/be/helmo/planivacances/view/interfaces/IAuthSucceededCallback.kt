package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.factory.interfaces.IAuthCallback

interface IAuthSucceededCallback {

    fun setAuthCallback(iAuthCallback: IAuthCallback?)

}