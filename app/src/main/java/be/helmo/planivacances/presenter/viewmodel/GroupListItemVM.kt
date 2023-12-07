package be.helmo.planivacances.presenter.viewmodel

import java.util.*

data class GroupListItemVM(
    var gid: String? = null,
    val groupName: String,
    val description: String,
    val startDate: Date,
    val endDate: Date)
