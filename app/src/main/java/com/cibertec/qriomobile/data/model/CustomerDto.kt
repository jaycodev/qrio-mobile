package com.cibertec.qriomobile.data.model

data class CustomerDto(
    val id: Long? = null,
    val firebase_uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val status: String? = "ACTIVO",
)
