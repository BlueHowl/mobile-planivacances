package be.helmo.planivacances.service

import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.service.dto.PlaceDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface IPlaceService {

    @POST("api/place/{gid}")
    suspend fun create(
        @Header("Authorization") token: String,
        @Body place: PlaceDTO,
        @Path("gid") gid: String): Response<String>

    @GET("api/place/{gid}/{pid}")
    suspend fun getGroupPlace(
        @Header("Authorization") token: String,
        @Path("gid") gid: String,
        @Path("pid") pid: String): Response<Place>

    @GET("api/place/{gid}")
    suspend fun getPlaces(
        @Header("Authorization") token: String,
        @Path("gid") gid: String): Response<List<Place>>

    @DELETE("api/place/{gid}/{pid}")
    suspend fun deletePlace(
        @Header("Authorization") token: String,
        @Path("gid") gid: String,
        @Path("pid") pid: String): Response<String>

}