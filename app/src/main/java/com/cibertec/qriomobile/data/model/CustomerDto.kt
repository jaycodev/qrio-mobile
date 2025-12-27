package com.cibertec.qriomobile.data.model

data class CustomerDto(
    val id: Long? = null,
    val firebaseUid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val status: String? = "ACTIVO",
    val createdAt: String? = null

)
