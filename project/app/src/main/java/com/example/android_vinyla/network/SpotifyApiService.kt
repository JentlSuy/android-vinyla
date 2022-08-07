package com.example.android_vinyla.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


private const val BASE_URL_SPOTIFY_API = "https://api.spotify.com/"
private const val BASE_URL_SPOTIFY_TOKEN = "https://open.spotify.com/"
private var _SPOTIFY_TOKEN: String = ""

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

var client = OkHttpClient.Builder().addInterceptor { chain ->
    val newRequest = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $_SPOTIFY_TOKEN")
        .build()
    chain.proceed(newRequest)
}.build()

private val retrofitSpotify = Retrofit.Builder()
    .client(client)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL_SPOTIFY_API)
    .build()

private val retrofitToken = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL_SPOTIFY_TOKEN)
    .build()

interface SpotifyApiService {
    @GET("v1/search?type=artist&decorate_restrictions=false&include_external=audio&limit=1")
    fun getArtistDetails(
        @Query("q") artistName: String
    ): Deferred<SpotifyArtistDetailsProperty>

}

object SpotifyApi {
    val retrofitService: SpotifyApiService by lazy {
        retrofitSpotify.create(SpotifyApiService::class.java)
    }
}

interface SpotifyApiGetTokenService {
    @GET("get_access_token?reason=transport&productType=web_player")
    fun getSpotifyToken(): Deferred<SpotifyTokenProperty>
}

object SpotifyApiGetToken {
    val retrofitService: SpotifyApiGetTokenService by lazy {
        retrofitToken.create(SpotifyApiGetTokenService::class.java)
    }

    fun setSpotifyToken(token: String) {
        _SPOTIFY_TOKEN = token
        Log.i("SpotifyApiService", "Spotify Token has been set: " + _SPOTIFY_TOKEN)
    }

    fun getSpotifyToken(): String {
        return _SPOTIFY_TOKEN
    }
}