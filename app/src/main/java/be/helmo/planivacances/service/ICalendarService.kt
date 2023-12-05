package be.helmo.planivacances.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ICalendarService {
    @GET("activity/calendar/{gid}")
    suspend fun getICS(@Path("gid") gid: String): Response<String>
}