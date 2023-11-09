package be.helmo.planivacances.view.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
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
import be.helmo.planivacances.service.dto.CreateGroupDTO
import be.helmo.planivacances.service.dto.PlaceDTO
import be.helmo.planivacances.view.MainActivity
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import com.adevinta.leku.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Fragment de création de groupe
 */
class CreateGroupFragment : Fragment() {

    lateinit var binding : FragmentCreateGroupBinding

    lateinit var groupPresenter : IGroupPresenter

    lateinit var lekuActivityResultLauncher: ActivityResultLauncher<Intent>

    var address: String? = null
    var location: LatLng? = null

    var dateField: Int = 0
    var tempDate: String? = null
    var startDateHour: String? = null
    var endDateHour: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        groupPresenter = AppSingletonFactory.instance!!.getGroupPresenter()
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

                    val address = data?.getStringExtra(LOCATION_ADDRESS)
                    Log.d("ADDRESS", address.toString())
                    val postalcode = data?.getStringExtra(ZIPCODE)
                    Log.d("POSTALCODE", postalcode.toString())

                    val addressFormated = String.format("%s %s", address.toString(), postalcode.toString())

                    val location = LatLng(latitude!!, longitude!!)

                    this.location = location
                    this.address = addressFormated
                    binding.tvGroupPlace.text = addressFormated

                } else {
                    Toast.makeText(context, "Erreur lors de la récupération de la localisation", Toast.LENGTH_LONG).show()
                }
            }

        return binding.root
    }

    /**
     * Prépare et vérifie les inputs et appel la création de groupe
     */
    fun addGroup() {
        if(binding.etGroupName.text.isNotEmpty() && binding.etGroupDescription.text.isNotEmpty()) {
            try {
                val formatter = SimpleDateFormat(getString(R.string.date_format))
                val startDate = formatter.parse(binding.tvGroupStartDate.text.toString())!!
                val endDate = formatter.parse(binding.tvGroupEndDate.text.toString())!!

                if(startDate.before(endDate)) {
                    val group = CreateGroupDTO(
                        binding.etGroupName.text.toString(),
                        binding.etGroupDescription.text.toString(),
                        startDate,
                        endDate
                    )

                    createGroup(
                        group,
                        PlaceDTO(address!!, location!!.latitude, location!!.longitude)
                    )
                } else {
                    showToast("La date de fin ne peut pas être antérieur à la date de début")
                    Log.w(TAG, "La date de fin ne peut pas être antérieur à la date de début")
                }
            }
            catch (e: ParseException) {
                showToast("Une des dates est mal encodée")
                Log.e(TAG, "Parse error : " + e.message)
            }
        } else {
            showToast("Le titre ou la description n'ont pas été remplis")
            Log.w(TAG, "Le titre ou la description n'ont pas été remplis")
        }
    }

    /**
     * Crée un groupe
     * @param group (CreateGroupDTO) contient les informations du groupe
     */
    fun createGroup(group: CreateGroupDTO, place: PlaceDTO) {
        hideKeyboard()
        binding.pbCreateGroup.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Main) {
            val result = groupPresenter.createGroup(group, place)

            if (result.success) {
                showToast(result.message!! as String)
                findNavController().navigate(R.id.action_createGroupFragment_to_groupFragment)
            } else {
                binding.pbCreateGroup.visibility = View.GONE
                showToast(result.message!! as String)
            }
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

        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireView().context, TimePickerDialog.OnTimeSetListener(::onTimeSet), hour, minute,
            DateFormat.is24HourFormat(requireView().context)
        )
        timePickerDialog.show()
    }
    fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if(dateField == 0) {
            startDateHour = String.format("%s %02d:%02d", tempDate, hourOfDay, minute)
            binding.tvGroupStartDate.text = startDateHour
            Log.d("startDateHour", startDateHour!!)
        } else if (dateField == 1) {
            endDateHour = String.format("%s %02d:%02d", tempDate, hourOfDay, minute)
            binding.tvGroupEndDate.text = endDateHour
            Log.d("startDateHour", endDateHour!!)
        }
    }

    /**
     * Affiche un message à l'écran
     */
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Appel la fonction qui cache le clavier
     */
    fun hideKeyboard() {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.hideKeyboard()
        }
    }

    companion object {
        const val TAG = "CreateGroupFragment"

        fun newInstance(): CreateGroupFragment {
            return CreateGroupFragment()
        }
    }
}