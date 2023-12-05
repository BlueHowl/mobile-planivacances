package be.helmo.planivacances.service

import be.helmo.planivacances.domain.LoginUser
import be.helmo.planivacances.domain.RegisterUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IAuthService {

    @POST("auth/register")
    suspend fun register(@Body user: RegisterUser): Response<String>

    @POST("auth/login")
    suspend fun login(@Body user: LoginUser): Response<String>

    @POST("auth/token")
    suspend fun validateToken(@Header("Authorization") token: String): Response<Boolean>
}