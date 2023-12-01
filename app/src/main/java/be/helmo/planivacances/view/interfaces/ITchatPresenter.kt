package be.helmo.planivacances.view.interfaces;

import be.helmo.planivacances.util.ResultMessage

interface ITchatPresenter {
    suspend fun connectToTchat()
    fun disconnectToTchat()
    fun sendMessage(message: String)
}
