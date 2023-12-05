package be.helmo.planivacances.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentGroupBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.IGroupView
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import java.text.SimpleDateFormat

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

        val group = groupPresenter.getCurrentGroup()!!

        val formatter = SimpleDateFormat(getString(R.string.date_format))
        val startDate = formatter.format(group.startDate)
        val endDate = formatter.format(group.endDate)

        binding.tvGroupName.text = group.groupName
        binding.tvGroupDescription.text = group.description
        binding.tvGroupPeriod.text = "Du $startDate au $endDate"
        binding.tvGroupPlace.text = groupPresenter.getCurrentGroupPlace()?.address


        binding.ibWeather.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_weatherFragment)
        }

        binding.ibCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_calendarFragment)
        }

        binding.ibItinerary.setOnClickListener {
            groupPresenter.loadItinerary()
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
        val mapsUri: Uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")

        val mapIntent = Intent(Intent.ACTION_VIEW,mapsUri)

        mapIntent.setPackage("com.google.android.apps.maps")

        if(mapIntent.resolveActivity(requireActivity().packageManager) != null)
        {
            startActivity(mapIntent)
        } else {
            showToast("L'application Google Maps doit être installée pour pouvoir utiliser cette fonctionnalité !")
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}