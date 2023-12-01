package be.helmo.planivacances.service

import be.helmo.planivacances.service.dto.MessageDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ITchatService {
    @POST("chat/messages")
    suspend fun getPreviousMessages(@Header("GID") gid:String): Response<List<MessageDTO>>

    @POST("chat/message")
    suspend fun sendMessage(@Body message: MessageDTO)
}