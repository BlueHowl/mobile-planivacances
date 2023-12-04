package be.helmo.planivacances.view.interfaces

import android.content.SharedPreferences
import be.helmo.planivacances.presenter.interfaces.IAuthView
import be.helmo.planivacances.service.IdTokenCallback
import be.helmo.planivacances.service.dto.LoginUserDTO
import be.helmo.planivacances.service.dto.RegisterUserDTO
import be.helmo.planivacances.util.ResultMessage

interface IAuthPresenter {

    fun setSharedPreference(sharedPreferences: SharedPreferences)

    fun getUid(): String

    fun getDisplayName() : String

    suspend fun loadIdToken(): Boolean

    suspend fun register(registerUser: RegisterUserDTO)

    suspend fun login(loginUser: LoginUserDTO, keepConnected: Boolean)

    suspend fun autoAuth()

    suspend fun initAuthenticator(): Boolean

    fun setIAuthView(authView : IAuthView)

}