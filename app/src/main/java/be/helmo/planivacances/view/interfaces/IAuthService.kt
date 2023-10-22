package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.domain.LoginUser
import be.helmo.planivacances.domain.RegisterUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IAuthService {

    @POST("api/auth/account")
    suspend fun register(@Body user: RegisterUser): Response<String>

    @GET("api/auth/account")
    suspend fun login(@Body user: LoginUser): Response<String>
}