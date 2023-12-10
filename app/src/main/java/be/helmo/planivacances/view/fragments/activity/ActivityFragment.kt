package be.helmo.planivacances.view.fragments.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentActivityBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.IActivityView
import be.helmo.planivacances.presenter.viewmodel.ActivityDetailVM
import be.helmo.planivacances.view.interfaces.IActivityPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ActivityFragment : Fragment(), IActivityView {
    lateinit var binding: FragmentActivityBinding
    lateinit var activityPresenter : IActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPresenter = AppSingletonFactory.instance!!.getActivityPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActivityBinding.inflate(inflater,container,false)

        binding.tvBackToCalendar.setOnClickListener {
            findNavController().navigate(R.id.action_ActivityFragment_to_CalendarFragment)
        }

        lifecycleScope.launch(Dispatchers.Default) {
            activityPresenter.loadCurrentActivity()
        }
        return binding.root
    }

    override fun loadActivity(activity: ActivityDetailVM) {
        MainScope().launch {
            binding.tvActivityName.text = activity.activityTitle
            binding.tvActivityPeriod.text = activity.activityPeriod
            binding.tvActivityPlace.text = activity.activityPlace
            binding.tvActivityDescription.text = activity.activityDescription
        }
    }

    override fun showToast(message: String, length: Int) {
        MainScope().launch {
            Toast.makeText(context,message,length).show()
        }
    }

    companion object {
        const val TAG = "ActivityFragment"

        fun newInstance(): ActivityFragment {
            return ActivityFragment()
        }
    }
}