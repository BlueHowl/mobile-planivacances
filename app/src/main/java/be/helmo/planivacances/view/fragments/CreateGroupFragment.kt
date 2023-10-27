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
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


/**
 * Fragment de création de groupe
 */
class CreateGroupFragment : Fragment() {

    lateinit var binding : FragmentCreateGroupBinding

    lateinit var groupPresenter : IGroupPresenter

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

    /**
     * Crée un groupe
     * @param group (CreateGroupDTO) contient les informations du groupe
     */
    fun createGroup(group: CreateGroupDTO) {
        hideKeyboard()
        binding.pbCreateGroup.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Main) {
            val result = groupPresenter.createGroup(group)

            if (result.success) {
                showToast(result.message!!)
                findNavController().navigate(R.id.action_createGroupFragment_to_homeFragment)
            } else {
                binding.pbCreateGroup.visibility = View.GONE
                showToast(result.message!!)
            }
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