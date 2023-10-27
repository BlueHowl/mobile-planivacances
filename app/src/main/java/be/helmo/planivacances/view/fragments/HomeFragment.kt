package be.helmo.planivacances.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentCreateGroupBinding
import be.helmo.planivacances.databinding.FragmentHomeBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.view.interfaces.IGroupPresenter

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    lateinit var groupPresenter: IGroupPresenter

    lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //prevent back button
        requireActivity().onBackPressedDispatcher.addCallback(this) {}

        groupPresenter = AppSingletonFactory.instance!!.getGroupPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container,false)

        binding.addGroupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createGroupFragment)
        }

        binding.notificationBtn.setOnClickListener {
            if(binding.rvGroupInvites.visibility == View.GONE) {
                //todo verify if there are notification to show before showing list
                binding.rvGroupInvites.visibility = View.VISIBLE
            } else {
                binding.rvGroupInvites.visibility = View.GONE
            }
        }

        return binding.root
    }

    companion object {
        const val TAG = "HomeFragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}