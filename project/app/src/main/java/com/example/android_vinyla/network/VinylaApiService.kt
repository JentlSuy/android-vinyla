package com.example.android_vinyla.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://vinyla-api.azurewebsites.net/api/"
var BEARER_TOKEN: String = ""
var EMAIL: String = ""

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface VinylaApiService {

    @GET("Users")
    fun getAll(@Query("email") type: String): Deferred<String>

    @GET("Users?")
    fun getByUser(): Call<String>

    @GET("Account/checkusername")
    fun checkEmailInUse(@Query("email") type: String): Deferred<String>

    @POST("Account")
    fun login(@Body body: LoginRequestProperty): Deferred<String>
}

object VinylaApi {
    val retrofitService: VinylaApiService by lazy {
        retrofit.create(VinylaApiService::class.java)
    }

    fun setBearerToken(token: String) {
        BEARER_TOKEN = token
        // Log.i("VinylaApiService", "Token has been set: " + BEARER_TOKEN)

    }

    fun setEmail(email: String) {
        EMAIL = email
        // Log.i("VinylaApiService", "Token has been set: " + BEARER_TOKEN)
    }

    fun getEmail(): String {
        return EMAIL
    }
}