package be.helmo.planivacances.view.fragments.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentCalendarBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.ICalendarView
import be.helmo.planivacances.presenter.viewmodel.ActivityListItemVM
import be.helmo.planivacances.view.interfaces.IActivityPresenter
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment(), ICalendarView {

    lateinit var binding : FragmentCalendarBinding
    lateinit var activityPresenter: IActivityPresenter
    lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPresenter = AppSingletonFactory.instance!!.getCalendarPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater,container,false)

        calendarAdapter = CalendarAdapter() {selectedActivityId ->
            activityPresenter.setCurrentActivity(selectedActivityId)
            findNavController().navigate(R.id.action_CalendarFragment_to_ActivityFragment)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.layoutManager = layoutManager
        binding.rvActivities.adapter = calendarAdapter

        binding.exportCalendarBtn.isEnabled = false
        binding.exportCalendarBtn.isClickable = false

        val currentDate = Date()

        binding.calendarView.setSelectedDate(currentDate)

        binding.tvBack2.setOnClickListener {
            findNavController().navigate(R.id.action_CalendarFragment_to_groupFragment)
        }

        binding.calendarView.setOnDateChangedListener { _, date, _ ->
            lifecycleScope.launch(Dispatchers.Default) {
                activityPresenter.onActivityDateChanged(date.day,date.month,date.year)
//                binding.calendarView.setDateSelected(currentDate,true)
            }
        }

        lifecycleScope.launch(Dispatchers.Default) {
            activityPresenter.loadActivities()
        }

        binding.createActivityBtn.setOnClickListener {
            findNavController().navigate(R.id.action_CalendarFragment_to_CreateActivityFragment)
        }

        binding.exportCalendarBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Default) {
                activityPresenter.getCalendarFile()
            }
        }
        return binding.root
    }

    companion object {
        const val TAG = "CalendarFragment"

        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun downloadCalendar(calendarContent: String,fileName:String) {
        MainScope().launch {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            try {
                val outputStream = FileOutputStream(file)
                outputStream.write(calendarContent.toByteArray())
                outputStream.close()

                val uri = FileProvider.getUriForFile(
                    context!!,
                    "${context!!.packageName}.provider",
                    file
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "application/ics")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context?.startActivity(intent)
            } catch (e: Exception) {
                showToast("Erreur durant le téléchargement du calendrier",1)
            }
        }
    }

    override fun onActivitiesLoaded(activityDates: List<Date>) {
        MainScope().launch {
            binding.exportCalendarBtn.isEnabled = true
            binding.exportCalendarBtn.isClickable = true
            lifecycleScope.launch(Dispatchers.Default) {
                val currentDate : CalendarDay = binding.calendarView.selectedDate
                activityPresenter.onActivityDateChanged(currentDate.day,currentDate.month,currentDate.year)
            }

            binding.calendarView.removeDecorators()

            for(date in activityDates) {
                val calendarDay = CalendarDay.from(date)
                binding.calendarView.addDecorator(EventDecorator(Color.BLUE,calendarDay))
            }
        }
    }

    override fun setDisplayedActivities(activities: List<ActivityListItemVM>) {
        MainScope().launch {
            calendarAdapter.clearActivitiesList()
            activities.forEach {
                calendarAdapter.addActivity(it)
            }
        }
    }

    override fun showToast(message: String,length:Int) {
        MainScope().launch {
            Toast.makeText(context, message, length).show()
        }
    }

    private inner class EventDecorator(private val color: Int, private val date: CalendarDay) :
        DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == date
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(5F, color))
        }
    }
}