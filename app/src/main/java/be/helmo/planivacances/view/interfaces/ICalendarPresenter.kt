package be.helmo.planivacances.view.interfaces

import be.helmo.planivacances.presenter.interfaces.ICalendarView

interface ICalendarPresenter {
    fun setCalendarView(calendarView:ICalendarView)
    suspend fun getCalendarFile()
}