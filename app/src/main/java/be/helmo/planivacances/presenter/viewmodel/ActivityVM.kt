package be.helmo.planivacances.presenter.viewmodel

import java.util.*

data class ActivityVM(
    var title: String,
    var description: String,
    var startDate: Date,
    var duration: Int,
    var place: PlaceVM
)