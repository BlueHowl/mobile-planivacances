package be.helmo.planivacances.service

import be.helmo.planivacances.BuildConfig
import be.helmo.planivacances.service.dto.MessageDTO
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.util.HttpChannelAuthorizer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object ApiClient {
    const val BASE_API_URL: String = "https://studapps.cg.helmo.be:5011/REST_CAO_BART/api/" //"http://192.168.1.19:8080/api/"//addr ipv4 local
    const val WEATHER_API_URL: String = "https://api.weatherapi.com/v1/"
    const val TCHAT_AUTH_URL: String = "https://studapps.cg.helmo.be:5011/REST_CAO_BART/api/chat/" //"http://192.168.1.19:8080/api/chat/"

    val gson : Gson by lazy {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .setLenient()
            .create()
    }

    val httpClient : OkHttpClient by lazy {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        OkHttpClient.Builder()
            .authenticator(TokenAuthenticator.instance!!)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
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

    val retrofitForStringResult : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_API_URL)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
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

    val tchatService : ITchatService by lazy {
        retrofit.create(ITchatService::class.java)
    }

    val calendarService : ICalendarService by lazy {
        retrofitForStringResult.create(ICalendarService::class.java)
    }

    val activityService : IActivityService by lazy {
        retrofit.create(IActivityService::class.java)
    }

    fun getTchatInstance(): Pusher {
        val headers = HashMap<String,String>()
        TokenAuthenticator.instance!!.idToken?.let { headers.put("Authorization", it) }
        val authorizer = HttpChannelAuthorizer(TCHAT_AUTH_URL)
        authorizer.setHeaders(headers)

        val options = PusherOptions()
        options.setCluster(BuildConfig.PUSHER_CLUSTER)
        options.channelAuthorizer = authorizer

        return Pusher(BuildConfig.PUSHER_KEY,options)
    }

    fun formatMessageToDisplay(message : String): MessageDTO? {
        return gson.fromJson(message,MessageDTO::class.java)
    }
}