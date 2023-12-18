package be.helmo.planivacances.presenter.viewmodel

import java.util.*

data class ActivityVM(
    val title: String,
    val description: String,
    val startDate: Date,
    val duration: Int,
    val place: PlaceVM
)