package be.helmo.planivacances.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentGroupBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.IGroupView
import be.helmo.planivacances.presenter.viewmodel.GroupDetailVM
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [GroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupFragment : Fragment(), IGroupView {

    lateinit var binding : FragmentGroupBinding

    lateinit var groupPresenter : IGroupPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        groupPresenter = AppSingletonFactory.instance!!.getGroupPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGroupBinding.inflate(inflater, container,false)

        binding.deleteGroupBtn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Default) {
                groupPresenter.deleteCurrentGroup()
            }
        }

        lifecycleScope.launch(Dispatchers.Default) {
            groupPresenter.showGroupInfos()
        }

        binding.ibWeather.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_weatherFragment)
        }

        binding.ibCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_calendarFragment)
        }

        binding.ibItinerary.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Default) {
                groupPresenter.loadItinerary()
            }
        }

        binding.ibTchat.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_tchatFragment)
        }

        binding.tvBack.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_homeFragment)
        }

        return binding.root
    }

    companion object {
        const val TAG = "GroupFragment"

        fun newInstance(): GroupFragment {
            return GroupFragment()
        }
    }

    override fun buildItinerary(latitude: String, longitude: String) {
        MainScope().launch {
            val mapsUri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"
            )

            val mapIntent = Intent(Intent.ACTION_VIEW, mapsUri)

            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
            } else {
                showToast(
                    "L'application Google Maps doit être installée pour pouvoir utiliser cette fonctionnalité !",
                    1
                )
            }
        }
    }

    override fun setGroupInfos(group: GroupDetailVM) {
        MainScope().launch {
            binding.tvGroupName.text = group.groupName
            binding.tvGroupDescription.text = group.description
            binding.tvGroupPeriod.text = group.period
            binding.tvGroupPlace.text = group.address
        }
    }

    override fun onGroupDeleted() {
        MainScope().launch {
            showToast("Le groupe a bien été supprimé", 1)
            findNavController().navigate(R.id.action_groupFragment_to_homeFragment)
        }
    }

    override fun showToast(message: String,length:Int) {
        MainScope().launch {
            Toast.makeText(context, message, length).show()
        }
    }
}