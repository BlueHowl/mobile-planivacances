package be.helmo.planivacances.view

import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import be.helmo.planivacances.R
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.factory.interfaces.IAuthCallback
import be.helmo.planivacances.factory.interfaces.ICreateGroupCallback
import be.helmo.planivacances.view.fragments.AuthFragment
import be.helmo.planivacances.view.fragments.CreateGroupFragment
import be.helmo.planivacances.view.fragments.HomeFragment

class MainActivity : AppCompatActivity(), IAuthCallback, ICreateGroupCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appContext = applicationContext


        AppSingletonFactory.instance!!.getAuthSucceededCallback().setAuthCallback(this)

        AppSingletonFactory.instance!!.getCreateGroupClickCallback().setCreateGroupCallback(this)


        //affiche le premier fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, AuthFragment.newInstance()).commit()

    }

    /**
     * Affiche la vue principale
     */
    override fun onAuthSucceeded() {
        val fragment = HomeFragment.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    /**
     * Affiche la vue de création de groupe de vacance
     */
    override fun showCreateGroup() {
        val fragment = CreateGroupFragment.newInstance()
        val fragmentManager: FragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    /**
     * Permet de récupèrer le context
     */
    companion object {

        lateinit  var appContext: Context //todo nécessaire ? car requireContext() existe

    }
}