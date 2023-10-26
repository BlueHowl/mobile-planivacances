package be.helmo.planivacances.service.dto

data class RegisterUserDTO(
    val username: String? = null,
    val mail: String? = null,
    val password: String? = null)
