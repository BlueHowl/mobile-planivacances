package be.helmo.planivacances.view.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentCreateGroupBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.ICreateGroupView
import be.helmo.planivacances.service.dto.GroupDTO
import be.helmo.planivacances.service.dto.PlaceDTO
import be.helmo.planivacances.view.MainActivity
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import com.adevinta.leku.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher


/**
 * Fragment de création de groupe
 */
class CreateGroupFragment : Fragment(), ICreateGroupView {

    lateinit var binding : FragmentCreateGroupBinding

    lateinit var groupPresenter : IGroupPresenter

    lateinit var lekuActivityResultLauncher: ActivityResultLauncher<Intent>

    var country: String? = null
    var city: String? = null
    var street: String? = null
    var number: String? = null
    var postalCode: String? = null
    var location: LatLng? = null

    var dateField: Int = 0
    var tempDate: String? = null
    var startDate: String? = null
    var endDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        groupPresenter = AppSingletonFactory.instance!!.getGroupPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateGroupBinding.inflate(inflater, container,false)

        binding.addGroupBtn.setOnClickListener {
            addGroup()
        }

        //back when backText is Clicked
        binding.tvBack.setOnClickListener {
            findNavController().navigate(R.id.action_createGroupFragment_to_homeFragment)
        }

        binding.tvGroupPlace.setOnClickListener {
            createLocationPickerDialog()
        }

        binding.tvGroupStartDate.setOnClickListener {
            dateField = 0
            createDateHourDialog()
        }

        binding.tvGroupEndDate.setOnClickListener {
            dateField = 1
            createDateHourDialog()
        }

        lekuActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("Location Picker result", "OK")
                    val data = result.data
                    val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
                    Log.d("LATITUDE", latitude.toString())
                    val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
                    Log.d("LONGITUDE", longitude.toString())
                    val postalCode = data?.getStringExtra(ZIPCODE)
                    Log.d("POSTALCODE", postalCode.toString())

                    val addressFormated =  getAddressFromLatLng(requireContext(), latitude!!, longitude!!)

                    val location = LatLng(latitude, longitude)
                    this.location = location
                    this.postalCode = postalCode
                    binding.tvGroupPlace.text = addressFormated

                } else {
                    Toast.makeText(context, "Erreur lors de la récupération de la localisation", Toast.LENGTH_LONG).show()
                }
            }

        return binding.root
    }

    fun getAddressFromLatLng(context: Context, lat: Double, lng: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1) as List<Address>

            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                country = address.countryName.trim()
                city = address.locality.trim()
                street = address.thoroughfare
                number = address.subThoroughfare

                return "$street, $number $city $country"
            }
        } catch (e: IOException) {
            Log.e("Geocoder", "Error getting address from location", e)
        }

        return null
    }

    /**
     * Prépare et vérifie les inputs et appel la création de groupe
     */
    fun addGroup() {
        if(binding.etGroupName.text.isEmpty()) {
            showToast("Le titre n'a pas été remplis")
            Log.w(TAG, "Le titre n'a pas été remplis")
            return
        }

        try {
            val formatter = SimpleDateFormat(getString(R.string.date_format))
            val startDate = formatter.parse(binding.tvGroupStartDate.text.toString())!!
            val endDate = formatter.parse(binding.tvGroupEndDate.text.toString())!!

            if(startDate.after(endDate)) {
                showToast("La date de fin ne peut pas être antérieur à la date de début")
                Log.w(TAG, "La date de fin ne peut pas être antérieur à la date de début")
                return
            }

            if(country == null || city == null || street == null || postalCode == null) {
                showToast("Addresse invalide")
                Log.w(TAG, "Addresse invalide")
                return
            }

            val place = PlaceDTO(country!!,
                city!!,
                street!!,
                number!!,
                postalCode!!,
                location!!.latitude,
                location!!.longitude)

            val group = GroupDTO(
                null,
                binding.etGroupName.text.toString(),
                binding.etGroupDescription.text.toString(),
                startDate,
                endDate,
                place
            )

            lifecycleScope.launch(Dispatchers.Default) {
                groupPresenter.createGroup(group)
            }


        }
        catch (e: ParseException) {
            showToast("Une des dates est mal encodée")
            Log.e(TAG, "Parse error : " + e.message)
        }
    }

    /**
     * Crée un dialog de récupération de lieu
     */
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
            DatePickerDialog.OnDateSetListener(::onDateSet),
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        tempDate = String.format("%02d/%02d/%d", dayOfMonth, month, year)

        if(dateField == 0) {
            startDate = tempDate
            binding.tvGroupStartDate.text = startDate
            Log.d("startDate", startDate!!)
        } else if (dateField == 1) {
            endDate = tempDate
            binding.tvGroupEndDate.text = endDate
            Log.d("endDate", endDate!!)
        }

        /*val timePickerDialog = TimePickerDialog(
            requireView().context, TimePickerDialog.OnTimeSetListener(::onTimeSet), hour, minute,
            DateFormat.is24HourFormat(requireView().context)
        )
        timePickerDialog.show()*/
    }
    /*fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if(dateField == 0) {
            startDateHour = String.format("%s %02d:%02d", tempDate, hourOfDay, minute)
            binding.tvGroupStartDate.text = startDateHour
            Log.d("startDateHour", startDateHour!!)
        } else if (dateField == 1) {
            endDateHour = String.format("%s %02d:%02d", tempDate, hourOfDay, minute)
            binding.tvGroupEndDate.text = endDateHour
            Log.d("startDateHour", endDateHour!!)
        }
    }*/

    override fun onGroupCreated() {
        showToast("Groupe créé !")
        findNavController().navigate(R.id.action_createGroupFragment_to_groupFragment)
    }

    /**
     * Affiche un message à l'écran
     */
    override fun showToast(message: String) {
        binding.pbCreateGroup.visibility = View.GONE
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val TAG = "CreateGroupFragment"

        fun newInstance(): CreateGroupFragment {
            return CreateGroupFragment()
        }
    }
}