package be.helmo.planivacances.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.addCallback
import androidx.recyclerview.widget.RecyclerView
import be.helmo.planivacances.R
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.view.interfaces.IGroupPresenter

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    lateinit var groupPresenter: IGroupPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //prevent back button
        requireActivity().onBackPressedDispatcher.addCallback(this) {}

        groupPresenter = AppSingletonFactory.instance!!.getGroupPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val groupInviteRV = view.findViewById<RecyclerView>(R.id.rvGroupInvites)
        //set group invite recyclerView to gone by default
        //groupInviteRV.visibility = View.GONE

        val btnAddGroup = view.findViewById<ImageButton>(R.id.addGroupBtn)

        val btnNotification = view.findViewById<ImageButton>(R.id.notificationBtn)

        btnAddGroup.setOnClickListener {
            groupPresenter.createGroupClick()
        }

        btnNotification.setOnClickListener {
            if(groupInviteRV.visibility == View.GONE) {
                //todo verify if there are notification to show before showing list
                groupInviteRV.visibility = View.VISIBLE
            } else {
                groupInviteRV.visibility = View.GONE
            }
        }

        return view
    }

    companion object {
        const val TAG = "HomeFragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}