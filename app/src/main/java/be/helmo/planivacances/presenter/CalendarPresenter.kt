package be.helmo.planivacances.presenter

import be.helmo.planivacances.presenter.interfaces.ICalendarView
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.view.interfaces.ICalendarPresenter
import be.helmo.planivacances.view.interfaces.IGroupPresenter

class CalendarPresenter(private val groupPresenter: IGroupPresenter) : ICalendarPresenter {
    private var calendarView : ICalendarView? = null

    override fun setCalendarView(calendarView: ICalendarView) {
        this.calendarView = calendarView
    }

    override suspend fun getCalendarFile() {
        try {
            val response = ApiClient.calendarService.getICS(groupPresenter.getCurrentGroupId())

            if(response.isSuccessful && response.body() != null) {
                val currentGroup  = groupPresenter.getCurrentGroup()!!
                calendarView?.downloadCalendar(response.body()!!,"Calendrier-${currentGroup.groupName}")
            }
        } catch(e:Exception) {
            calendarView?.showToast("Erreur lors du téléchargement du calendrier",1)
        }
    }

}