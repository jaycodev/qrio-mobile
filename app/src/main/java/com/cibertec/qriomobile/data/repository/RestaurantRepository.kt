package com.cibertec.qriomobile.data.repository

import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.apiCall

class RestaurantRepository(private val api: ApiService) {
    suspend fun getRestaurants(): NetworkResult<List<com.cibertec.qriomobile.data.model.RestaurantDto>> =
        apiCall { api.getRestaurants() }

    suspend fun getRestaurantById(id: Long): NetworkResult<com.cibertec.qriomobile.data.model.RestaurantDto> =
        apiCall { api.getRestaurantById(id) }
}
