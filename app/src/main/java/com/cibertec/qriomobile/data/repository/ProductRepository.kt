package com.cibertec.qriomobile.data.repository

import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.apiCall

class ProductRepository(private val api: ApiService) {
	suspend fun getProductsByBranch(branchId: Long): NetworkResult<List<com.cibertec.qriomobile.data.model.ProductDto>> =
		apiCall { api.getProductsByBranch(branchId) }

	suspend fun getProductById(id: Long): NetworkResult<com.cibertec.qriomobile.data.model.ProductDto> =
		apiCall { api.getProductById(id) }
}
