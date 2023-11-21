package be.helmo.planivacances.service.dto

import java.util.*

data class GroupDTO(
    var gid: String? = null,
    val groupName: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val place: PlaceDTO,
    var owner: String? = null)
