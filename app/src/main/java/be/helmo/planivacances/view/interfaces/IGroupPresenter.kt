package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.service.dto.GroupDTO
import be.helmo.planivacances.util.ResultMessage

interface IGroupPresenter {

    suspend fun createGroup(group: GroupDTO): ResultMessage

    suspend fun loadUserGroups(): ResultMessage

    fun getGroups(): List<GroupDTO>

    fun getCurrentGroup(): GroupDTO?

    fun getCurrentGroupPlace(): Place?

    fun getCurrentGroupId() : String

    fun setCurrentGroupId(gid: String)

}