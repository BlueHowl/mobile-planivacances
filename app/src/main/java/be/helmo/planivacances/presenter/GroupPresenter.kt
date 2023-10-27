package be.helmo.planivacances.presenter

import android.util.Log
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.dto.CreateGroupDTO
import be.helmo.planivacances.util.ResultMessage
import be.helmo.planivacances.view.fragments.CreateGroupFragment
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import kotlinx.coroutines.coroutineScope

class GroupPresenter : IGroupPresenter {

    override suspend fun createGroup(group: CreateGroupDTO): ResultMessage {
        return coroutineScope {
            try {
                val response = ApiClient.groupService.create(AppSingletonFactory.instance?.getAuthToken()!!, group)

                if (response.isSuccessful && response.body() != null) {
                    val delay = response.body()
                    Log.d(CreateGroupFragment.TAG, "Group created : $delay")
                    return@coroutineScope ResultMessage(true, "Groupe créé")
                } else {
                    Log.d(CreateGroupFragment.TAG, "${response.message()}, ${response.isSuccessful()}, ${AppSingletonFactory.instance?.getAuthToken()!!}")
                    return@coroutineScope ResultMessage(false, "Erreur lors de la création du groupe ${response.message()}")
                }

            } catch (e: Exception) {
                return@coroutineScope ResultMessage(false, "Erreur durant la création du groupe : ${e.message}")
            }
        }
    }
}