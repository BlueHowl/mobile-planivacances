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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import be.helmo.planivacances.ApiClient
import be.helmo.planivacances.R
import be.helmo.planivacances.domain.LoginUser
import be.helmo.planivacances.domain.RegisterUser
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.factory.interfaces.IAuthCallback
import be.helmo.planivacances.view.interfaces.IAuthPresenter
import be.helmo.planivacances.view.interfaces.IAuthSucceededCallback
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth

    lateinit var signInLauncher: ActivityResultLauncher<Intent>

    lateinit var registerPanel : LinearLayout
    lateinit var loginPanel : LinearLayout
    var panelId : Int = 0

    lateinit var authPresenter: IAuthPresenter

    lateinit var authProgressBar : ProgressBar

    lateinit var sharedPreferences: SharedPreferences
    var keepConnected : CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        authPresenter = AppSingletonFactory.instance!!.getAuthPresenter()

        sharedPreferences = requireContext().getSharedPreferences("PlanivacancesPreferences", Context.MODE_PRIVATE)

        val authToken = sharedPreferences.getString("Auth_Token", null)
        if(authToken != null) {
            auth(authToken)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auth, container, false)

        val sun = view.findViewById<ImageView>(R.id.authSun)
        val sea = view.findViewById<ImageView>(R.id.authSea)

        val googleAuthButton = view.findViewById<Button>(R.id.btnAuthGoogle)

        val loginButton = view.findViewById<Button>(R.id.btnLogin)
        val loginMail = view.findViewById<EditText>(R.id.etLoginMail)
        val loginPassword = view.findViewById<EditText>(R.id.etLoginPassword)

        val registerButton = view.findViewById<Button>(R.id.btnRegister)
        val registerUsername = view.findViewById<EditText>(R.id.etRegisterName)
        val registerMail = view.findViewById<EditText>(R.id.etRegisterMail)
        val registerPassword = view.findViewById<EditText>(R.id.etRegisterPassword)

        registerPanel = view.findViewById(R.id.registerPanel)
        loginPanel = view.findViewById(R.id.loginPanel)

        val registerLink = view.findViewById<TextView>(R.id.tvRegisterHelper)
        val loginLink = view.findViewById<TextView>(R.id.tvLoginHelper)

        authProgressBar = view.findViewById(R.id.pbAuth)

        keepConnected = view.findViewById(R.id.cbKeepConnected)

        //background blur
        Glide.with(this)
            .load(R.drawable.sun) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(sun)

        Glide.with(this)
            .load(R.drawable.sea) // Replace with your image resource
            .transform(MultiTransformation(RoundedCorners(30), BlurTransformation(30)))
            .into(sea)

        //set registerPanel to false by default
        //registerPanel.visibility = View.GONE

        //Click listeners
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

        loginButton.setOnClickListener {
            val loginUser = LoginUser(loginMail.text.toString(), loginPassword.text.toString())
            login(loginUser)
        }

        registerButton.setOnClickListener {
            val registerUser = RegisterUser(registerUsername.text.toString(), registerMail.text.toString(), registerPassword.text.toString())
            register(registerUser)
        }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the result here if needed
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    // Handle sign-in failure (e.getStatusCode(), e.getStatusMessage())
                    Log.w(TAG, "Google Auth Failure " + e.getStatusCode() + " : " + e.getStatusMessage())
                }
            } else {
                Log.w(TAG, "Failed to auth with google")
            }
        }

        //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    fun startGoogleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        val signInIntent = mGoogleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        if (account != null) {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            mAuth.signInWithCredential(credential).addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    // Sign in success
                    auth(account.idToken)
                    Log.i(TAG, "Succefuly signed in (account token : ${account.idToken})")
                } else {
                    // Sign in failed
                    Log.w(TAG, "Failed signed in")
                }
            }
        } else {
            // Handle the case where acct is null
            Log.w(TAG, "Account is null")
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

    //todo put elsewhere than in view
    fun register(registerUser: RegisterUser) {
        hideKeyboard()
        authProgressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = ApiClient.authService.register(registerUser)

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()
                    auth(token)
                    Log.d(TAG, "Register Respone : ${token.toString()}")
                } else {
                    authProgressBar.visibility = View.GONE
                    showToast("Une erreur est survenue : ${response.message()}")
                }

            } catch (e: Exception) {
                authProgressBar.visibility = View.GONE
                showToast("Une erreur est survenue : ${e.message}")
            }
        }
    }

    //todo put elsewhere than in view
    fun login(loginUser: LoginUser) {
        hideKeyboard()
        authProgressBar.visibility = View.VISIBLE

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(loginUser.mail!!, loginUser.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result.user
                    user?.getIdToken(true)?.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val token = tokenTask.result?.token
                            auth(token)
                            Log.i(TAG, "Successfully signed in (account token: $token)")
                        } else {
                            // Handle the error in getting the ID token
                            val errorMessage = "Erreur lors de l'identification"
                            showToast(errorMessage)
                            authProgressBar.visibility = View.GONE
                            Log.w(TAG, errorMessage)
                        }
                    }
                } else {
                    // Handle the error in signing in
                    val errorMessage = "Erreur lors de la connexion"
                    showToast("Erreur lors de la connexion")
                    authProgressBar.visibility = View.GONE
                    Log.w(TAG, errorMessage)
                }
            }
    }

    fun auth(token: String?) {
        if(keepConnected != null && keepConnected!!.isChecked) {
            val editor = sharedPreferences.edit()
            editor.putString("Auth_Token", token)
            editor.apply()
        }

        AppSingletonFactory.instance?.setAuthToken(token)
        authPresenter.authSucceded() //appel à un évenement qui change de fragment
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    //vraiment utile ?
    fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = requireActivity().currentFocus
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    companion object {
        const val TAG = "AuthFragment"

        fun newInstance(): AuthFragment {
            return AuthFragment()
        }
    }
}