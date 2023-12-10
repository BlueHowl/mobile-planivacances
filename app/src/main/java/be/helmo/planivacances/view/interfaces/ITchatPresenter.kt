package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.presenter.interfaces.ITchatView

interface ITchatPresenter {

    fun setITchatView(tchatView: ITchatView)
    suspend fun connectToTchat()
    fun disconnectToTchat()
    fun sendMessage(message: String)
}
