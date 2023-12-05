package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.domain.GroupListItem
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.presenter.interfaces.ICreateGroupView
import be.helmo.planivacances.presenter.interfaces.IGroupView
import be.helmo.planivacances.presenter.interfaces.IHomeView

interface IGroupPresenter {

    suspend fun createGroup(group: Group)

    suspend fun loadUserGroups()

    fun getGroupListItems(): List<GroupListItem>

    fun getCurrentGroup(): Group?

    fun getCurrentGroupPlace(): Place?

    fun getCurrentGroupId() : String

    fun setCurrentGroupId(gid: String)

    fun loadItinerary()

    fun setIGroupView(groupView:IGroupView)

    fun setICreateGroupView(createGroupView: ICreateGroupView)

    fun setIHomeView(homeView: IHomeView)
}