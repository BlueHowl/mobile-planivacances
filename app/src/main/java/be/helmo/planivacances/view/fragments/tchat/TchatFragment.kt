package be.helmo.planivacances.view.fragments.tchat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentTchatBinding
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.ITchatView
import be.helmo.planivacances.service.dto.MessageDTO
import be.helmo.planivacances.view.interfaces.ITchatPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [TchatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TchatFragment : Fragment(), ITchatView {

    lateinit var binding : FragmentTchatBinding

    lateinit var tchatPresenter: ITchatPresenter

    lateinit var tchatAdapter: TchatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tchatPresenter = AppSingletonFactory.instance!!.getTchatPresenter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentTchatBinding.inflate(inflater, container, false)

        Glide.with(this)
            .load(R.drawable.sun)
            .transform(MultiTransformation(RoundedCorners(25),
                       BlurTransformation(20)))
            .into(binding.tchatSun)

        Glide.with(this)
            .load(R.drawable.palmtree)
            .transform(MultiTransformation(RoundedCorners(25),
                       BlurTransformation(20)))
            .into(binding.tchatPalmTree)

        binding.tvBack.setOnClickListener {
            findNavController().navigate(R.id.action_tchatFragment_to_groupFragment)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            initTchatComponents()
            tchatPresenter.connectToTchat()
        }

        return binding.root
    }

    fun initTchatComponents() {
        MainScope().launch {
            tchatAdapter = TchatAdapter()
            val layoutManager : LinearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager.isSmoothScrollbarEnabled = true
            binding.rvTchatContainer.layoutManager = layoutManager
            binding.rvTchatContainer.adapter = tchatAdapter

            binding.ibSendTchat.setOnClickListener {
                val message: String = binding.etTchatSendText.text.toString().trim()

                if (message.isNotEmpty()) {
                    binding.etTchatSendText.text.clear()
                    tchatPresenter.sendMessage(message)
                }
            }
        }
    }

    override fun addMessageToView(message: MessageDTO) {
       MainScope().launch {
           tchatAdapter?.addMessage(message)
           scrollToBottom()
       }
    }

    private fun scrollToBottom() {
        val layoutManager = binding.rvTchatContainer.layoutManager as LinearLayoutManager
        layoutManager.smoothScrollToPosition(binding.rvTchatContainer,
                                       null,
                                     tchatAdapter.itemCount - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TchatFragment","onDestroy called")
        tchatPresenter.disconnectToTchat()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("TchatFragment","onDetach called")
        tchatPresenter.disconnectToTchat()
    }

    companion object {
        const val TAG = "TchatFragment"

        fun newInstance(): TchatFragment {
            return TchatFragment()
        }
    }
}