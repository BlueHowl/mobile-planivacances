package be.helmo.planivacances.service

import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.service.dto.GroupAndPlaceDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IGroupService {

    @POST("api/group")
    suspend fun create(
        @Header("Authorization") token: String,
        @Body gp: GroupAndPlaceDTO): Response<String>

    @GET("api/group/{gid}")
    suspend fun get(
        @Header("Authorization") token: String,
        @Path("gid") gid: String): Response<Group>

    @GET("api/group/list")
    suspend fun getList(
        @Header("Authorization") token: String,
        @Path("gid") gid: String): Response<List<Group>>

    @PUT("api/group/{gid}")
    suspend fun update(
        @Header("Authorization") token: String,
        @Path("gid") gid: String,
        @Body group: Group): Response<Group>

    @DELETE("api/group/{gid}")
    suspend fun update(
        @Header("Authorization") token: String,
        @Path("gid") gid: String): Response<Group>
}