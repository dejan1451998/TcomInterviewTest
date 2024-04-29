package com.dldev.tcom.network

import com.dldev.tcom.network.models.Vehicle
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface VehicleApiService {
    @GET("allVehicles")
    @Headers("Content-Type: application/json", "Accept: application/json", "Authorization: Bearer ", "X-api-key: vekq8ne97uryr3mj4iudv8um07ggmhcat874q96jzvyypabgrm3zhyrwcgybm4hk")
    suspend fun getAllVehicles(): Response<List<Vehicle>>

    @FormUrlEncoded
    @POST("addToFavorites")
    suspend fun addToFavorites(@Field("vehicleID") vehicleID: Int): Response<ResponseBody>

    @FormUrlEncoded
    @POST("addToFavorites")
    suspend fun removeFromFavorites(@Field("vehicleID") vehicleID: Int): Response<ResponseBody>

    @GET("vehicle")
    suspend fun getVehicle(@Query("vehicleID") vehicleID: Int): Response<Vehicle>
}