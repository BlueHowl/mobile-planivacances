package be.helmo.planivacances.service

import be.helmo.planivacances.service.dto.CreateGroupDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IGroupService {

    @POST("api/group")
    suspend fun create(@Header("Authorization") token: String, @Body group: CreateGroupDTO): Response<String>

}