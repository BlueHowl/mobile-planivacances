package be.helmo.planivacances.view.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentHomeBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.util.ResultMessage
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    lateinit var groupPresenter: IGroupPresenter
    lateinit var authPresenter: IAuthPresenter

    lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //prevent back button
        requireActivity().onBackPressedDispatcher.addCallback(this) {}

        groupPresenter = AppSingletonFactory.instance!!.getGroupPresenter()
        authPresenter = AppSingletonFactory.instance!!.getAuthPresenter()
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

        binding.pbGroupList.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Main) {
            var groups = groupPresenter.getGroups()
            var result = ResultMessage(true, null)

            //charge une seule fois
            if(groups.isEmpty()) {
                result = groupPresenter.loadUserGroups(authPresenter.getUid())
            }

            if (result.success) {
                groups = groupPresenter.getGroups()

                binding.rvGroups.layoutManager = LinearLayoutManager(requireContext())
                groupAdapter = GroupAdapter(requireContext(), groups) { selectedGroupId ->
                    //selectionne le groupe
                    groupPresenter.setCurrentGroupId(selectedGroupId)
                    findNavController().navigate(R.id.action_homeFragment_to_groupFragment)
                }
                binding.rvGroups.adapter = groupAdapter

                binding.pbGroupList.visibility = View.GONE

            } else {
                showToast(result.message!! as String)
            }
        }

        return binding.root
    }

    /**
     * Affiche un message à l'écran
     */
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val TAG = "HomeFragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}