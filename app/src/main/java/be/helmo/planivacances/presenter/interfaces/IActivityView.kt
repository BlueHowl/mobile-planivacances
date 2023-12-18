package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.presenter.viewmodel.ActivityDetailVM

interface IActivityView : IShowToast {
    fun loadActivity(activity:ActivityDetailVM)
    fun buildItinerary(latitude: String, longitude: String)

    fun onActivityDeleted()
}