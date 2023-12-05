package be.helmo.planivacances.domain

/**
 * Utilisateur d'enregistrement
 */
data class RegisterUser(
    val username: String? = null,
    val mail: String? = null,
    val password: String? = null)
