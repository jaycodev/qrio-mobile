package com.cibertec.qriomobile.data.repository

import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.apiCall

class BranchRepository(private val api: ApiService) {
    suspend fun getBranchesByRestaurant(restaurantId: Long): NetworkResult<List<com.cibertec.qriomobile.data.model.BranchDto>> =
        apiCall { api.getBranchesByRestaurant(restaurantId) }

    suspend fun getBranchById(id: Long): NetworkResult<com.cibertec.qriomobile.data.model.BranchDto> =
        apiCall { api.getBranchById(id) }
}
