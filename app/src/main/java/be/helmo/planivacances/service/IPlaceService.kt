package be.helmo.planivacances.service

import be.helmo.planivacances.service.dto.PlaceDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IPlaceService {

    @POST("place/{gid}")
    suspend fun create(
        @Body place: PlaceDTO,
        @Path("gid") gid: String): Response<String>

    @GET("place/{gid}/{pid}")
    suspend fun getGroupPlace(
        @Path("gid") gid: String,
        @Path("pid") pid: String): Response<PlaceDTO>

    @GET("place/{gid}")
    suspend fun getPlaces(@Path("gid") gid: String): Response<List<PlaceDTO>>

    @DELETE("place/{gid}/{pid}")
    suspend fun deletePlace(
        @Path("gid") gid: String,
        @Path("pid") pid: String): Response<String>

}