package be.helmo.planivacances

import be.helmo.planivacances.view.interfaces.IAuthService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL: String = "http://192.168.0.118:8080/"  //addr ipv4 local

    val gson : Gson by lazy {
        GsonBuilder().setLenient().create()
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
}