package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.service.dto.CreateGroupDTO
import be.helmo.planivacances.service.dto.PlaceDTO
import be.helmo.planivacances.util.ResultMessage

interface IGroupPresenter {

    suspend fun createGroup(createGroup: CreateGroupDTO, placeDTO: PlaceDTO): ResultMessage

    fun getCurrentGroup(): Group?

    fun getCurrentGroupPlace(): Place?

}