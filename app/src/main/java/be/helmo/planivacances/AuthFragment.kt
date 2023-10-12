package be.helmo.planivacances

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import be.helmo.planivacances.factory.ServiceSingletonFactory
import be.helmo.planivacances.view.auth.interfaces.IAuthService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import jp.wasabeef.glide.transformations.BlurTransformation


class AuthFragment : Fragment() {

    lateinit var authService: IAuthService

    lateinit var mAuth: FirebaseAuth

    lateinit var registerPanel : LinearLayout
    lateinit var loginPanel : LinearLayout
    var panelId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = ServiceSingletonFactory.instance!!.getAuthService()

        mAuth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.auth_fragment, container, false)

        val sun = view.findViewById<ImageView>(R.id.authSun)
        val sea = view.findViewById<ImageView>(R.id.authSea)

        val googleAuthButton = view.findViewById<Button>(R.id.btnAuthGoogle)

        registerPanel = view.findViewById<LinearLayout>(R.id.registerPanel)
        loginPanel = view.findViewById<LinearLayout>(R.id.loginPanel)

        val registerLink = view.findViewById<TextView>(R.id.tvRegisterHelper)
        val loginLink = view.findViewById<TextView>(R.id.tvLoginHelper)

        Glide.with(this)
            .load(R.drawable.sun) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(sun)

        Glide.with(this)
            .load(R.drawable.sea) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(30), BlurTransformation(30)))
            .into(sea)

        registerPanel.visibility = View.GONE

        registerLink.setOnClickListener {
            switchAuthPanel()
        }

        loginLink.setOnClickListener {
            switchAuthPanel()
        }

        // Set a click listener on the button
        googleAuthButton.setOnClickListener {
            startGoogleAuth()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AuthFragment", "onViewCreated called")

        //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Handle sign-in failure (e.getStatusCode(), e.getStatusMessage())
            }
        }
    }

    fun startGoogleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        if (acct != null) {
            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        Log.d("Tag", "Succefuly signed in")
                    } else {
                        // Sign in failed
                    }
                }
        } else {
            // Handle the case where acct is null
        }
    }

    fun switchAuthPanel() {
        panelId = ++panelId % 2;

        if(panelId == 0) {
            registerPanel.visibility = View.GONE
            loginPanel.visibility = View.VISIBLE
        } else {
            registerPanel.visibility = View.VISIBLE
            loginPanel.visibility = View.GONE
        }
    }

    companion object {
        const val TAG = "AuthFragment"
        const val RC_SIGN_IN = 9999
        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}