package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.service.dto.CreateGroupDTO
import be.helmo.planivacances.util.ResultMessage

interface IGroupPresenter {

    suspend fun createGroup(group: CreateGroupDTO): ResultMessage

}