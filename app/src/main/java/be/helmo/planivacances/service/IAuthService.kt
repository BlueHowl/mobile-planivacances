package be.helmo.planivacances.service

import be.helmo.planivacances.service.dto.LoginUserDTO
import be.helmo.planivacances.service.dto.RegisterUserDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface IAuthService {

    @POST("api/auth/register")
    suspend fun register(@Body user: RegisterUserDTO): Response<String>

    @POST("api/auth/login")
    suspend fun login(@Body user: LoginUserDTO): Response<String>

    @GET("api/auth/token")
    suspend fun validateToken(@Header("Authorization") token: String): Response<Boolean>
}