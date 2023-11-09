package be.helmo.planivacances.presenter

import android.util.Log
import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.dto.CreateGroupDTO
import be.helmo.planivacances.service.dto.GroupAndPlaceDTO
import be.helmo.planivacances.service.dto.PlaceDTO
import be.helmo.planivacances.util.ResultMessage
import be.helmo.planivacances.view.fragments.CreateGroupFragment
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope

class GroupPresenter : IGroupPresenter {

    var groups : HashMap<String, Group> = HashMap()

    lateinit var currentGid : String

    override suspend fun createGroup(createGroup: CreateGroupDTO, placeDTO: PlaceDTO): ResultMessage {
        return coroutineScope {
            try {
                val response = ApiClient.groupService.create(AppSingletonFactory.instance?.getAuthToken()!!, GroupAndPlaceDTO(createGroup, placeDTO))

                if (response.isSuccessful && response.body() != null) {
                    val gid = response.body()!!

                    currentGid = gid

                    groups[gid] = Group(createGroup.groupName,
                        createGroup.description,
                        createGroup.startDate,
                        createGroup.endDate,
                        Place(placeDTO.address, LatLng(placeDTO.lat, placeDTO.lon)),
                        createGroup.isPublished,
                        FirebaseAuth.getInstance().uid!!)

                    Log.d(CreateGroupFragment.TAG, "Group created : $gid")
                    return@coroutineScope ResultMessage(true, "Groupe créé")
                } else {
                    Log.d(CreateGroupFragment.TAG, "${response.message()}, ${response.isSuccessful}, ${AppSingletonFactory.instance?.getAuthToken()!!}")
                    return@coroutineScope ResultMessage(false, "Erreur lors de la création du groupe ${response.message()}")
                }

            } catch (e: Exception) {
                return@coroutineScope ResultMessage(false, "Erreur durant la création du groupe : ${e.message}")
            }
        }
    }

    override fun getCurrentGroup(): Group? {
        return groups[currentGid]
    }

    override fun getCurrentGroupPlace(): Place? {
        return groups[currentGid]?.place
    }
}