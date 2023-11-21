package be.helmo.planivacances.domain

import com.google.android.gms.maps.model.LatLng

data class Place(val country : String,
                 val city : String,
                 val street : String,
                 val number : String,
                 val postalCode : String,
                 val latLng: LatLng) {

    val latLngString: String
        get() = "${latLng.latitude},${latLng.longitude}"

    val address: String
        get() = "$street, $number, $city $postalCode, $country"
}
