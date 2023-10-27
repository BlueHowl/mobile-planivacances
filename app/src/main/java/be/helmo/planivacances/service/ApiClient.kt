package be.helmo.planivacances.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL: String = "http://192.168.159.13:8080/"//"http://192.168.0.118:8080/"  //addr ipv4 local

    val gson : Gson by lazy {
        GsonBuilder()
            .setDateFormat("dd/MM/yyyy HH:mm:ss")
            .setLenient()
            .create()
    }

    val httpClient : OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val authService : IAuthService by lazy{
        retrofit.create(IAuthService::class.java)
    }

    val groupService : IGroupService by lazy{
        retrofit.create(IGroupService::class.java)
    }
}