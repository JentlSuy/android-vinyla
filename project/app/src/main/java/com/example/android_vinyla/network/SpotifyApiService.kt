package com.example.android_vinyla.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL_SPOTIFY_API = "https://api.spotify.com/"
private const val BASE_URL_SPOTIFY_TOKEN = "https://open.spotify.com/"
private var _SPOTIFY_TOKEN: String = ""

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofitSpotify = Retrofit.Builder()
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
    @GET("v1/")
    fun getArtistDetails():Deferred<ArtistDetailsProperty>

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

    fun setSpotifyToken(token:String){
        _SPOTIFY_TOKEN = token
        Log.i("SpotifyApiService", "Spotify Token has been set: " + _SPOTIFY_TOKEN)
    }
}