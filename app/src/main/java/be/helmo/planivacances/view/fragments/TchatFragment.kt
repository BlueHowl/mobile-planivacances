package be.helmo.planivacances.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentTchatBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import jp.wasabeef.glide.transformations.BlurTransformation

/**
 * A simple [Fragment] subclass.
 * Use the [TchatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TchatFragment : Fragment() {

    lateinit var binding : FragmentTchatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentTchatBinding.inflate(inflater, container,false)

        Glide.with(this)
            .load(R.drawable.sun)
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(binding.tchatSun)

        Glide.with(this)
            .load(R.drawable.palmtree)
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(binding.tchatPalmTree)


        return view
    }

    companion object {
        const val TAG = "TchatFragment"

        fun newInstance(): TchatFragment {
            return TchatFragment()
        }
    }
}