package be.helmo.planivacances.presenter.viewmodel

import java.util.*

data class GroupVM(
    val name:String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val place: PlaceVM
)