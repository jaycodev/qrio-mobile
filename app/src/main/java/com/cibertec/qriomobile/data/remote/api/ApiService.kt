
    package com.cibertec.qriomobile.data.remote.api

    import com.cibertec.qriomobile.data.model.*
    import retrofit2.Response
    import retrofit2.http.*

    interface ApiService {

        // -----------------------------
        // RESTAURANTES (cliente)
        // -----------------------------
        @GET("/restaurants")
        suspend fun getRestaurants(): Response<ApiSuccess<List<RestaurantDto>>>

        @GET("/restaurants/{id}")
        suspend fun getRestaurantById(@Path("id") id: Long): Response<ApiSuccess<RestaurantDto>>

        // -----------------------------
        // SUCURSALES (por restaurante)
        // -----------------------------
        @GET("/branches")
        suspend fun getBranchesByRestaurant(@Query("restaurantId") restaurantId: Long): Response<ApiSuccess<List<BranchDto>>>

        @GET("/branches/{id}")
        suspend fun getBranchById(@Path("id") id: Long): Response<ApiSuccess<BranchDto>>

        // -----------------------------
        // PRODUCTOS (por sucursal)
        // -----------------------------
        @GET("/products")
        suspend fun getProductsByBranch(@Query("branchId") branchId: Long): Response<ApiSuccess<List<ProductDto>>>

        @GET("/products/{id}")
        suspend fun getProductById(@Path("id") id: Long): Response<ApiSuccess<ProductDto>>

        // -----------------------------
        // AUTH (Firebase)
        // -----------------------------
        @POST("auth/firebase")
        suspend fun firebaseAuth(
            @Body request: FirebaseAuthRequest
        ): Response<AuthResponse>

        // -----------------------------
        // CLIENTE (Customers)
        // -----------------------------
        @GET("/customers/me")
        suspend fun getMyProfile(): Response<ApiSuccess<CustomerDto>>
        @GET("/customers/firebase/{uid}")
        suspend fun getCustomerByFirebaseUid(@Path("uid") uid: String): Response<ApiSuccess<CustomerDto>>

        @POST("/customers")
        suspend fun createCustomer(@Body customer: CustomerDto): Response<ApiSuccess<CustomerDto>>

        @PUT("/customers/{id}")
        suspend fun updateCustomer(
            @Path("id") id: Long,
            @Body customer: CustomerDto
        ): Response<ApiSuccess<CustomerDto>>

        // CLIENTE (Customers)
        @GET("/customers/{id}")
        suspend fun getCustomerById(
            @Path("id") id: Long
        ): Response<ApiSuccess<CustomerDto>>

        @GET("/auth/me")
        suspend fun getMe(): Response<com.cibertec.qriomobile.data.model.MeResponse>


        // -----------------------------
        // MÉTODOS DE PAGO (por cliente)
        // -----------------------------
        @GET("/payment-methods")
        suspend fun getPaymentMethodsByCustomer(@Query("customerId") customerId: Long): Response<ApiSuccess<List<PaymentMethodDto>>>

        @POST("/payment-methods")
        suspend fun createPaymentMethod(@Body method: PaymentMethodDto): Response<ApiSuccess<PaymentMethodDto>>

        // -----------------------------
        // PEDIDOS (Orders)
        // -----------------------------
        @POST("/orders")
        suspend fun createOrder(@Body order: CreateOrderRequestDto): Response<ApiSuccess<OrderDto>>

        @GET("/orders/{id}")
        suspend fun getOrderById(@Path("id") id: Long): Response<ApiSuccess<OrderDetailDto>>

        // -----------------------------
        // OFERTAS / PROMOCIONES
        // -----------------------------
        @GET("/offers")
        suspend fun getOffers(): Response<ApiSuccess<List<com.cibertec.qriomobile.data.model.OfferItemDto>>>

        @GET("/offers/restaurant/{restaurantId}")
        suspend fun getOffersByRestaurant(
            @Path("restaurantId") restaurantId: Long
        ): Response<ApiSuccess<List<com.cibertec.qriomobile.data.model.OfferItemDto>>>
    }

