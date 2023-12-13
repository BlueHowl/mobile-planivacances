package be.helmo.planivacances.service

import be.helmo.planivacances.service.dto.ActivityDTO
import be.helmo.planivacances.service.dto.GroupDTO
import retrofit2.Response
import retrofit2.http.*

interface IActivityService {
    @GET("activity/{gid}")
    suspend fun loadActivities(@Path("gid") gid: String): Response<Map<String,ActivityDTO>>

    @POST("activity/{gid}")
    suspend fun createActivity(@Path("gid") gid:String, @Body activityDTO: ActivityDTO): Response<String>

    @DELETE("activity/{gid}/{aid}")
    suspend fun deleteActivity(@Path("gid") gid: String,@Path("aid") aid:String) : Response<String>
}