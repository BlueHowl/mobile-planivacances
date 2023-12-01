package be.helmo.planivacances.service

import be.helmo.planivacances.service.dto.GroupDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IGroupService {

    @POST("group")
    suspend fun create(@Body gp: GroupDTO): Response<String>

    @GET("group/{gid}")
    suspend fun get(@Path("gid") gid: String): Response<GroupDTO>

    @GET("group/list")
    suspend fun getList(): Response<List<GroupDTO>>

    @PUT("group/{gid}")
    suspend fun update(
        @Path("gid") gid: String,
        @Body group: GroupDTO): Response<String>

    @DELETE("group/{gid}")
    suspend fun delete(@Path("gid") gid: String): Response<String>
}