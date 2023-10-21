package be.helmo.planivacances

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tchat, container, false)

        val sun = view.findViewById<ImageView>(R.id.tchatSun)
        val palmTree = view.findViewById<ImageView>(R.id.tchatPalmTree)

        Glide.with(this)
            .load(R.drawable.sun) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(sun)

        Glide.with(this)
            .load(R.drawable.palmtree) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(palmTree)


        return view
    }

    companion object {
        const val TAG = "TchatFragment"

        fun newInstance(): TchatFragment {
            return TchatFragment()
        }
    }
}