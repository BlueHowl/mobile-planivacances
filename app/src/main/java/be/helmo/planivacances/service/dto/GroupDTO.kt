package be.helmo.planivacances.service.dto

import java.util.*

data class GroupDTO(val groupName: String,
                    val description: String,
                    val startDate: Date,
                    val endDate: Date,
                    val placeId: String,
                    val isPublished: Boolean,
                    val owner: String = "null")
