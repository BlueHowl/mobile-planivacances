package be.helmo.planivacances.service


import be.helmo.planivacances.BuildConfig
import be.helmo.planivacances.service.dto.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherService {

    @GET("forecast.json")
    suspend fun getForecast(@Query("q") latLng: String,
                    @Query("days") days : Int = 14,
                    @Query("aqi") aqi : String = "no",
                    @Query("alerts") alerts : String = "no",
                    @Query("lang") lang : String = "fr",
                    @Query("key") api_key : String = BuildConfig.WEATHER_API_KEY): Response<WeatherData>

}