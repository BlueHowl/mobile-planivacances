package be.helmo.planivacances.view.fragments.group

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentUpdateGroupBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.IUpdateGroupView
import be.helmo.planivacances.presenter.viewmodel.GroupVM
import be.helmo.planivacances.presenter.viewmodel.PlaceVM
import be.helmo.planivacances.view.interfaces.IGroupPresenter
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

class UpdateGroupFragment : Fragment(), IUpdateGroupView {

    lateinit var binding : FragmentUpdateGroupBinding
    lateinit var groupPresesenter : IGroupPresenter
    lateinit var lekuActivityResultLauncher : ActivityResultLauncher<Intent>

    var country: String? = null
    var city: String? = null
    var street: String? = null
    var number: String? = null
    var postalCode: String? = null
    var location: LatLng? = null

    var dateField: Int = 0
    var startDate: String? = null
    var endDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        groupPresesenter = AppSingletonFactory.instance!!.getGroupPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateGroupBinding.inflate(inflater,container,false)

        lifecycleScope.launch(Dispatchers.Default) {
            groupPresesenter.loadCurrentGroup()
        }

        binding.tvBackToHome.setOnClickListener {
            findNavController().navigate(R.id.action_UpdateGroupFragment_to_groupFragment)
        }

        binding.tvUpdateGroupPlace.setOnClickListener {
            createLocationPickerDialog()
        }

        binding.tvUpdateGroupStartDate.setOnClickListener {
            dateField = 0
            createDateHourDialog()
        }

        binding.tvUpdateGroupEndDate.setOnClickListener {
            dateField = 1
            createDateHourDialog()
        }

        binding.updateGroupBtn.setOnClickListener {
            updateGroup()
        }

        lekuActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result: ActivityResult ->

                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
                    val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
                    postalCode = data?.getStringExtra(ZIPCODE)

                    val addressFormated = getAddressFromLatLng(requireContext(),
                        latitude!!,
                        longitude!!)

                    this.location = LatLng(latitude, longitude)
                    binding.tvUpdateGroupPlace.text = addressFormated

                } else {
                    showToast(
                        "Erreur lors de la récupération de la localisation",
                        1
                    )
                }
            }

        return binding.root
    }

    fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1) as List<Address>

            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                country = address.countryName.trim()
                city = address.locality.trim()
                street = address.thoroughfare
                number = address.subThoroughfare

                return "$street, $number $city $country"
            }
        } catch (e: IOException) {
            showToast("Erreur durant la récupération de l'adresse provenant de l'emplacement",1)
        }

        return null
    }

    fun updateGroup() {
        if(binding.etUpdateGroupName.text.isBlank()) {
            showToast("Le titre doit être défini",1)
            return
        }

        try {
            val formatter = SimpleDateFormat(getString(R.string.date_format))
            val startDate = formatter.parse(binding.tvUpdateGroupStartDate.text.toString())!!
            val endDate = formatter.parse(binding.tvUpdateGroupEndDate.text.toString())!!
            val currentDate = Calendar.getInstance().time

            if (startDate.before(currentDate) || endDate.before(currentDate)) {
                showToast(
                    "La date de début et de fin doivent être supérieures ou égales à la date du jour",
                    1
                )
                return
            }

            if (startDate.after(endDate)) {
                showToast(
                    "La date de fin ne peut pas être antérieure à la date de début",
                    1
                )
                return
            }

            if (country == null || city == null || street == null || postalCode == null) {
                showToast("Adresse invalide", 1)
                return
            }

            val place = PlaceVM(street!!,number!!,postalCode!!,city!!,country!!,location!!)

            val groupVM = GroupVM(binding.etUpdateGroupName.text.toString(),binding.etUpdateGroupDescription.text.toString(),startDate,endDate,place)

            lifecycleScope.launch(Dispatchers.Default) {
                groupPresesenter.updateCurrentGroup(groupVM)
            }

        } catch (e: ParseException) {
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
            calendar.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(startDate!!)!!
        } else if(dateField == 1 && endDate != null) {
            calendar.time = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(endDate!!)!!
        }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val datePickerDialog = DatePickerDialog(
            requireView().context,
            DatePickerDialog.OnDateSetListener(::onDateSet),
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month+1, year)

        if(dateField == 0) {
            startDate = formattedDate
            binding.tvUpdateGroupStartDate.text = startDate
        } else if (dateField == 1) {
            endDate = formattedDate
            binding.tvUpdateGroupEndDate.text = endDate
        }
    }

    override fun onGroupUpdated() {
        MainScope().launch {
            showToast("Groupe mis à jour !", 0)
            findNavController().navigate(R.id.action_UpdateGroupFragment_to_homeFragment)
        }
    }

    override fun setCurrentGroup(groupVM: GroupVM) {
        MainScope().launch {
            binding.etUpdateGroupName.setText(groupVM.name)

            startDate = SimpleDateFormat("dd/MM/yyyy").format(groupVM.startDate)
            binding.tvUpdateGroupStartDate.setText(startDate)

            endDate = SimpleDateFormat("dd/MM/yyyy").format(groupVM.endDate)
            binding.tvUpdateGroupEndDate.setText(endDate)

            val placeVM: PlaceVM = groupVM.place

            country = placeVM.country
            city = placeVM.city
            street = placeVM.street
            number = placeVM.number
            postalCode = placeVM.postalCode
            location = LatLng(placeVM.latLng.latitude,placeVM.latLng.longitude)

            val address = "$street, $number $city $country"
            binding.tvUpdateGroupPlace.text = address

            binding.etUpdateGroupDescription.setText(groupVM.description)
        }
    }

    override fun showToast(message: String, length: Int) {
        MainScope().launch {
            Toast.makeText(context,message,length).show()
        }
    }

    companion object {
        const val TAG = "UpdateGroupFragment"

        fun newInstance(): UpdateGroupFragment {
            return UpdateGroupFragment()
        }
    }
}