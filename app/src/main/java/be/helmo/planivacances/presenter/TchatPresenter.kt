package be.helmo.planivacances.presenter

import android.util.Log
import be.helmo.planivacances.presenter.interfaces.ITchatView
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.dto.MessageDTO
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import be.helmo.planivacances.view.interfaces.ITchatPresenter
import com.pusher.client.Pusher
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class TchatPresenter(private val groupPresenter: IGroupPresenter,
                     private val authPresenter: IAuthPresenter) : ITchatPresenter {
    private var tchatView : ITchatView? = null
    private var tchatService : Pusher? = null
    private var channel: PrivateChannel? = null
    private var previousMessageLoaded : Boolean = false


    fun setTchatView(tchatView: ITchatView) {
        this.tchatView = tchatView
    }


    override suspend fun connectToTchat() {
        tchatService = ApiClient.getTchatInstance()
        tchatService?.connect(object:com.pusher.client.connection.ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) {
                if (change != null) {
                    Log.d("Tchat Presenter : ",
                        "Etat de la connexion : ${change.currentState}")
                    if(change.currentState == ConnectionState.CONNECTED) {
                        subscribeToGroupChannel()
                    }
                }
            }
            override fun onError(message: String?, code: String?, e: Exception?) {
                Log.d("Tchat presenter : ",
                    "Erreur durant la connexion au tchat : $message")
            }
        })
    }

    private fun subscribeToGroupChannel() {
        channel = tchatService?.subscribePrivate(
            "private-${groupPresenter.getCurrentGroupId()}",
                        object:PrivateChannelEventListener {

            override fun onEvent(event: PusherEvent?) {}

            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.d("Tchat presenter : ","Suscription ok => $channelName")
                bindToEvents()
                CoroutineScope(Dispatchers.Main).launch {
                    loadPreviousMessages()
                }
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                Log.d("Tchat presenter","Suscription failed : $message")
            }
        })
    }

    private fun bindToEvents() {
        channel?.bind("new_messages",object:PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                if(previousMessageLoaded) {
                    Log.d("Tchat presenter : ", "${event?.eventName} => ${event?.data}")
                    if (event?.data != null) {
                        val messageDTO : MessageDTO? = ApiClient.formatMessageToDisplay(event.data)
                        if(messageDTO != null) {
                            sendMessageToView(messageDTO)
                        }
                    }
                }
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
            }

        })
    }

    private suspend fun loadPreviousMessages() {
        val response = ApiClient
            .tchatService
            .getPreviousMessages(groupPresenter.getCurrentGroupId())

        if(response.isSuccessful && response.body() != null) {
            val messages : List<MessageDTO> = response.body()!!

            for(message in messages) {
                sendMessageToView(message)
            }
            previousMessageLoaded = true
        }
    }

    private fun sendMessageToView(message: MessageDTO) {
        if(message.sender == authPresenter.getUid()) {
            message.sender = "me"
        } else {
            message.sender = "other"
        }
        tchatView?.addMessageToView(message)
    }

    override fun sendMessage(message: String) {
        if(message.isNotEmpty()) {

            val messageDTO = MessageDTO(
                authPresenter.getUid(),
                authPresenter.getDisplayName(),
                groupPresenter.getCurrentGroupId(),
                message,
                System.currentTimeMillis()
            )

            CoroutineScope(Dispatchers.Main).launch {
                ApiClient.tchatService.sendMessage(messageDTO)
            }
        }
    }

    override fun disconnectToTchat() {
        channel?.unbind("new_messages",object:PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {}

            override fun onSubscriptionSucceeded(channelName: String?) {}

            override fun onAuthenticationFailure(message: String?, e: Exception?) {}
        })
        tchatService?.unsubscribe("private-${groupPresenter.getCurrentGroupId()}")
        tchatService?.disconnect()
    }
}