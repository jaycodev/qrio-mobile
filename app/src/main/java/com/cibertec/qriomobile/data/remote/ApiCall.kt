package com.cibertec.qriomobile.data.remote

import com.cibertec.qriomobile.data.model.ApiSuccess
import retrofit2.Response

suspend fun <T> apiCall(block: suspend () -> Response<ApiSuccess<T>>): NetworkResult<T> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body.data)
            } else {
                NetworkResult.Error(response.code(), "Respuesta vacía")
            }
        } else {
            NetworkResult.Error(response.code(), response.errorBody()?.string())
        }
    } catch (t: Throwable) {
        NetworkResult.Exception(t)
    }
}
