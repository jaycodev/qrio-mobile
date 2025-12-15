package com.cibertec.qriomobile.data.remote.api

import com.cibertec.qriomobile.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // -----------------------------
    // RESTAURANTS
    // -----------------------------
    @GET("/restaurants")
    suspend fun getRestaurants(): Response<List<RestaurantDto>>

    @GET("/restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: Int): Response<RestaurantDto>

    @POST("/restaurants")
    suspend fun createRestaurant(@Body restaurant: RestaurantDto): Response<RestaurantDto>

    @PUT("/restaurants/{id}")
    suspend fun updateRestaurant(
        @Path("id") id: Int,
        @Body restaurant: RestaurantDto
    ): Response<RestaurantDto>


    // -----------------------------
    // OFFERS
    // -----------------------------
    @GET("/offers")
    suspend fun getOffers(): Response<List<OfferDto>>

    @GET("/offers/{id}")
    suspend fun getOfferById(@Path("id") id: Int): Response<OfferDto>

    @POST("/offers")
    suspend fun createOffer(@Body offer: OfferDto): Response<OfferDto>

    @PUT("/offers/{id}")
    suspend fun updateOffer(
        @Path("id") id: Int,
        @Body offer: OfferDto
    ): Response<OfferDto>



    // -----------------------------
    // CUSTOMERS
    // -----------------------------
    @GET("/customers")
    suspend fun getCustomers(): Response<List<CustomerDto>>

    @GET("/customers/{id}")
    suspend fun getCustomerById(@Path("id") id: Int): Response<CustomerDto>

    @POST("/customers")
    suspend fun createCustomer(@Body customer: CustomerDto): Response<CustomerDto>

    @PUT("/customers/{id}")
    suspend fun updateCustomer(
        @Path("id") id: Int,
        @Body customer: CustomerDto
    ): Response<CustomerDto>

    @GET("/customers/firebase/{uid}")
    suspend fun getCustomerByFirebaseUid(@Path("uid") uid: String): Response<CustomerDto>



    // -----------------------------
    // PRODUCTS
    // -----------------------------
    @GET("/products")
    suspend fun getProducts(): Response<List<ProductDto>>

    @GET("/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDto>

    @POST("/products")
    suspend fun createProduct(@Body product: ProductDto): Response<ProductDto>

    @PUT("/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: ProductDto
    ): Response<ProductDto>


    // -----------------------------
    // BRANCHES
    // -----------------------------
    @GET("/branches")
    suspend fun getBranches(): Response<List<BranchDto>>

    @GET("/branches/{id}")
    suspend fun getBranchById(@Path("id") id: Int): Response<BranchDto>

    @POST("/branches")
    suspend fun createBranch(@Body branch: BranchDto): Response<BranchDto>

    @PUT("/branches/{id}")
    suspend fun updateBranch(
        @Path("id") id: Int,
        @Body branch: BranchDto
    ): Response<BranchDto>


    // -----------------------------
    // CATEGORIES
    // -----------------------------
    @GET("/categories")
    suspend fun getCategories(): Response<List<CategoryDto>>

    @GET("/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): Response<CategoryDto>

    @POST("/categories")
    suspend fun createCategory(@Body category: CategoryDto): Response<CategoryDto>

    @PUT("/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body category: CategoryDto
    ): Response<CategoryDto>


    // -----------------------------
    // ORDERS
    // -----------------------------
    @GET("/orders")
    suspend fun getOrders(): Response<List<OrderDto>>

    @GET("/orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<OrderDto>

    @POST("/orders")
    suspend fun createOrder(@Body order: OrderDto): Response<OrderDto>

    @PUT("/orders/{id}")
    suspend fun updateOrder(
        @Path("id") id: Int,
        @Body order: OrderDto
    ): Response<OrderDto>



    // -----------------------------
    // PAYMENT METHODS
    // -----------------------------
    @GET("/payment-methods")
    suspend fun getPaymentMethods(): Response<List<PaymentMethodDto>>

    @GET("/payment-methods/{id}")
    suspend fun getPaymentMethodById(@Path("id") id: Int): Response<PaymentMethodDto>

    @POST("/payment-methods")
    suspend fun createPaymentMethod(
        @Body method: PaymentMethodDto
    ): Response<PaymentMethodDto>

    @PUT("/payment-methods/{id}")
    suspend fun updatePaymentMethod(
        @Path("id") id: Int,
        @Body method: PaymentMethodDto
    ): Response<PaymentMethodDto>
}
