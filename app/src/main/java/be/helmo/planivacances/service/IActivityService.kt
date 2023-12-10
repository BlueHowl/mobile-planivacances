package be.helmo.planivacances.service

import be.helmo.planivacances.service.dto.ActivityDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface IActivityService {
    @GET("activity/{gid}")
    suspend fun loadActivities(@Path("gid") gid: String): Response<Map<String,ActivityDTO>>
}