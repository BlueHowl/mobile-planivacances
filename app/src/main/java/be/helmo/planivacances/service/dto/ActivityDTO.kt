package be.helmo.planivacances.service.dto

import java.util.*

data class ActivityDTO(
    var title: String,
    var description: String,
    var startDate: Date,
    var duration: Int,
    var place: PlaceDTO
    )