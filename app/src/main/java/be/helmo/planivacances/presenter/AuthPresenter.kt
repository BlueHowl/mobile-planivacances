package be.helmo.planivacances.presenter

import android.content.SharedPreferences
import android.util.Log
import be.helmo.planivacances.presenter.interfaces.IAuthView
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.TokenAuthenticator
import be.helmo.planivacances.service.dto.LoginUserDTO
import be.helmo.planivacances.service.dto.RegisterUserDTO
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

/**
 * Authentification Presenter
 */
class AuthPresenter : IAuthPresenter {

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var sharedPreferences : SharedPreferences

    lateinit var authView: IAuthView

    val tokenAuthenticator: TokenAuthenticator = TokenAuthenticator.instance!!

    /**
     * Enregistrement asycnhrone d'un profil utilisateur
     * @param registerUser (RegisterUserDTO) Objet contenant le nom,
     * mail et mot de passe utilisateur
     */
    override suspend fun register(registerUser: RegisterUserDTO) {
        try {
            val response =  ApiClient.authService.register(registerUser)

            if (!response.isSuccessful || response.body() == null) {
                authView.showToast("Erreur lors de l'enregistrement : ${response.message()}", 1)
                return
            }

            val customToken = response.body()

            Log.d("AuthFragment", "Register Response: $customToken")
            auth(customToken!!, false)
        } catch (e: Exception) {
            Log.w("Erreur lors de l'enregistrement", "${e.message}")
            authView.showToast("Une erreur est survenue lors de l'enregistrement", 1)
        }
    }

    /**
     * Connexion asycnhrone à un profil utilisateur
     * @param loginUser (LoginUserDTO) Objet contenant le mail et mot de passe utilisateur
     * @param keepConnected (Boolean) stocker le token en local ?
     */
    override suspend fun login(loginUser: LoginUserDTO, keepConnected: Boolean) {
        try {
            val response = ApiClient.authService.login(loginUser)

            if (!response.isSuccessful || response.body() == null) {
                authView.showToast("Erreur lors de la connexion\n&${response.message()}", 1)

                return
            }

            val customToken = response.body()

            Log.d("AuthFragment", "Login Response : $customToken")
            auth(customToken!!, keepConnected)
        } catch (e: Exception) {
            Log.w("Erreur lors de la connexion", "${e.message}")
            authView.showToast("Une erreur est survenue lors de la connexion", 1)
        }
    }

    /**
     * Connexion asynchrone automatique sur base du potentiel token d'identification sauvegardé
     */
    override suspend fun autoAuth() {
        val customToken = sharedPreferences.getString("CustomToken", null)
        if(customToken == null) {
            authView.showToast("Authentification automatique impossible", 0)
            return
        }
        auth(customToken, true)

    }

    /**
     * Sauvegarde le resfresh token si demandé et précise le token au TokenAuthenticator
     * @param token (String) refreshToken
     * @param keepConnected (Boolean) stocker le token en local ?
     */
    suspend fun auth(customToken: String, keepConnected: Boolean) {
        if(keepConnected) {
            val editor = sharedPreferences.edit()
            editor.putString("CustomToken", customToken)
            editor.apply()
        }

        val authResult = mAuth.signInWithCustomToken(customToken).await()

        if (authResult != null) {
            initAuthenticator()

            authView.goToHome()

            return
        }

        authView.showToast("Erreur lors de l'authentification", 1)
    }

    /**
     * load a new idToken in the TokenAuthenticator
     * @return (Boolean)
     */
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

    /**
     * initialize the authenticator
     * @return (Boolean)
     */
    override suspend fun initAuthenticator(): Boolean {
        tokenAuthenticator.authPresenter = this //todo setters ou pas en kotlin ?
        return loadIdToken()
    }


    /**
     * Get user id
     * @return (String)
     */
    override fun getUid(): String {
        return mAuth.uid!!
    }

    /**
     * get the name of the user
     * @return (String)
     */
    override fun getDisplayName(): String {
        return mAuth.currentUser!!.displayName!!;
    }


    /**
     * assigne le sharedPreferences à la variable locale du presenter
     * @param sharedPreferences (SharedPreferences)
     */
    override fun setSharedPreference(sharedPreferences: SharedPreferences) {
        this.sharedPreferences = sharedPreferences
    }

    /**
     * Assigne la AuthView Interface
     */
    override fun setIAuthView(authView : IAuthView) {
        this.authView = authView
    }

}