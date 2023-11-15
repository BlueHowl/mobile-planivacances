package be.helmo.planivacances.presenter

import android.util.Log
import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.TokenAuthenticator
import be.helmo.planivacances.service.dto.CreateGroupDTO
import be.helmo.planivacances.service.dto.GroupAndPlaceDTO
import be.helmo.planivacances.service.dto.GroupDTO
import be.helmo.planivacances.service.dto.PlaceDTO
import be.helmo.planivacances.util.ResultMessage
import be.helmo.planivacances.view.fragments.CreateGroupFragment
import be.helmo.planivacances.view.fragments.GroupFragment
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope

class GroupPresenter : IGroupPresenter {

    var groups : HashMap<String, GroupDTO> = HashMap()

    lateinit var currentGid : String

    override suspend fun createGroup(createGroup: CreateGroupDTO, placeDTO: PlaceDTO): ResultMessage {
        return coroutineScope {
            try {
                val response = ApiClient.groupService.create(GroupAndPlaceDTO(createGroup, placeDTO))//AppSingletonFactory.instance?.getAuthToken()!!,

                if (response.isSuccessful && response.body() != null) {
                    val gid = response.body()!!

                    currentGid = gid

                    groups[gid] = GroupDTO(gid,
                        createGroup.groupName,
                        createGroup.description,
                        createGroup.startDate,
                        createGroup.endDate,
                        PlaceDTO(placeDTO.address, placeDTO.lat, placeDTO.lon),
                        //Place(placeDTO.address, LatLng(placeDTO.lat, placeDTO.lon)),
                        createGroup.isPublished,
                        FirebaseAuth.getInstance().uid!!)

                    Log.d(CreateGroupFragment.TAG, "Group created : $gid")
                    return@coroutineScope ResultMessage(true, "Groupe créé")
                } else {
                    Log.d(CreateGroupFragment.TAG, "${response.message()}, ${response.isSuccessful}")
                    return@coroutineScope ResultMessage(false, "Erreur lors de la création du groupe ${response.message()}")
                }

            } catch (e: Exception) {
                return@coroutineScope ResultMessage(false, "Erreur durant la création du groupe : ${e.message}")
            }
        }
    }

    override suspend fun loadUserGroups(uid: String): ResultMessage {

        return coroutineScope {
            try {
                val response = ApiClient.groupService.getList(uid)

                if (response.isSuccessful && response.body() != null) {
                    val groupsDto = response.body()!!

                    for(groupDto in groupsDto) {
                        groups[groupDto.gid] = groupDto
                    }

                    Log.d(GroupFragment.TAG, "Groups retrieved : ${groups.size}")
                    return@coroutineScope ResultMessage(true, "Groupes chargés avec succès")
                } else {
                    Log.d(GroupFragment.TAG, "${response.message()}, ${response.isSuccessful}")
                    return@coroutineScope ResultMessage(false, "Erreur lors de la récupération des groupes ${response.message()}")
                }

            } catch (e: Exception) {
                return@coroutineScope ResultMessage(false, "Erreur durant la récupération des groupes : ${e.message}")
            }
        }
    }

    override fun getGroups(): List<GroupDTO> {
        return groups.values.toList()
    }

    override fun getCurrentGroup(): GroupDTO? {
        return groups[currentGid]
    }

    override fun getCurrentGroupPlace(): Place {
        val pDto = groups[currentGid]?.place //TODO diretly get LAtLng object from api
        return Place(pDto!!.address, LatLng(pDto.lat, pDto.lon))
    }

    override fun setCurrentGroupId(gid: String) {
        currentGid = gid
    }
}