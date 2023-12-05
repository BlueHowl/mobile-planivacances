package be.helmo.planivacances.domain

/**
 * Utilisateur de connexion
 */
data class LoginUser(
    val mail: String? = null,
    val password: String? = null)
