package com.cibertec.qriomobile.data.model

data class ApiSuccess<T>(
    val message: String,
    val data: T
)
