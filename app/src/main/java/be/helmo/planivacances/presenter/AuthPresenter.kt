package be.helmo.planivacances.presenter

import android.content.SharedPreferences
import android.util.Log
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.TokenAuthenticator
import be.helmo.planivacances.service.dto.LoginUserDTO
import be.helmo.planivacances.service.dto.RegisterUserDTO
import be.helmo.planivacances.util.ResultMessage
import be.helmo.planivacances.view.fragments.AuthFragment
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

/**
 * Authentification Presenter
 */
class AuthPresenter : IAuthPresenter {

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var sharedPreferences : SharedPreferences

    val tokenAuthenticator: TokenAuthenticator = TokenAuthenticator.instance!!

    /**
     * assigne le sharedPreferences à la variable locale du presenter
     * @param sharedPreferences (SharedPreferences)
     */
    override fun setSharedPreference(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    /**
     * Authentification asynchrone par token, récupère le token d'identification
     * sur base d'un customToken
     * @param customToken (String) customToken
     * @return (ResultMessage) Message de résultat
     */
    override suspend fun signInWithCustomToken(customToken: String): ResultMessage {
        val authResult = mAuth.signInWithCustomToken(customToken).await()
        return if (authResult != null) {
            Log.i("AuthFragment.TAG", "Successfully signed in")
            ResultMessage(true, "Authentification réussie")
        } else {
            // Sign-in failed
            Log.w("AuthFragment.TAG", "Failed to retrieve new id token")
            ResultMessage(false, "Failed to retrieve new id token")
        }
    }

    /**
     * Enregistrement asycnhrone d'un profil utilisateur
     * @param registerUser (RegisterUserDTO) Objet contenant le nom,
     * mail et mot de passe utilisateur
     * @return (ResultMessage) Message de résultat
     */
    override suspend fun register(registerUser: RegisterUserDTO): ResultMessage {
        return coroutineScope {
            try {
                val response = async { ApiClient.authService.register(registerUser) }

                if (response.await().isSuccessful && response.await().body() != null) {
                    val customToken = response.await().body()

                    Log.d(AuthFragment.TAG, "Register Response: $customToken")
                    return@coroutineScope auth(customToken, false)
                } else {
                    return@coroutineScope ResultMessage(false,
                        "Une erreur est survenue: ${response.await().message()}")
                }
            } catch (e: Exception) {
                return@coroutineScope ResultMessage(false,
                    "Une erreur est survenue: ${e.message}")
            }
        }
    }

    /**
     * Connexion asycnhrone à un profil utilisateur
     * @param loginUser (LoginUserDTO) Objet contenant le mail et mot de passe utilisateur
     * @param keepConnected (Boolean) stocker le token en local ?
     * @return (ResultMessage) Message de résultat
     */
    override suspend fun login(loginUser: LoginUserDTO, keepConnected: Boolean): ResultMessage {

        return coroutineScope {
            try {
                val response = async { ApiClient.authService.login(loginUser) }

                if (response.await().isSuccessful && response.await().body() != null) {
                    val customToken = response.await().body()

                    Log.d(AuthFragment.TAG, "Login Response : $customToken")
                    return@coroutineScope auth(customToken, keepConnected)
                } else {
                    return@coroutineScope ResultMessage(false,
                        "Erreur lors de la connexion : ${response.await().message()}")
                }

            } catch (e: Exception) {
                Log.w("Erreur lors de la connexion", "${e.message}")
                return@coroutineScope ResultMessage(false,
                    "Une erreur est survenue lors de la connexion: ${e.message}")
            }
        }
    }

    /**
     * Connexion asynchrone automatique sur base du potentiel token d'identification sauvegardé
     * @return (ResultMessage) Message de résultat
     */
    override suspend fun autoAuth(): ResultMessage {
        val customToken = sharedPreferences.getString("RefreshToken", null)
            ?: return ResultMessage(false, null)

        return auth(customToken, true)
    }

    override suspend fun loadIdToken(): Boolean {
        val tokenTask = mAuth.currentUser?.getIdToken(false)?.await()

        return if (tokenTask != null) {
            val token = "Bearer ${tokenTask.token}"
            tokenAuthenticator.idToken = token
            Log.i("AuthFragment.TAG", "Successfully retrieved new account token: $token")
            true
        } else {
            val errorMessage = "Erreur lors de récupération d'un nouveau jeton d'identification"
            Log.w("AuthFragment.TAG", errorMessage)
            false
        }
    }

    override fun getUid(): String {
        return mAuth.uid!!
    }

    override fun getDisplayName(): String {
        return mAuth.currentUser!!.displayName!!;
    }

    /**
     * Sauvegarde le resfresh token si demandé et précise le token au TokenAuthenticator
     * @param token (String) refreshToken
     * @param keepConnected (Boolean) stocker le token en local ?
     */
    override suspend fun auth(customToken: String?, keepConnected: Boolean): ResultMessage {
        if(keepConnected) {
            val editor = sharedPreferences.edit()
            editor.putString("RefreshToken", customToken)
            editor.apply()
        }

        val result = signInWithCustomToken(customToken!!)

        if(result.success) {
            //tokenAuthenticator.refreshToken = customToken
            initAuthenticator()

            return ResultMessage(true, result.message)
        }

        return ResultMessage(false, result.message)

    }

    override suspend fun initAuthenticator(): Boolean {
        tokenAuthenticator.authPresenter = this //todo setters ou pas en kotlin ?
        return loadIdToken()
    }

}