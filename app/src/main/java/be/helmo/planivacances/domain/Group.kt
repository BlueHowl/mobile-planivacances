package be.helmo.planivacances.domain

import java.util.*

data class Group(val groupName: String,
                 val description: String,
                 val startDate: Date,
                 val endDate: Date,
                 val place: Place,
                 val isPublished: Boolean,
                 val owner: String = "null")
