package be.helmo.planivacances.presenter

import android.util.Log
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.presenter.interfaces.ICreateGroupView
import be.helmo.planivacances.presenter.interfaces.IGroupView
import be.helmo.planivacances.presenter.interfaces.IHomeView
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.dto.GroupDTO
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth

class GroupPresenter : IGroupPresenter {
    lateinit var groupView: IGroupView
    lateinit var createGroupView: ICreateGroupView
    lateinit var homeView: IHomeView
    var groups : HashMap<String, GroupDTO> = HashMap()

    lateinit var currentGid : String

    override suspend fun createGroup(group: GroupDTO) {

        try {
            val response = ApiClient.groupService.create(group)

            if (response.isSuccessful && response.body() != null) {
                val gid = response.body()!!

                currentGid = gid

                group.gid = gid
                group.owner = FirebaseAuth.getInstance().uid!!
                groups[gid] = group

                Log.d("CreateGroupFragment", "Group created : $gid")
                createGroupView.onGroupCreated()
            } else {
                Log.d("CreateGroupFragment", "${response.message()}, ${response.isSuccessful}")
                createGroupView.showToast("Erreur lors de la création du groupe ${response.message()}")
            }

        } catch (e: Exception) {
            createGroupView.showToast("Erreur durant la création du groupe")
        }

    }

    override suspend fun loadUserGroups() {
        try {
            val response = ApiClient.groupService.getList()

            if (response.isSuccessful && response.body() != null) {
                val groupsDto = response.body()!!

                for(groupDto in groupsDto) {
                    groups[groupDto.gid!!] = groupDto
                }

                Log.d("GroupFragment", "Groups retrieved : ${groups.size}")
                homeView.onGroupsLoaded()
            } else {
                Log.d("GroupFragment", "${response.message()}, ${response.isSuccessful}")
                homeView.showToast("Erreur lors de la récupération des groupes ${response.message()}")
            }

        } catch (e: Exception) {
            homeView.showToast("Erreur durant la récupération des groupes")
        }

    }

    override fun getGroups(): List<GroupDTO> {
        return groups.values.toList()
    }

    override fun getCurrentGroup(): GroupDTO? {
        return groups[currentGid]
    }

    override fun getCurrentGroupPlace(): Place {
        val pDto = groups[currentGid]?.place!!
        return Place(pDto.country,
            pDto.city,
            pDto.street,
            pDto.number,
            pDto.postalCode,
            LatLng(pDto.lat, pDto.lon))
    }

    override fun getCurrentGroupId(): String {
        return currentGid
    }

    override fun setCurrentGroupId(gid: String) {
        currentGid = gid
    }

    override fun loadItinerary() {
        val place = groups[currentGid]?.place
        val latitude = place?.lat?.toString()
        val longitude = place?.lon?.toString()

        if(latitude != null && longitude != null) {
            groupView.buildItinerary(latitude,longitude)
        }
    }

    override fun setIGroupView(groupView: IGroupView) {
        this.groupView = groupView
    }

    override fun setICreateGroupView(createGroupView: ICreateGroupView) {
        this.createGroupView = createGroupView
    }

    override fun setIHomeView(homeView: IHomeView) {
        this.homeView = homeView
    }
}