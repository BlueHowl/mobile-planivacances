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
import be.helmo.planivacances.databinding.FragmentUpdateActivityBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.IUpdateActivityView
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
import java.util.*
import java.util.concurrent.TimeUnit

class UpdateActivityFragment : Fragment(), IUpdateActivityView {
    lateinit var binding : FragmentUpdateActivityBinding
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
        binding = FragmentUpdateActivityBinding.inflate(inflater,container,false)

        lifecycleScope.launch(Dispatchers.Default) {
            activityPresenter.getCurrentActivity()
        }

        binding.tvUpdateActivityPlace.setOnClickListener {
            createLocationPickerDialog()
        }

        binding.tvBackToCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_UpdateActivityFragment_to_ActivityFragment)
        }

        binding.tvUpdateActivityStartDate.setOnClickListener {
            dateField = 0
            createDateHourDialog()
        }

        binding.tvUpdateActivityEndDate.setOnClickListener {
            dateField = 1
            createDateHourDialog()
        }

        binding.updateActivityBtn.setOnClickListener {
            updateActivity()
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

                binding.tvUpdateActivityPlace.text = addressFormatted
            } else {
                showToast("Erreur lors de la récupération de la localisation",1)
            }
        }



        return binding.root
    }

    fun updateActivity() {
        if(binding.etUpdateActivityTitle.text.isBlank()) {
            showToast("Le titre doit être défini",1)
            return
        }
        try {
            val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val startDate = formatter.parse(binding.tvUpdateActivityStartDate.text.toString())!!
            val endDate = formatter.parse(binding.tvUpdateActivityEndDate.text.toString())!!
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

            val activity : ActivityVM = ActivityVM(binding.etUpdateActivityTitle.text.toString(),binding.etUpdateActivityDescription.text.toString(),startDate,duration,place)

            lifecycleScope.launch(Dispatchers.Default) {
                activityPresenter.updateCurrentActivity(activity)
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

        if(location != null) {
            locationPickerIntent.withLocation(location)
        }

        lekuActivityResultLauncher.launch(locationPickerIntent.build(requireContext()))
    }

    fun createDateHourDialog() {
        val calendar: Calendar = Calendar.getInstance()
        if(dateField == 0 && startDate != null) {
            calendar.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(startDate)
        } else if(dateField == 1 && endDate != null) {
            calendar.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(endDate)
        }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(
            requireView().context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
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
        if(dateField == 0 && startTime != null) {
            calendar.time = SimpleDateFormat("HH:mm").parse(startTime)
        } else if(dateField == 1 && endTime != null) {
            calendar.time = SimpleDateFormat("HH:mm").parse(endTime)
        }
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
            binding.tvUpdateActivityStartDate.text = "$startDate $startTime"
        } else if(dateField == 1) {
            endTime = formattedTime
            binding.tvUpdateActivityEndDate.text = "$endDate $endTime"
        }
    }

    fun getAddressFromLatLng(context: Context, lat:Double, lng:Double) : String? {
        val geocoder = Geocoder(context, Locale.getDefault())

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

    override fun setCurrentActivity(activityVM: ActivityVM) {
        MainScope().launch {
            binding.etUpdateActivityTitle.setText(activityVM.title)

            startDate = SimpleDateFormat("dd/MM/yyyy").format(activityVM.startDate)
            startTime = SimpleDateFormat("HH:mm").format(activityVM.startDate)
            binding.tvUpdateActivityStartDate.setText("$startDate $startTime")

            val calendar = Calendar.getInstance()
            calendar.time = activityVM.startDate
            calendar.add(Calendar.SECOND, activityVM.duration)
            val currentEndDate = calendar.time
            endDate = SimpleDateFormat("dd/MM/yyyy").format(currentEndDate)
            endTime = SimpleDateFormat("HH:mm").format(currentEndDate)
            binding.tvUpdateActivityEndDate.setText("$endDate $endTime")

            val placeVM: PlaceVM = activityVM.place
            country = placeVM.country
            city = placeVM.city
            street = placeVM.street
            number = placeVM.number
            postalCode = placeVM.postalCode
            location = LatLng(placeVM.latLng.latitude, placeVM.latLng.longitude)
            binding.tvUpdateActivityPlace.setText("$street, $number $city $country")

            binding.etUpdateActivityDescription.setText(activityVM.description)
        }
    }

    override fun onActivityUpdated() {
        MainScope().launch {
            showToast("Activité mise à jour avec succès",1)
            findNavController().navigate(R.id.action_UpdateActivityFragment_to_CalendarFragment)
        }
    }

    override fun showToast(message: String, length: Int) {
        MainScope().launch {
            Toast.makeText(context, message, length).show()
        }
    }

    companion object {
        const val TAG = "UpdateActivityFragment"

        fun newInstance(): UpdateActivityFragment {
            return UpdateActivityFragment()
        }
    }
}