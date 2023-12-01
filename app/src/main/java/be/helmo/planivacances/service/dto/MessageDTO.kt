package be.helmo.planivacances.service.dto

data class MessageDTO(
    var sender : String,
    val displayName: String,
    val groupId : String,
    val content: String,
    val time: Long
                      )