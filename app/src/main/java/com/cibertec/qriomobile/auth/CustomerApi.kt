package com.cibertec.qriomobile.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Request para registrar cliente

data class CreateCustomerRequest(
    val name: String,
    val email: String,
    val phone: String?,
    val password: String,
    val status: String? = null // backend por defecto ACTIVO si null
)

// Respuesta breve (id/name/email/status) acorde a CustomerListResponse

data class CustomerListResponse(
    val id: Long,
    val name: String,
    val email: String,
    val status: String,
    val createdAt: String?
)

interface CustomerApi {
    @POST("/customers")
    suspend fun create(@Body body: CreateCustomerRequest): Response<CustomerListResponse>
}
