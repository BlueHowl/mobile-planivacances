package be.helmo.planivacances.util

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    fun formatTimestampForDisplay(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd/MM/yyyy - HH'h'mm", Locale.getDefault())

        return format.format(date)
    }
}