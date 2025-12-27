package com.cibertec.qriomobile.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.Date

// Compatibilidad: enviar ambos campos para servidores que esperan 'firebaseToken'
data class FirebaseLoginRequest(val idToken: String? = null, val firebaseToken: String? = null)
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
    suspend fun loginWithFirebase(@Body body: FirebaseLoginRequest, @retrofit2.http.Header("X-Firebase-IdToken") idTokenHeader: String? = null): Response<LoginResponse>

    @GET("/auth/token-info")
    suspend fun tokenInfo(): Response<TokenInfoResponse>
}
