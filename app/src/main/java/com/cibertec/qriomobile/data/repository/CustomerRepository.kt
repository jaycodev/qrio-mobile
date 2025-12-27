package com.cibertec.qriomobile.data.repository

import com.cibertec.qriomobile.data.model.CustomerDto
import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.apiCall

class CustomerRepository(private val api: ApiService) {
    suspend fun getByFirebaseUid(uid: String): NetworkResult<CustomerDto> =
        apiCall { api.getCustomerByFirebaseUid(uid) }


    suspend fun getById(id: Long): NetworkResult<CustomerDto> =
        apiCall { api.getCustomerById(id) }

    suspend fun create(customer: CustomerDto): NetworkResult<CustomerDto> =
        apiCall { api.createCustomer(customer) }

    suspend fun update(id: Long, customer: CustomerDto): NetworkResult<CustomerDto> =
        apiCall { api.updateCustomer(id, customer) }
}
