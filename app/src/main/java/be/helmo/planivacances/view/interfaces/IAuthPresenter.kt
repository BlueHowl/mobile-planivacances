package be.helmo.planivacances.view.interfaces

import android.content.SharedPreferences
import be.helmo.planivacances.presenter.interfaces.IAuthView

interface IAuthPresenter {

    fun setSharedPreference(sharedPreferences: SharedPreferences)

    fun getUid(): String

    fun getDisplayName() : String

    suspend fun loadIdToken(): Boolean

    suspend fun register(username: String, mail: String, password: String)

    suspend fun login(mail: String, password: String, keepConnected: Boolean)

    suspend fun autoAuth()

    suspend fun initAuthenticator(): Boolean

    fun setIAuthView(authView : IAuthView)

}