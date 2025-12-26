package com.cibertec.qriomobile.data.repository

import com.cibertec.qriomobile.data.model.PaymentMethodDto
import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.apiCall

class PaymentMethodRepository(private val api: ApiService) {
    suspend fun getPaymentMethodsByCustomer(customerId: Long): NetworkResult<List<PaymentMethodDto>> =
        apiCall { api.getPaymentMethodsByCustomer(customerId) }

    suspend fun createPaymentMethod(method: PaymentMethodDto): NetworkResult<PaymentMethodDto> =
        apiCall { api.createPaymentMethod(method) }
}
