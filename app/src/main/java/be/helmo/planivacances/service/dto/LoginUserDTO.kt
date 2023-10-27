package be.helmo.planivacances.service.dto

/**
 * Utilisateur de connexion
 */
data class LoginUserDTO(
    val mail: String? = null,
    val password: String? = null)
