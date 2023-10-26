package be.helmo.planivacances.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.lifecycleScope
import be.helmo.planivacances.R
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.dto.CreateGroupDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [CreateGroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateGroupFragment : Fragment() {

    lateinit var createGroupProgressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_group, container, false)

        val addGroupBtn = view.findViewById<ImageButton>(R.id.addGroupBtn)
        val groupName = view.findViewById<EditText>(R.id.etGroupName)
        val startDate = view.findViewById<TextView>(R.id.tvGroupStartDate)
        val endDate = view.findViewById<TextView>(R.id.tvGroupEndDate)
        val groupPlace = view.findViewById<Spinner>(R.id.groupPlaceSpinner)
        val groupDescription = view.findViewById<EditText>(R.id.etGroupDescription)

        val backButton = view.findViewById<TextView>(R.id.tvBack)

        createGroupProgressBar = view.findViewById(R.id.pbCreateGroup)


        addGroupBtn.setOnClickListener {
            val group = CreateGroupDTO(
                groupName.text.toString(),
                groupDescription.text.toString(),
                Date("22/01/2024 12:12:12"),
                Date("22/01/2024 12:12:12"),
                //Date(startDate.text.toString()),
                //Date(endDate.text.toString()),
                "1")

            createGroup(group)
        }

        //back when backText is Clicked
        backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return view
    }

    fun createGroup(group: CreateGroupDTO) {
        hideKeyboard()
        createGroupProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = ApiClient.groupService.create(AppSingletonFactory.instance?.getAuthToken()!!, group)

                if (response.isSuccessful && response.body() != null) {
                    val delay = response.body()
                    Log.d(TAG, "Group created : $delay")
                    createGroupProgressBar.visibility = View.GONE //todo enlever si transition vers une autre vue
                } else {
                    createGroupProgressBar.visibility = View.GONE
                    showToast("Erreur lors de la création du groupe ${response.message()}")
                    Log.d(TAG, "${response.message()}, ${response.isSuccessful()}, ${AppSingletonFactory.instance?.getAuthToken()!!}")
                }

            } catch (e: Exception) {
                createGroupProgressBar.visibility = View.GONE
                showToast("Erreur durant la création du groupe : ${e.message}")
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    //todo vraiment utile ?
    fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = requireActivity().currentFocus
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    companion object {
        const val TAG = "CreateGroupFragment"

        fun newInstance(): CreateGroupFragment {
            return CreateGroupFragment()
        }
    }
}