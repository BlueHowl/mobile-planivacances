package be.helmo.planivacances.presenter

import android.content.SharedPreferences
import android.util.Log
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.dto.LoginUserDTO
import be.helmo.planivacances.service.dto.RegisterUserDTO
import be.helmo.planivacances.util.ResultMessage
import be.helmo.planivacances.view.fragments.AuthFragment
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class AuthPresenter() : IAuthPresenter {

    val mAuth: FirebaseAuth

    lateinit var sharedPreferences : SharedPreferences


    init {
        mAuth = FirebaseAuth.getInstance()
    }

    override fun setSharedPreference(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    override suspend fun authWithToken(idToken: String, keepConnected: Boolean): ResultMessage {
        val authResult = mAuth.signInWithCustomToken(idToken).await()
        return if (authResult != null) {
            val user = authResult.user
            val tokenTask = user?.getIdToken(true)?.await()
            if (tokenTask != null) {
                val token = tokenTask.token
                auth(token, keepConnected)
                Log.i("AuthFragment.TAG", "Successfully signed in (account token: $token)")
                ResultMessage(true, "Successfully signed in (account token: $token)")
            } else {
                val errorMessage = "Erreur lors de l'identification google"
                Log.w("AuthFragment.TAG", errorMessage)
                ResultMessage(false, errorMessage)
            }
        } else {
            // Sign-in failed
            Log.w("AuthFragment.TAG", "Failed google sign-in")
            ResultMessage(false, "Failed google sign-in")
        }
    }

    override suspend fun register(registerUser: RegisterUserDTO): ResultMessage {
        return coroutineScope {
            try {
                val response = async { ApiClient.authService.register(registerUser) }

                if (response.await().isSuccessful && response.await().body() != null) {
                    val idToken = response.await().body()
                    //auth(token, keepConnected)
                    val resultMessage = authWithToken(idToken!!, false)

                    Log.d(AuthFragment.TAG, "Register Response: $idToken")
                    return@coroutineScope ResultMessage(resultMessage.success, resultMessage.message) //ResultType.Success(token)
                } else {
                    //authProgressBar.visibility = View.GONE
                    return@coroutineScope ResultMessage(false, "Une erreur est survenue: ${response.await().message()}")
                }
            } catch (e: Exception) {
                //authProgressBar.visibility = View.GONE
                return@coroutineScope ResultMessage(false, "Une erreur est survenue: ${e.message}")
            }
        }

        //return CoroutineResult(false, "Une erreur est survenue lors de l'enregistrement")
    }

    override suspend fun login(loginUser: LoginUserDTO, keepConnected: Boolean): ResultMessage {

        return coroutineScope {
            try {
                val response = async { ApiClient.authService.login(loginUser) }

                if (response.await().isSuccessful && response.await().body() != null) {
                    val idToken = response.await().body()

                    val resultMessage = authWithToken(idToken!!, keepConnected)

                    Log.d(AuthFragment.TAG, "Login Response : $idToken")
                    return@coroutineScope ResultMessage(resultMessage.success, resultMessage.message) //ResultType.Success(token)
                } else {
                    return@coroutineScope ResultMessage(false, "Erreur lors de la connexion : ${response.await().message()}")
                }

            } catch (e: Exception) {
                Log.w("Connexion", "${e.message}")
                return@coroutineScope ResultMessage(false, "Une erreur est survenue lors de la connexion: ${e.message}")
            }
        }
    }

    override suspend fun autoAuth(): ResultMessage {
        return coroutineScope {
            val authToken = sharedPreferences.getString("Auth_Token", null)
            if(authToken != null) {
                try {
                    val response = ApiClient.authService.validateToken(authToken)

                    if (response.isSuccessful && response.body() == true) {
                        auth(authToken, true)
                        Log.d(AuthFragment.TAG, "Token local valide : $authToken")
                        return@coroutineScope ResultMessage(true, authToken)
                    } else {
                        Log.d(AuthFragment.TAG, "Token local invalide : $authToken")
                        return@coroutineScope ResultMessage(
                            false,
                            "Erreur lors de la connexion automatique ${response.message()}"
                        )
                    }

                } catch (e: Exception) {
                    return@coroutineScope ResultMessage(
                        false,
                        "Erreur lors de la connexion automatique : ${e.message}"
                    )
                }
            } else {
                return@coroutineScope ResultMessage(false, null)
            }
        }
    }

    fun auth(token: String?, keepConnected: Boolean) {
        var formattedToken = token
        if(token?.substring(0, 6) != "Bearer") {
            formattedToken = "Bearer $token"
        }
        if(keepConnected) {
            val editor = sharedPreferences.edit()
            editor.putString("Auth_Token", formattedToken)
            editor.apply()
        }

        AppSingletonFactory.instance?.setAuthToken(formattedToken)
    }

}