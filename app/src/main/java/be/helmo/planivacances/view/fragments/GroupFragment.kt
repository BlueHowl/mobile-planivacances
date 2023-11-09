package be.helmo.planivacances.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentGroupBinding

/**
 * A simple [Fragment] subclass.
 * Use the [GroupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GroupFragment : Fragment() {

    lateinit var binding : FragmentGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentGroupBinding.inflate(inflater, container,false)


        binding.ibWeather.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_weatherFragment)
        }

        binding.ibCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_calendarFragment)
        }

        binding.ibItinerary.setOnClickListener {

        }

        binding.ibTchat.setOnClickListener {
            findNavController().navigate(R.id.action_groupFragment_to_tchatFragment)
        }

        return binding.root
    }

    companion object {
        const val TAG = "GroupFragment"

        fun newInstance(): GroupFragment {
            return GroupFragment()
        }
    }
}