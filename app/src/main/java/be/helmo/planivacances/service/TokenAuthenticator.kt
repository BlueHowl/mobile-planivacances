package be.helmo.planivacances.service

import android.util.Log
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class TokenAuthenticator : Authenticator {

    //var refreshToken: String? = null
    var idToken: String? = null

    lateinit var authPresenter: IAuthPresenter

    //var token: String? = null // Initialize with your actual initial token
    val requestLimit = 6 // Maximum allowed requests within a specified period
    val requestLimitPeriodMillis = TimeUnit.SECONDS.toMillis(10) // 10 seconds
    val requestCounter = AtomicLong(0)
    var lastRequestTime = 0L

    override fun authenticate(route: Route?, response: Response): Request? {
        synchronized(this) {
            val currentTime = System.currentTimeMillis()

            // Reset request counter if a new period has started
            if (currentTime - lastRequestTime > requestLimitPeriodMillis) {
                requestCounter.set(0)
            }
            if (requestCounter.get() < requestLimit) {
                // Allow the request and increment the counter
                requestCounter.incrementAndGet()
                lastRequestTime = currentTime
                /*if (refreshToken != null) {
                    //todo runBlocking ?
                    GlobalScope.launch {
                        authPresenter.signInWithCustomToken(refreshToken!!)
                    }
                }*/
                runBlocking {
                    authPresenter.loadIdToken()
                }

                return if(idToken != null) {
                    return response.request().newBuilder()
                        .header("Authorization", idToken!!)
                        .build()
                } else {
                    Log.d("TokenAuthenticator", "Token is null, not adding Authorization header to the request")
                    null
                }
            } else {
                // Too many requests, handle accordingly (e.g., wait, throw an exception, etc.)
                Log.d("TokenAuthenticator", "Too many requests. Please wait.")
                return null
            }
        }
    }

    companion object {
        var instance: TokenAuthenticator? = null
            get() {
                if (field == null) field = TokenAuthenticator()
                return field
            }
    }
}
