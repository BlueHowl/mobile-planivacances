package be.helmo.planivacances.service

import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.service.dto.GroupAndPlaceDTO
import be.helmo.planivacances.service.dto.GroupDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IGroupService {

    @POST("group")
    suspend fun create(@Body gp: GroupAndPlaceDTO): Response<String>

    @GET("group/{gid}")
    suspend fun get(@Path("gid") gid: String): Response<Group>

    @GET("group/{gid}/list")
    suspend fun getList(@Path("gid") gid: String): Response<List<GroupDTO>>

    @PUT("group/{gid}")
    suspend fun update(
        @Path("gid") gid: String,
        @Body group: Group): Response<Group>

    @DELETE("group/{gid}")
    suspend fun update(@Path("gid") gid: String): Response<Group>
}