package be.helmo.planivacances.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {
    const val BASE_API_URL: String = "http://192.168.0.25:8080/api/"//"http://192.168.147.75:8080/"  //addr ipv4 local
    const val WEATHER_API_URL: String = "https://api.weatherapi.com/v1/"

    val gson : Gson by lazy {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .setLenient()
            .create()
    }

    val httpClient : OkHttpClient by lazy {
        OkHttpClient.Builder()
            .authenticator(TokenAuthenticator.instance!!)
            .build()
    }

    val retrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val weatherRetrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(WEATHER_API_URL)
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

    val weatherService : IWeatherService by lazy{
        weatherRetrofit.create(IWeatherService::class.java)
    }
}