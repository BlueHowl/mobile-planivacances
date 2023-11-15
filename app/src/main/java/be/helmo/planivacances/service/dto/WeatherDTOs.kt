package be.helmo.planivacances.service.dto

data class WeatherData(
    val forecast: Forecast
)

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day
)

data class Day(
    val avgtemp_c: Double,
    val condition: Condition,
    val avghumidity: Double,
    val daily_chance_of_rain: Int,
    val maxwind_kph: Double
)

data class Condition(
    val icon: String
)
