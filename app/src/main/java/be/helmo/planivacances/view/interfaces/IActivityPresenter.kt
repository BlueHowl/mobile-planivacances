package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.presenter.interfaces.IActivityView
import be.helmo.planivacances.presenter.interfaces.ICalendarView

interface IActivityPresenter {
    fun setICalendarView(calendarView: ICalendarView)
    fun setIActivityView(activityView: IActivityView)
    fun setCurrentActivity(activityId:String)
    suspend fun getCalendarFile()
    suspend fun loadActivities()

    suspend fun deleteCurrentActivity()
    fun loadCurrentActivity()

    fun loadItinerary()
    fun onActivityDateChanged(dayOfMonth: Int,month: Int,year: Int)
}