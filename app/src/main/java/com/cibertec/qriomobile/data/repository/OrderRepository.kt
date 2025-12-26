package com.cibertec.qriomobile.data.repository

import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.apiCall

class OrderRepository(private val api: ApiService) {
    suspend fun createOrder(order: com.cibertec.qriomobile.data.model.CreateOrderRequestDto): NetworkResult<com.cibertec.qriomobile.data.model.OrderDto> =
        apiCall { api.createOrder(order) }

    suspend fun getOrderById(id: Long): NetworkResult<com.cibertec.qriomobile.data.model.OrderDetailDto> =
        apiCall { api.getOrderById(id) }
}
