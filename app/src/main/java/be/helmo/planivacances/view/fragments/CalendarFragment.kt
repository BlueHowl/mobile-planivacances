package be.helmo.planivacances.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import be.helmo.planivacances.databinding.FragmentCalendarBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.ICalendarView
import be.helmo.planivacances.view.interfaces.ICalendarPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * A simple [Fragment] subclass.
 * Use the [CalendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CalendarFragment : Fragment(), ICalendarView {

    lateinit var binding : FragmentCalendarBinding
    lateinit var calendarPresenter: ICalendarPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendarPresenter = AppSingletonFactory.instance!!.getCalendarPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater,container,false)

        lifecycleScope.launch(Dispatchers.Main) {
            calendarPresenter.getCalendarFile()
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

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.fromFile(file), "text/calendar")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            } catch (e: Exception) {
                showToast("Erreur durant le téléchargement du calendrier")
            }
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}