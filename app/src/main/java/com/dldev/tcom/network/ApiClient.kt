package com.dldev.tcom.network

import com.dldev.tcom.App
import com.dldev.tcom.view_model.SharedPreferencesHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://zadatak.tcom.rs/zadatak/public/api/"

    fun createHttpClient(): OkHttpClient {
        val context = App.instance.getAppContext()


        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                var token = SharedPreferencesHelper.getLoginToken(context)
                val original = chain.request()

                val request = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("X-api-key", "vekq8ne97uryr3mj4iudv8um07ggmhcat874q96jzvyypabgrm3zhyrwcgybm4hk")
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(createHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val vehicleApiService: VehicleApiService by lazy {
        retrofit.create(VehicleApiService::class.java)
    }
}