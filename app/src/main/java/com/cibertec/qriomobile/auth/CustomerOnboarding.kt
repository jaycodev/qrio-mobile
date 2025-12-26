package com.cibertec.qriomobile.auth

import com.cibertec.qriomobile.data.model.CustomerDto
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.data.remote.api.ApiService
import com.cibertec.qriomobile.data.repository.CustomerRepository

/**
 * Helper para asegurar que el Customer exista en el backend.
 * Llamar después del login con Firebase.
 */
class CustomerOnboarding(api: ApiService) {
    private val repo = CustomerRepository(api)

    /**
     * Intenta obtener el cliente por firebase UID; si no existe, lo crea.
     * @return NetworkResult con el Customer final (existente o recién creado).
     */
    suspend fun ensureCustomerExists(
        firebaseUid: String,
        name: String?,
        email: String?,
        phone: String?
    ): NetworkResult<CustomerDto> {
        return when (val found = repo.getByFirebaseUid(firebaseUid)) {
            is NetworkResult.Success -> found
            is NetworkResult.Error -> {
                // Si es 404, crear; cualquier otro error, propagar
                if (found.code == 404) {
                    val newCustomer = CustomerDto(
                        firebase_uid = firebaseUid,
                        name = name,
                        email = email,
                        phone = phone,
                        status = "ACTIVO"
                    )
                    repo.create(newCustomer)
                } else found
            }
            is NetworkResult.Exception -> found
        }
    }
}
