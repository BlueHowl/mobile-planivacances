package be.helmo.planivacances.view.fragments.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentCreateActivityBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.ICreateActivityView
import be.helmo.planivacances.presenter.viewmodel.ActivityVM
import be.helmo.planivacances.presenter.viewmodel.PlaceVM
import be.helmo.planivacances.view.interfaces.IActivityPresenter
import com.adevinta.leku.LATITUDE
import com.adevinta.leku.LONGITUDE
import com.adevinta.leku.LocationPickerActivity
import com.adevinta.leku.ZIPCODE
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class CreateActivityFragment : Fragment(), ICreateActivityView {
    lateinit var binding : FragmentCreateActivityBinding
    lateinit var activityPresenter: IActivityPresenter
    lateinit var lekuActivityResultLauncher: ActivityResultLauncher<Intent>

    var dateField : Int = 0
    var startDate: String? = null
    var endDate: String? = null

    var startTime: String? = null
    var endTime: String? = null

    var country: String? = null
    var city: String? = null
    var street: String? = null
    var number: String? = null
    var postalCode: String? = null
    var location: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityPresenter = AppSingletonFactory.instance!!.getActivityPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateActivityBinding.inflate(inflater,container,false)

        binding.tvCreateActivityPlace.setOnClickListener {
            createLocationPickerDialog()
        }

        binding.tvBackToCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_CreateActivityFragment_to_CalendarFragment)
        }

        binding.tvCreateActivityStartDate.setOnClickListener {
            dateField = 0
            createDateHourDialog()
        }

        binding.tvCreateActivityEndDate.setOnClickListener {
            dateField = 1
            createDateHourDialog()
        }

        binding.createActivityBtn.setOnClickListener {
            addActivity()
        }

        lekuActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->

            if(result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val latitude = data?.getDoubleExtra(LATITUDE,0.0)
                val longitude = data?.getDoubleExtra(LONGITUDE,0.0)
                postalCode = data?.getStringExtra(ZIPCODE)

                val addressFormatted = getAddressFromLatLng(requireContext(), latitude!!, longitude!!)

                location =  LatLng(latitude,longitude)

                binding.tvCreateActivityPlace.text = addressFormatted
            } else {
                showToast("Erreur lors de la récupération de la localisation",1)
            }
        }

        return binding.root
    }

    fun addActivity() {
        if(binding.etCreateActivityTitle.text.isBlank()) {
            showToast("Le titre doit être défini",1)
            return
        }
        try {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val startDate = formatter.parse(binding.tvCreateActivityStartDate.text.toString())!!
            val endDate = formatter.parse(binding.tvCreateActivityEndDate.text.toString())!!
            val currentDate = Calendar.getInstance().time

            if(startDate.before(currentDate) || endDate.before(currentDate)) {
                showToast("La date de début et de fin doivent être supérieures ou égales à la date du jour",1)
                return
            }

            if(startDate.after(endDate)) {
                showToast("La date de fin ne peut pas être antérieure à la date de début",1)
                return
            }

            if(street == null || postalCode == null || city == null || country == null) {
                showToast("Adresse invalide",1)
                return
            }

            val duration: Int = TimeUnit.MILLISECONDS.toSeconds(endDate.time - startDate.time).toInt()

            val place = PlaceVM(street!!,number!!,postalCode!!,city!!,country!!,location!!)

            val activity : ActivityVM = ActivityVM(binding.etCreateActivityTitle.text.toString(),binding.etCreateActivityDescription.text.toString(),startDate,duration,place)

            lifecycleScope.launch(Dispatchers.Default) {
                activityPresenter.createActivity(activity)
            }
        } catch(e : ParseException) {
            showToast("Une des dates est mal encodée",1)
        }
    }

    fun createLocationPickerDialog() {
        val locationPickerIntent = LocationPickerActivity.Builder()
            .withDefaultLocaleSearchZone()
            .shouldReturnOkOnBackPressed()
            .withZipCodeHidden()
            .withVoiceSearchHidden()
            .build(requireContext())

        lekuActivityResultLauncher.launch(locationPickerIntent)
    }

    fun createDateHourDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(
            requireView().context,
            DatePickerDialog.OnDateSetListener {_,year,month,dayOfMonth ->
                onDateSet(year,month,dayOfMonth)
                createTimePickerDialog()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        val formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month+1, year)

        if (dateField == 0) {
            startDate = formattedDate
        } else if (dateField == 1) {
            endDate = formattedDate
        }
    }

    fun createTimePickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireView().context,
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                onTimeSet(hour,minute)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    fun onTimeSet(hour:Int,minute:Int) {
        val formattedTime = String.format("%02d:%02d", hour, minute)

        if(dateField == 0) {
            startTime = formattedTime
            binding.tvCreateActivityStartDate.text = "$startDate $startTime"
        } else if(dateField == 1) {
            endTime = formattedTime
            binding.tvCreateActivityEndDate.text = "$endDate $endTime"
        }
    }

    fun getAddressFromLatLng(context: Context, lat:Double, lng:Double) : String? {
        val geocoder = Geocoder(context,Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(lat,lng,1) as List<Address>

            if(addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                city = address.locality.trim()
                number = address.subThoroughfare
                street = address.thoroughfare
                country = address.countryName.trim()

                return "$street, $number $city $country"
            }
        } catch(e: IOException) {
            showToast("Erreur durant la récupération de l'adresse provenant de l'emplacement",1)
        }
        return null
    }

    override fun onActivityCreated() {
        MainScope().launch {
            showToast("Activité créée avec succès",1)
            findNavController().navigate(R.id.action_CreateActivityFragment_to_CalendarFragment)
        }
    }

    override fun showToast(message: String, length: Int) {
        MainScope().launch {
            Toast.makeText(context, message, length).show()
        }
    }

    companion object {
        const val TAG = "CreateActivityFragment"

        fun newInstance(): CreateActivityFragment {
            return CreateActivityFragment()
        }
    }
}