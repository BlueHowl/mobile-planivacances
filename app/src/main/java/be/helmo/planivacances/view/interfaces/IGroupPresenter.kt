package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.presenter.interfaces.ICreateGroupView
import be.helmo.planivacances.presenter.interfaces.IGroupView
import be.helmo.planivacances.presenter.interfaces.IHomeView
import be.helmo.planivacances.service.dto.GroupDTO

interface IGroupPresenter {

    suspend fun createGroup(group: GroupDTO)

    suspend fun loadUserGroups()

    fun getGroups(): List<GroupDTO>

    fun getCurrentGroup(): GroupDTO?

    fun getCurrentGroupPlace(): Place?

    fun getCurrentGroupId() : String

    fun setCurrentGroupId(gid: String)

    fun loadItinerary()

    fun setIGroupView(groupView:IGroupView)

    fun setICreateGroupView(createGroupView: ICreateGroupView)

    fun setIHomeView(homeView: IHomeView)
}