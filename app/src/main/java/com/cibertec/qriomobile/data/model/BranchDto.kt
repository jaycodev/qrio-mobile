package com.cibertec.qriomobile.data.model

data class BranchDto(
    val id: Long? = null,

    val name: String,
    val address: String? = null,
    val phone: String? = null,
    val schedule: String? = null,
    val created_at: String? = null
)
