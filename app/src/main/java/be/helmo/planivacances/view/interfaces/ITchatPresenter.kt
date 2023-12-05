package be.helmo.planivacances.view.interfaces

interface ITchatPresenter {
    suspend fun connectToTchat()
    fun disconnectToTchat()
    fun sendMessage(message: String)
}
