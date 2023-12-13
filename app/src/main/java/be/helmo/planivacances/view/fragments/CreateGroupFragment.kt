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
import be.helmo.planivacances.domain.Group
import be.helmo.planivacances.domain.Place
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.ICreateGroupView
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import com.adevinta.leku.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


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
                    binding.tvGroupPlace.text = addressFormated

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

    /**
     * Prépare et vérifie les inputs et appel la création de groupe
     */
    fun addGroup() {
        if(binding.etGroupName.text.isBlank()) {
            showToast("Le titre n'a pas été rempli", 1)
            return
        }

        try {
            val formatter = SimpleDateFormat(getString(R.string.date_format))
            val startDate = formatter.parse(binding.tvGroupStartDate.text.toString())!!
            val endDate = formatter.parse(binding.tvGroupEndDate.text.toString())!!
            val currentDate = Calendar.getInstance().time

            if(startDate.before(currentDate) || endDate.before(currentDate)) {
                showToast("La date de début et de fin doivent être supérieures ou égales à la date du jour",1)
                return
            }

            if(startDate.after(endDate)) {
                showToast(
                    "La date de fin ne peut pas être antérieure à la date de début",
                    1
                )
                return
            }

            if(country == null || city == null || street == null || postalCode == null) {
                showToast("Adresse invalide", 1)
                return
            }

            val place = Place(
                country!!,
                city!!,
                street!!,
                number!!,
                postalCode!!,
                location!!
            )

            val group = Group(
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
            showToast("Une des dates est mal encodée", 1)
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
        val formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month+1, year)

        if(dateField == 0) {
            startDate = formattedDate
            binding.tvGroupStartDate.text = startDate
        } else if (dateField == 1) {
            endDate = formattedDate
            binding.tvGroupEndDate.text = endDate
        }
    }

    override fun onGroupCreated() {
        MainScope().launch {
            showToast("Groupe créé !", 0)
            findNavController().navigate(R.id.action_createGroupFragment_to_groupFragment)
        }
    }

    /**
     * Affiche un message à l'écran
     * @param message (String)
     * @param length (Int) 0 = short, 1 = long
     */
    override fun showToast(message: String, length: Int) {
        MainScope().launch {
            binding.pbCreateGroup.visibility = View.GONE
            Toast.makeText(context, message, length).show()
        }
    }

    companion object {
        const val TAG = "CreateGroupFragment"

        fun newInstance(): CreateGroupFragment {
            return CreateGroupFragment()
        }
    }
}