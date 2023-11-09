package be.helmo.planivacances.view.fragments.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.helmo.planivacances.R
import be.helmo.planivacances.databinding.FragmentWeatherBinding
import be.helmo.planivacances.domain.WeatherForecast
import be.helmo.planivacances.factory.AppSingletonFactory
import be.helmo.planivacances.view.interfaces.IWeatherPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [WeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherFragment : Fragment() {

    lateinit var binding : FragmentWeatherBinding

    lateinit var weatherPresenter: IWeatherPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weatherPresenter = AppSingletonFactory.instance!!.getWeatherPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWeatherBinding.inflate(inflater, container,false)

        Glide.with(this)
            .load(R.drawable.sun)
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(binding.weatherSun)

        Glide.with(this)
            .load(R.drawable.palmtree)
            .transform(MultiTransformation(RoundedCorners(25), BlurTransformation(20)))
            .into(binding.weatherPalmTree)

        binding.tvBack.setOnClickListener {
            findNavController().navigate(R.id.action_weatherFragment_to_groupFragment)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val result = weatherPresenter.getForecast()

            if (result.success) {
                val weatherList: List<WeatherForecast> = result.message as List<WeatherForecast>
                val adapter = WeatherAdapter(weatherList, requireContext())

                binding.rvWeatherContainer.layoutManager = LinearLayoutManager(requireContext())
                binding.rvWeatherContainer.adapter = adapter
            } else {
                showToast(result.message!! as String)
            }
        }

        return binding.root
    }

    /**
     * Affiche un message à l'écran
     */
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val TAG = "WeatherFragment"

        fun newInstance(): WeatherFragment {
            return WeatherFragment()
        }
    }
}