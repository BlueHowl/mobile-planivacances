package be.helmo.planivacances.domain

import java.util.*

data class GroupListItem(
    var gid: String? = null,
    val groupName: String,
    val description: String,
    val startDate: Date,
    val endDate: Date)
