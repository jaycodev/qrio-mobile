package com.cibertec.qriomobile.data.repository

import com.cibertec.qriomobile.data.model.OfferItemDto
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.remote.apiCall

class OfferRepository(private val api: ApiService) {
    suspend fun getOffers(): NetworkResult<List<OfferItemDto>> =
        apiCall { api.getOffers() }
        
    suspend fun getOffersByRestaurant(restaurantId: Long): NetworkResult<List<OfferItemDto>> =
        apiCall { api.getOffersByRestaurant(restaurantId) }
}
