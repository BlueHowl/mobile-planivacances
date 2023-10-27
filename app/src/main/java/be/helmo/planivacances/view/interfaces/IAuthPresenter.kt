package be.helmo.planivacances.view.interfaces

import android.content.SharedPreferences
import be.helmo.planivacances.service.dto.LoginUserDTO
import be.helmo.planivacances.service.dto.RegisterUserDTO
import be.helmo.planivacances.util.ResultMessage

interface IAuthPresenter {

    fun setSharedPreference(sharedPreferences: SharedPreferences)

    //suspend fun authWithToken(idToken: String?, callback: (ResultMessage) -> Unit)
    suspend fun authWithToken(idToken: String, keepConnected: Boolean): ResultMessage

    suspend fun register(registerUser: RegisterUserDTO): ResultMessage

    suspend fun login(loginUser: LoginUserDTO, keepConnected: Boolean): ResultMessage

    suspend fun autoAuth(): ResultMessage
}