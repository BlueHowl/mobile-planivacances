package be.helmo.planivacances.view.fragments.weather

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.helmo.planivacances.R
import be.helmo.planivacances.domain.WeatherForecast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class WeatherAdapter(weatherList: List<WeatherForecast>, context: Context) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    private val weatherList: List<WeatherForecast>
    private val context: Context

    init {
        this.weatherList = weatherList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather: WeatherForecast = weatherList[position]
        holder.tvWeatherTemperature.text = weather.temperature
        holder.tvWeatherDate.text = weather.date
        holder.tvWeatherInfos.text = weather.infos

        Glide.with(context)
            .load(weather.imageUrl)
            .apply(RequestOptions().placeholder(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL))
            .into(holder.ivWeather)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivWeather: ImageView
        var tvWeatherTemperature: TextView
        var tvWeatherDate: TextView
        var tvWeatherInfos: TextView

        init {
            ivWeather = itemView.findViewById(R.id.ivWeather)
            tvWeatherTemperature = itemView.findViewById(R.id.tvWeatherTemperature)
            tvWeatherDate = itemView.findViewById(R.id.tvWeatherDate)
            tvWeatherInfos = itemView.findViewById(R.id.tvWeatherInfos)
        }
    }
}
