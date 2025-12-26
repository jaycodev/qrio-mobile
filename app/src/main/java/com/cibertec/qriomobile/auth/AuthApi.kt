package com.cibertec.qriomobile.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.Date

data class FirebaseLoginRequest(val idToken: String)
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
    @POST("/auth/firebase")
    suspend fun loginWithFirebase(@Body body: FirebaseLoginRequest): Response<LoginResponse>

    @GET("/auth/token-info")
    suspend fun tokenInfo(): Response<TokenInfoResponse>
}
