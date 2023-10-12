package be.helmo.planivancances

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import be.helmo.planivancances.factory.ServiceSingletonFactory
import be.helmo.planivancances.view.auth.interfaces.IAuthService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import jp.wasabeef.glide.transformations.BlurTransformation


class AuthFragment : Fragment() {

    lateinit var authService: IAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = ServiceSingletonFactory.instance!!.getAuthService()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.auth_fragment, container, false)

        val sun = view.findViewById<ImageView>(R.id.authSun)
        val sea = view.findViewById<ImageView>(R.id.authSea)

        Glide.with(this)
            .load(R.drawable.sun) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(sun)

        Glide.with(this)
            .load(R.drawable.sea) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(30), BlurTransformation(30)))
            .into(sea)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AuthFragment", "onViewCreated called")

        //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    companion object {
        const val TAG = "AuthFragment"
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}