package be.helmo.planivacances.service

interface IdTokenCallback {
    fun onIdTokenResult(token: String)
}