package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.presenter.interfaces.ICreateGroupView
import be.helmo.planivacances.presenter.interfaces.IGroupView
import be.helmo.planivacances.presenter.interfaces.IHomeView
import be.helmo.planivacances.presenter.interfaces.IUpdateGroupView
import be.helmo.planivacances.presenter.viewmodel.GroupVM

interface IGroupPresenter {

    suspend fun createGroup(groupVM: GroupVM)

    fun loadItinerary()

    fun showGroupInfos()

    suspend fun loadUserGroups()

    fun getCurrentGroup(): Group?

    fun getCurrentGroupPlace(): Place?

    fun getCurrentGroupId() : String

    fun setCurrentGroupId(gid: String)

    fun setIGroupView(groupView:IGroupView)

    fun setICreateGroupView(createGroupView: ICreateGroupView)

    fun setIUpdateGroupView(updateGroupView: IUpdateGroupView)

    fun setIHomeView(homeView: IHomeView)

    fun loadCurrentGroup()

    suspend fun updateCurrentGroup(groupVM:GroupVM)

    suspend fun deleteCurrentGroup()
}