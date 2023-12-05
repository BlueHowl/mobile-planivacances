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

    /**
     * Crée un groupe
     * @param group (GroupDTO)
     */
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
                createGroupView.showToast("Erreur lors de la création du groupe ${response.message()}", 1)
            }

        } catch (e: Exception) {
            createGroupView.showToast("Erreur durant la création du groupe", 1)
        }

    }

    /**
     * Charge les groupes de l'utilisateur
     */
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
                homeView.showToast("Erreur lors de la récupération des groupes ${response.message()}", 1)
            }

        } catch (e: Exception) {
            homeView.showToast("Erreur durant la récupération des groupes", 1)
        }

    }

    /**
     * Récupère la liste des groupes
     * @return (List<GroupDTO>)
     */
    override fun getGroups(): List<GroupDTO> {
        return groups.values.toList()
    }

    /**
     * Récupère le groupe courant
     * @return (GroupDTO)
     */
    override fun getCurrentGroup(): GroupDTO? {
        return groups[currentGid]
    }

    /**
     * Récupère le lieu du groupe courant
     * @return (Place)
     */
    override fun getCurrentGroupPlace(): Place {
        val pDto = groups[currentGid]?.place!!
        return Place(pDto.country,
            pDto.city,
            pDto.street,
            pDto.number,
            pDto.postalCode,
            LatLng(pDto.lat, pDto.lon))
    }

    /**
     * Récupère l'id du groupe courant
     * @return (String)
     */
    override fun getCurrentGroupId(): String {
        return currentGid
    }

    /**
     * Assigne l'id du groupe séléctionné
     * @param gid (String)
     */
    override fun setCurrentGroupId(gid: String) {
        currentGid = gid
    }

    /**
     * Charge les données nécessaires à l'affichage de l'itinéraire
     */
    override fun loadItinerary() {
        val place = groups[currentGid]?.place
        val latitude = place?.lat?.toString()
        val longitude = place?.lon?.toString()

        if(latitude != null && longitude != null) {
            groupView.buildItinerary(latitude,longitude)
        }
    }

    /**
     * Assigne la GroupView Interface
     */
    override fun setIGroupView(groupView: IGroupView) {
        this.groupView = groupView
    }

    /**
     * Assigne la CreateGroupView Interface
     */
    override fun setICreateGroupView(createGroupView: ICreateGroupView) {
        this.createGroupView = createGroupView
    }

    /**
     * Assigne la HomeView Interface
     */
    override fun setIHomeView(homeView: IHomeView) {
        this.homeView = homeView
    }
}