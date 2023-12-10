package be.helmo.planivacances.presenter

import be.helmo.planivacances.presenter.interfaces.IActivityView
import be.helmo.planivacances.presenter.interfaces.ICalendarView
import be.helmo.planivacances.presenter.viewmodel.ActivityDetailVM
import be.helmo.planivacances.presenter.viewmodel.ActivityListItemVM
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.dto.ActivitiesDTO
import be.helmo.planivacances.service.dto.ActivityDTO
import be.helmo.planivacances.service.dto.PlaceDTO
import be.helmo.planivacances.view.interfaces.IActivityPresenter
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ActivityPresenter(private val groupPresenter: IGroupPresenter) : IActivityPresenter {
    lateinit var calendarView : ICalendarView
    lateinit var activityView: IActivityView
    var activitiesDTO: ActivitiesDTO = ActivitiesDTO(HashMap())
    lateinit var currentActivityDTO : Pair<String,ActivityDTO>

    override fun setICalendarView(calendarView: ICalendarView) {
        this.calendarView = calendarView
    }

    override fun setIActivityView(activityView: IActivityView) {
        this.activityView = activityView
    }

    override fun setCurrentActivity(activityId: String) {
        if(activitiesDTO.activitiesMap.contains(activityId)) {
            currentActivityDTO = Pair(activityId,activitiesDTO.activitiesMap[activityId]!!)
        }
    }

    override suspend fun getCalendarFile() {
        try {
            val response = ApiClient.calendarService.getICS(groupPresenter.getCurrentGroupId())

            if(response.isSuccessful && response.body() != null) {
                val currentGroup  = groupPresenter.getCurrentGroup()!!
                calendarView.downloadCalendar(response.body()!!,"Calendrier-${currentGroup.groupName}.ics")
            }
        } catch(e:Exception) {
            e.printStackTrace()
            calendarView.showToast("Erreur lors du téléchargement du calendrier",1)
        }
    }

    override suspend fun loadActivities() {
        val response = ApiClient.activityService.loadActivities(groupPresenter.getCurrentGroupId())

        if(response.isSuccessful && response.body() != null) {
            activitiesDTO.activitiesMap.clear()

            response.body()!!.forEach { entry ->
                run {
                    activitiesDTO.activitiesMap[entry.key] = entry.value
                }
            }
            onActivitiesLoaded()
        }
    }

    override suspend fun deleteCurrentActivity() {
        val response = ApiClient.activityService.deleteActivity(groupPresenter.getCurrentGroupId(),currentActivityDTO.first)

        if(response.isSuccessful) {
            activityView.onActivityDeleted()
        } else {
            activityView.showToast("Erreur durant la suppression de l'activité",1)
        }
    }

    override fun loadCurrentActivity() {
        val currentActivity = currentActivityDTO.second
        activityView.loadActivity(ActivityDetailVM(currentActivity.title,formatPeriod(currentActivity.startDate,currentActivity.duration),formatPlace(currentActivity.place),currentActivity.description))
    }

    override fun loadItinerary() {
        val currentActivity = currentActivityDTO.second
        activityView.buildItinerary(currentActivity.place.lat.toString(),currentActivity.place.lon.toString())
    }

    fun onActivitiesLoaded() {
        val activityDates : ArrayList<Date> = ArrayList()
        activitiesDTO.activitiesMap.forEach {
            entry ->
            activityDates.add(entry.value.startDate)
        }
        calendarView.onActivitiesLoaded(activityDates)
    }

    override fun onActivityDateChanged(dayOfMonth: Int, month: Int, year: Int) {
        val date = Calendar.getInstance()
        date.set(year, month, dayOfMonth)

        val activities : ArrayList<ActivityListItemVM> = ArrayList()
        val dateFormat = SimpleDateFormat("dd/MM/YYYY")
        val formattedDate = dateFormat.format(date.timeInMillis)

        activitiesDTO.activitiesMap.forEach {
            entry ->
            val currentActivity = entry.value
            if(formattedDate == dateFormat.format(currentActivity.startDate)) {
                activities.add(ActivityListItemVM(entry.key,currentActivity.title,formatStartDate(currentActivity.startDate),formatDuration(currentActivity.duration)))
            }
        }
        calendarView.setDisplayedActivities(activities)
    }

    fun formatStartDate(startDate:Date) : String {
        val startDateFormat : SimpleDateFormat = SimpleDateFormat("HH'h'mm")
        return startDateFormat.format(startDate)
    }

    fun formatPeriod(startDate: Date,duration: Int) : String {
        val startDateFormat : SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        return "Le ${startDateFormat.format(startDate)} à ${formatStartDate(startDate)} - Durée : ${formatDuration(duration)}"
    }

    fun formatPlace(placeDTO: PlaceDTO) : String {
        return "${placeDTO.street},${placeDTO.number},${placeDTO.city} ${placeDTO.postalCode},${placeDTO.country}"
    }

    fun formatDuration(duration: Int) : String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        return String.format("%dh%02d",hours,minutes)
    }
}