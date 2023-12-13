package be.helmo.planivacances.presenter.viewmodel

import com.google.android.gms.maps.model.LatLng

data class PlaceVM(val street : String,
                   val number : String,
                   val postalCode : String,
                   val city : String,
                   val country: String,
                   val latLng: LatLng
)