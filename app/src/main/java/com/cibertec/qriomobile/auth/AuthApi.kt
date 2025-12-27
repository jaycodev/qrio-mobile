package com.cibertec.qriomobile.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.Date

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val accessToken: String)
data class TokenInfoResponse(
    val subject: String,
    val role: String?,
    val customerId: Long?,
    val email: String?,
    val name: String?,
    val issuedAt: Date?,
    val expiration: Date?
)

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("/auth/customer/login")
    suspend fun customerLogin(@Body body: LoginRequest): Response<LoginResponse>

    @GET("/auth/token-info")
    suspend fun tokenInfo(): Response<TokenInfoResponse>

    @POST("/auth/logout")
    suspend fun logout(): Response<Unit>
}
