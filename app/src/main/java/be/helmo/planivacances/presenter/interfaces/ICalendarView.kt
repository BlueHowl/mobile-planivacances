package be.helmo.planivacances.presenter.interfaces

interface ICalendarView : IShowToast {
    fun downloadCalendar(calendarContent:String,fileName:String)
}