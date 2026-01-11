package com.cibertec.qriomobile.data

import android.util.Log
import com.cibertec.qriomobile.BuildConfig
import com.cibertec.qriomobile.auth.AuthRepository
import com.cibertec.qriomobile.data.remote.api.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {


    // Proveedor de token (inyectar desde capa de auth Firebase)
    @Volatile
    private var authTokenProvider: (() -> String?)? = null

    fun setAuthTokenProvider(provider: () -> String?) {
        authTokenProvider = provider
    }

    private val authInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()

            Log.d("API", "Request a: ${original.url}")

            // Primero intenta con el proveedor inyectado, sino usa AuthRepository directamente
            val token = authTokenProvider?.invoke() ?: AuthRepository.getToken()
            val request = if (!token.isNullOrBlank()) {
                original.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else original
            return chain.proceed(request)
        }
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Exponer creación genérica de servicios (e.g., AuthApi)
    fun <T> create(service: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }

    // Enlazar proveedor de token con AuthManager automáticamente
    init {
        setAuthTokenProvider { com.cibertec.qriomobile.auth.AuthRepository.getToken() }
    }
}
