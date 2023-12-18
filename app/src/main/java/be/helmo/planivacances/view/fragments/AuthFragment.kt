package be.helmo.planivacances.view.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import be.helmo.planivacances.BuildConfig
import be.helmo.planivacances.databinding.FragmentAuthBinding
import be.helmo.planivacances.R
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.presenter.interfaces.IAuthView
import be.helmo.planivacances.view.MainActivity
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


/**
 * Fragment d'authentification (login et register)
 */
class AuthFragment : Fragment(), IAuthView {

    lateinit var mAuth: FirebaseAuth

    lateinit var signInLauncher: ActivityResultLauncher<Intent>

    lateinit var binding : FragmentAuthBinding

    var panelId : Int = 0

    lateinit var authPresenter: IAuthPresenter


    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        authPresenter = AppSingletonFactory.instance!!.getAuthPresenter(this)

        sharedPreferences = requireContext().getSharedPreferences("PlanivacancesPreferences",
                                                                  Context.MODE_PRIVATE)
        authPresenter.setSharedPreference(sharedPreferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAuthBinding.inflate(inflater, container,false)

        //Chargement du début : autoAuth
        binding.pbAuth.visibility = View.VISIBLE
        autoAuth()

        //background blur
        Glide.with(this)
            .load(R.drawable.sun) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(25),
                                           BlurTransformation(20)))
            .into(binding.authSun)

        Glide.with(this)
            .load(R.drawable.sea) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(30),
                                           BlurTransformation(30)))
            .into(binding.authSea)

        //Click listeners
        binding.tvRegisterHelper.setOnClickListener {
            switchAuthPanel()
        }

        binding.tvLoginHelper.setOnClickListener {
            switchAuthPanel()
        }

        binding.btnAuthGoogle.setOnClickListener {
            startGoogleAuth()
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.btnRegister.setOnClickListener {
            register()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken,null)

                    mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity()) { t ->
                            if (t.isSuccessful) {
                                // Sign in success
                                Log.d(TAG, "signInWithCredential:success")

                                lifecycleScope.launch(Dispatchers.Default) {
                                    val r = authPresenter.initAuthenticator()

                                    if(r) { goToHome() }
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", t.exception)
                                // Handle sign-in failure here.
                            }
                        }

                } catch (e: ApiException) {
                    Log.w(TAG, "Google Auth Failure " + e.statusCode + " : " + e.message)
                }
            } else {
                Log.w(TAG, "Failed to auth with google")
            }
        }

    }

    /**
     * lance l'activité d'authentification google
     */
    fun startGoogleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.OAUTH_CLIENT_ID)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val signInIntent = mGoogleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    /**
     * Change le panneau d'authentification (login ou register)
     */
    fun switchAuthPanel() {
        panelId = ++panelId % 2

        if(panelId == 0) {
            binding.registerPanel.visibility = View.GONE
            binding.loginPanel.visibility = View.VISIBLE
        } else {
            binding.registerPanel.visibility = View.VISIBLE
            binding.loginPanel.visibility = View.GONE
        }
    }

    /**
     * Appel la fonction d'enregistrement asynchrone
     */
    fun register() {
        hideKeyboard()
        binding.pbAuth.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Default) {
            authPresenter.register(
                binding.etRegisterName.text.toString(),
                binding.etRegisterMail.text.toString(),
                binding.etRegisterPassword.text.toString())
        }

    }

    /**
     * Appel la fonction de connexion asynchrone
     */
    fun login() {
        hideKeyboard()
        binding.pbAuth.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Default) {
            authPresenter.login(
                binding.etLoginMail.text.toString(),
                binding.etLoginPassword.text.toString(),
                binding.cbKeepConnected.isChecked)
        }

    }

    /**
     * Appel la fonction d'authentification automatique asynchrone
     */
    fun autoAuth() {
        lifecycleScope.launch(Dispatchers.Default) {
            authPresenter.autoAuth()
        }
    }

    /**
     * navigue vers le fragment home
     */
    override fun goToHome() {
        MainScope().launch {
            findNavController().navigate(R.id.action_authFragment_to_homeFragment)
        }
    }

    /**
     * Affiche un message à l'écran
     * @param message (String)
     * @param length (Int) 0 = short, 1 = long
     */
    override fun showToast(message: String, length: Int) {
        MainScope().launch {
            binding.pbAuth.visibility = View.GONE
            Toast.makeText(context, message, length).show()
        }
    }

    override fun stopLoading() {
        MainScope().launch {
            binding.pbAuth.visibility = View.GONE
        }
    }

    /**
     * Appel la fonction qui cache le clavier
     */
    fun hideKeyboard() {
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.hideKeyboard()
        }
    }

    companion object {
        const val TAG = "AuthFragment"

        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}