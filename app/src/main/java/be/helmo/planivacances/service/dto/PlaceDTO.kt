package be.helmo.planivacances.service.dto

data class PlaceDTO(val country : String,
                    val city : String,
                    val street : String,
                    val number : String,
                    val postalCode : String,
                    val lat: Double,
                    val lon: Double)
