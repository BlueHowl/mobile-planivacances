package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.presenter.viewmodel.ActivityVM

interface IUpdateActivityView : IShowToast {
    fun setCurrentActivity(activityVM: ActivityVM)
    fun onActivityUpdated()
}