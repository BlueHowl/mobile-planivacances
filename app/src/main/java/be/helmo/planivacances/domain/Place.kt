package be.helmo.planivacances.domain

import com.google.android.gms.maps.model.LatLng

data class Place(val address : String, val latLng: LatLng) {
    val latLngString: String
        get() = "${latLng.latitude},${latLng.longitude}"
}
