package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.service.dto.MessageDTO

interface ITchatView {
    fun addMessageToView(message: MessageDTO)
}