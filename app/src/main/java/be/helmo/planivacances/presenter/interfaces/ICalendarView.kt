package be.helmo.planivacances.presenter.interfaces

import be.helmo.planivacances.presenter.viewmodel.ActivityListItemVM
import java.util.*

interface ICalendarView : IShowToast {
    fun downloadCalendar(calendarContent:String,fileName:String)
    fun onActivitiesLoaded(activityDates: List<Date>)
    fun setDisplayedActivities(activities: List<ActivityListItemVM>)
}