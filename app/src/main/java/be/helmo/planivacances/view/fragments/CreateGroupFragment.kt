package be.helmo.planivacances.view.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentCreateGroupBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.service.ApiClient
import be.helmo.planivacances.service.dto.CreateGroupDTO
import be.helmo.planivacances.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [CreateGroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateGroupFragment : Fragment() {

    lateinit var binding : FragmentCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateGroupBinding.inflate(inflater, container,false)

        binding.addGroupBtn.setOnClickListener {
            val group = CreateGroupDTO(
                binding.etGroupName.text.toString(),
                binding.etGroupDescription.text.toString(),
                Date("22/01/2024 12:12:12"),
                Date("22/01/2024 12:12:12"),
                //Date(startDate.text.toString()),
                //Date(endDate.text.toString()),
                "1")

            createGroup(group)
        }

        //back when backText is Clicked
        binding.tvBack.setOnClickListener {
            //requireActivity().onBackPressedDispatcher.onBackPressed()
            findNavController().navigate(R.id.action_createGroupFragment_to_homeFragment)
        }

        return binding.root
    }

    fun createGroup(group: CreateGroupDTO) {
        hideKeyboard()
        binding.pbCreateGroup.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = ApiClient.groupService.create(AppSingletonFactory.instance?.getAuthToken()!!, group)

                if (response.isSuccessful && response.body() != null) {
                    val delay = response.body()
                    Log.d(TAG, "Group created : $delay")
                    findNavController().navigate(R.id.action_createGroupFragment_to_homeFragment)
                } else {
                    binding.pbCreateGroup.visibility = View.GONE
                    showToast("Erreur lors de la création du groupe ${response.message()}")
                    Log.d(TAG, "${response.message()}, ${response.isSuccessful()}, ${AppSingletonFactory.instance?.getAuthToken()!!}")
                }

            } catch (e: Exception) {
                binding.pbCreateGroup.visibility = View.GONE
                showToast("Erreur durant la création du groupe : ${e.message}")
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

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