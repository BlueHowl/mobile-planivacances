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
import be.helmo.planivacances.presenter.viewmodel.GroupListItemVM
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.IHomeView
import be.helmo.planivacances.presenter.viewmodel.GroupInvitationVM
import be.helmo.planivacances.view.interfaces.IGroupPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), IHomeView {

    lateinit var binding: FragmentHomeBinding

    lateinit var groupPresenter: IGroupPresenter

    lateinit var groupAdapter: GroupAdapter

    lateinit var groupInviteAdapter: GroupInviteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //prevent back button
        requireActivity().onBackPressedDispatcher.addCallback(this) {}

        groupPresenter = AppSingletonFactory.instance!!.getGroupPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container,false)

        initGroupInviteAdapter()

        binding.addGroupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createGroupFragment)
        }

        binding.notificationBtn.setOnClickListener {
            if(binding.rvGroupInvites.visibility == View.GONE && groupInviteAdapter.itemCount > 0) {
                binding.rvGroupInvites.visibility = View.VISIBLE
            } else {
                binding.rvGroupInvites.visibility = View.GONE
            }
            if(groupInviteAdapter.itemCount == 0) {
                showToast("Aucune notification", 1)
            }
        }

        binding.pbGroupList.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.Default) {
            groupPresenter.loadUserGroups()
        }

        return binding.root
    }

    override fun setGroupList(groups: List<GroupListItemVM>) {
        MainScope().launch {
            setGroupsAdapter(groups)

            binding.pbGroupList.visibility = View.GONE
        }
    }

    override fun onGroupInvitationsLoaded(invitations: List<GroupInvitationVM>) {
        MainScope().launch {
            groupInviteAdapter.clearInvitationsList()
            for(invitation in invitations) {
                groupInviteAdapter.addGroupInvitation(invitation)
            }
            if(invitations.isNotEmpty()) {
                binding.notificationDot.visibility = View.VISIBLE
            } else {
                binding.notificationDot.visibility = View.GONE
            }
        }
    }

    override fun onGroupInvitationAccepted() {
        MainScope().launch {
            showToast("Invitation acceptée avec succès",1)
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onGroupInvitationDeclined() {
        MainScope().launch {
            showToast("Invitation refusée avec succès",1)
            findNavController().navigate(R.id.homeFragment)
        }
    }

    fun initGroupInviteAdapter() {
        binding.rvGroupInvites.layoutManager = LinearLayoutManager(requireContext())
        groupInviteAdapter = GroupInviteAdapter {gid,accepted ->
            if(accepted) {
                lifecycleScope.launch(Dispatchers.Default) {
                    groupPresenter.acceptGroupInvite(gid)
                }
            } else {
                lifecycleScope.launch(Dispatchers.Default) {
                    groupPresenter.declineGroupInvite(gid)
                }
            }
        }
        binding.rvGroupInvites.adapter = groupInviteAdapter

        lifecycleScope.launch(Dispatchers.Default) {
            groupPresenter.loadUserGroupInvites()
        }
    }

    fun setGroupsAdapter(groups : List<GroupListItemVM>) {
        binding.rvGroups.layoutManager = LinearLayoutManager(requireContext())
        groupAdapter = GroupAdapter(requireContext(), groups) { selectedGroupId ->
            //selectionne le groupe
            groupPresenter.setCurrentGroupId(selectedGroupId)
            findNavController().navigate(R.id.action_homeFragment_to_groupFragment)
        }
        binding.rvGroups.adapter = groupAdapter
    }

    /**
     * Affiche un message à l'écran
     * @param message (String)
     * @param length (Int) 0 = short, 1 = long
     */
    override fun showToast(message: String, length: Int) {
        MainScope().launch {
            Toast.makeText(context, message, length).show()
        }
    }

    companion object {
        const val TAG = "HomeFragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}