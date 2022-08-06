package com.example.android_vinyla.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://vinyla-api.azurewebsites.net/api/"
private var _BEARER_TOKEN: String = ""
private var _EMAIL: String = ""

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface VinylaApiService {

    @GET("Users")
    fun getAll(@Query("email") type: String): Deferred<List<UserProperty>>

    @GET("Account/checkusername")
    fun checkEmailInUse(@Query("email") type: String): Deferred<String>

    @POST("Account")
    fun login(@Body body: LoginRequestProperty): Deferred<String>

    @POST("Account/register")
    fun register(@Body body: RegisterRequestProperty): Deferred<String>
}

object VinylaApi {
    val retrofitService: VinylaApiService by lazy {
        retrofit.create(VinylaApiService::class.java)
    }

    fun setBearerToken(token: String) {
        _BEARER_TOKEN = token
        Log.i("VinylaApiService", "Token has been set: " + _BEARER_TOKEN)

    }

    fun getBearerToken(): String {
        return _BEARER_TOKEN
    }

    fun setEmail(email: String) {
        _EMAIL = email
        Log.i("VinylaApiService", "Email has been set: " + _EMAIL)
    }

    fun getEmail(): String {
        return _EMAIL
    }
}