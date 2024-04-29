package com.dldev.tcom.network

import com.dldev.tcom.network.models.LoginResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserApiService {
    @POST("login")
    @Headers("Content-Type: application/json", "Accept: application/json", "X-api-key: vekq8ne97uryr3mj4iudv8um07ggmhcat874q96jzvyypabgrm3zhyrwcgybm4hk")
    suspend fun login(@Body email: Map<String, String>): LoginResponse
}