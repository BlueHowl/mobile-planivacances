package be.helmo.planivacances.view.interfaces

import android.content.SharedPreferences
import be.helmo.planivacances.service.IdTokenCallback
import be.helmo.planivacances.service.dto.LoginUserDTO
import be.helmo.planivacances.service.dto.RegisterUserDTO
import be.helmo.planivacances.util.ResultMessage

interface IAuthPresenter {

    fun setSharedPreference(sharedPreferences: SharedPreferences)

    fun getUid(): String

    suspend fun loadIdToken(): Boolean

    suspend fun signInWithCustomToken(customToken: String): ResultMessage

    suspend fun register(registerUser: RegisterUserDTO): ResultMessage

    suspend fun login(loginUser: LoginUserDTO, keepConnected: Boolean): ResultMessage

    suspend fun autoAuth(): ResultMessage

    suspend fun auth(customToken: String?, keepConnected: Boolean): ResultMessage

    suspend fun initAuthenticator(): Boolean

}