package be.helmo.planivacances.presenter

import android.util.Log
import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.presenter.viewmodel.GroupListItemVM
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.presenter.interfaces.ICreateGroupView
import be.helmo.planivacances.presenter.interfaces.IGroupView
import be.helmo.planivacances.presenter.interfaces.IHomeView
import be.helmo.planivacances.presenter.viewmodel.GroupDetailVM
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.util.DTOMapper
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat

class GroupPresenter : IGroupPresenter {
    lateinit var groupView: IGroupView
    lateinit var createGroupView: ICreateGroupView
    lateinit var homeView: IHomeView
    var groups : HashMap<String, Group> = HashMap()

    lateinit var currentGid : String

    /**
     * Crée un groupe
     * @param group (GroupDTO)
     */
    override suspend fun createGroup(group: Group) {
        val groupDto = DTOMapper.groupToGroupDTO(group)

        try {
            val response = ApiClient.groupService.create(groupDto)

            if (response.isSuccessful && response.body() != null) {
                val gid = response.body()!!

                currentGid = gid

                group.owner = FirebaseAuth.getInstance().uid!!
                groups[gid] = group

                Log.d("CreateGroupFragment", "Group created : $gid")
                createGroupView.onGroupCreated()
            } else {
                Log.d("CreateGroupFragment",
                    "${response.message()}, ${response.isSuccessful}")
                createGroupView.showToast(
                    "Erreur lors de la création du groupe ${response.message()}",
                    1
                )
            }

        } catch (e: Exception) {
            createGroupView.showToast("Erreur durant la création du groupe", 1)
        }

    }

    /**
     * Charge les groupes de l'utilisateur
     */
    suspend fun loadUserGroups() {
        try {
            val response = ApiClient.groupService.getList()

            if (response.isSuccessful && response.body() != null) {
                val groupsDto = response.body()!!
                Log.d("ag", response.body().toString())
                for(groupDto in groupsDto) {
                    Log.d("gg", groupDto.place.city)
                    groups[groupDto.gid!!] = DTOMapper.groupDtoToGroup(groupDto)
                }

                Log.d("GroupFragment", "Groups retrieved : ${groups.size}")
                homeView.setGroupList(getGroupListItems())
            } else {
                Log.d("GroupFragment", "${response.message()}, ${response.isSuccessful}")
                homeView.showToast(
                    "Erreur lors de la récupération des groupes ${response.message()}",
                    1
                )
            }

        } catch (e: Exception) {
            Log.d("GroupFragment",
                "Erreur durant la récupération des groupes : ${e.message}")
            homeView.showToast("Erreur durant la récupération des groupes", 1)
        }

    }

    /**
     * Charge les données nécessaires à l'affichage de l'itinéraire
     */
    override fun loadItinerary() {
        val place = groups[currentGid]?.place
        val latitude = place?.latLng?.latitude.toString()
        val longitude = place?.latLng?.longitude.toString()

        groupView.buildItinerary(latitude,longitude)
    }

    override fun showGroupInfos() {
        val group = getCurrentGroup()!!
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val startDate = formatter.format(group.startDate)
        val endDate = formatter.format(group.endDate)

        val groupDetailVM = GroupDetailVM(group.groupName,
            group.description,
            "Du $startDate au $endDate",
            getCurrentGroupPlace().address)

        groupView.setGroupInfos(groupDetailVM)
    }

    override suspend fun showGroupList() {
        val groups = getGroupListItems()

        //charge une seule fois
        if(groups.isEmpty()) {
            loadUserGroups()
        } else {
            homeView.setGroupList(groups)
        }
    }

    /**
     * Récupère la liste des groupes
     * @return (List<GroupDTO>)
     */
    fun getGroupListItems(): List<GroupListItemVM> {
        return groups.entries.map { (gid, group) ->
            DTOMapper.groupToGroupListItem(gid, group)
        }
    }

    /**
     * Récupère le groupe courant
     * @return (GroupDTO)
     */
    override fun getCurrentGroup(): Group? {
        return groups[currentGid]
    }

    /**
     * Récupère le lieu du groupe courant
     * @return (Place)
     */
    override fun getCurrentGroupPlace(): Place {
        return groups[currentGid]?.place!!
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