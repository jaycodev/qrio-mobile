package com.cibertec.qriomobile.data.model

data class AuthResponse(
    val token: String,
    val customerId: Long,
    val email: String,
    val name: String?,
    val isNew: Boolean
)
