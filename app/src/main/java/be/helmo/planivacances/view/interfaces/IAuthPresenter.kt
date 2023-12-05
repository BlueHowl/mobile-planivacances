package be.helmo.planivacances.view.interfaces

import android.content.SharedPreferences
import be.helmo.planivacances.presenter.interfaces.IAuthView
import be.helmo.planivacances.domain.LoginUser
import be.helmo.planivacances.domain.RegisterUser

interface IAuthPresenter {

    fun setSharedPreference(sharedPreferences: SharedPreferences)

    fun getUid(): String

    fun getDisplayName() : String

    suspend fun loadIdToken(): Boolean

    suspend fun register(registerUser: RegisterUser)

    suspend fun login(loginUser: LoginUser, keepConnected: Boolean)

    suspend fun autoAuth()

    suspend fun initAuthenticator(): Boolean

    fun setIAuthView(authView : IAuthView)

}