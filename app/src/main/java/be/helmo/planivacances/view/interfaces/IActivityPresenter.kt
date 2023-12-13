package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.presenter.interfaces.IActivityView
import be.helmo.planivacances.presenter.interfaces.ICalendarView
import be.helmo.planivacances.presenter.interfaces.ICreateActivityView
import be.helmo.planivacances.presenter.viewmodel.ActivityVM

interface IActivityPresenter {
    fun setICalendarView(calendarView: ICalendarView)
    fun setIActivityView(activityView: IActivityView)
    fun setICreateActivityView(createIActivityView: ICreateActivityView)
    fun setCurrentActivity(activityId:String)
    suspend fun getCalendarFile()
    suspend fun loadActivities()

    fun loadCurrentActivity()

    fun loadItinerary()

    suspend fun createActivity(activityVM: ActivityVM)

    suspend fun deleteCurrentActivity()
    fun onActivityDateChanged(dayOfMonth: Int,month: Int,year: Int)
}