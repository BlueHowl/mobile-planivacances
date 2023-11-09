package be.helmo.planivacances.service.dto

import java.util.Date

data class CreateGroupDTO(
    val groupName: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val placeId: String = "null",
    val isPublished: Boolean = false,
    val owner: String = "null")
