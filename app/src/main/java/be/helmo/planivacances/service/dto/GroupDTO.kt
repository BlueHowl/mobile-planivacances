package be.helmo.planivacances.service.dto

import be.helmo.planivacances.domain.Place
import java.util.*

data class GroupDTO(val gid: String,
                    val groupName: String,
                    val description: String,
                    val startDate: Date,
                    val endDate: Date,
                    val place: PlaceDTO,
                    val isPublished: Boolean,
                    val owner: String = "null")
