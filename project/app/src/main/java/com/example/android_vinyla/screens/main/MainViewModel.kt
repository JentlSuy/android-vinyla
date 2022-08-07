package com.example.android_vinyla.screens.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_vinyla.network.SpotifyApi
import com.example.android_vinyla.network.SpotifyApiGetToken
import com.example.android_vinyla.network.SpotifyTokenProperty
import com.example.android_vinyla.network.VinylaApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class VinylaApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

    // SELF SELF
    private val _response = MutableLiveData<String>()
    val response: LiveData<String> get() = _response

    private var _spotifyTokenProperty = MutableLiveData<SpotifyTokenProperty>()
    val spotifyTokenProperty: LiveData<SpotifyTokenProperty> get() = _spotifyTokenProperty

    private var sortedMapAlbumCount: List<Map.Entry<String, Int>> = ArrayList()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getSpotifyToken()
    }

    private fun getSpotifyToken() {
        coroutineScope.launch {
            var getTokenDeferred = SpotifyApiGetToken.retrofitService.getSpotifyToken()
            try {
                var result = getTokenDeferred.await()
                _spotifyTokenProperty.value = result
                SpotifyApiGetToken.setSpotifyToken(_spotifyTokenProperty.value!!.accessToken)
                //_token.value = "Success- Token = ${result.accessToken}"
            } catch (t: Throwable) {
                Log.i("MainViewModel", "Failure: " + t.message)
            }
            getVinylaProperties()
        }
    }

    private fun getVinylaProperties() {
        coroutineScope.launch {
            var getPropertiesDeferred = VinylaApi.retrofitService.getAll(VinylaApi.getEmail())
            try {
                var result = getPropertiesDeferred.await()

                val frequencyMap: MutableMap<String, Int> = HashMap()

                for (album in result.get(0).albums) {
                    var count = frequencyMap[album.artistName]
                    if (count == null) count = 0
                    frequencyMap[album.artistName] = count + 1
                }

                sortedMapAlbumCount =
                    frequencyMap.toList().sortedBy { (_, key) -> key }.toMap().asIterable()
                        .reversed()

                var responseString = ""

                for ((key, value) in sortedMapAlbumCount) {
                    responseString += "$key - $value\n"
                }

                _response.value = "$responseString"

                Log.i("MainViewModel", "Artists found: " + sortedMapAlbumCount.size)

                //_response.value = "Success: ${result.get(0).albums.size} albums retrieved!"

                getArtistImageUrls()

            } catch (t: Throwable) {
                // TODO EMPTY LIST AT REGISTER
                _response.value = "Failure: " + t.message
            }
        }

    }

    private fun getArtistImageUrls() {
        Log.i("MainViewModel", "STARTING URL RETRIEVAL...")
        val artistImageUrls: MutableMap<String, String> = HashMap()
        for ((key) in sortedMapAlbumCount) {
            Log.i("MainViewModel", "- " + key)

            coroutineScope.launch {
                //Log.i("MainViewModel", "Key: " + key.replace("\\s".toRegex(), "_").lowercase())
                //Log.i("MainViewModel", "Token: " + SpotifyApiGetToken.getSpotifyToken())
                var getArtistDeferred = SpotifyApi.retrofitService.getArtistDetails(
                    key
                )
                try {
                    val temp = getArtistDeferred.await()
                    Log.i("MainViewModel", "Try - Response: " + key + " - " + temp.artists.items[0].images[0].url)
                } catch (t: Throwable) {
                    Log.i("MainViewModel", "Catch - Response: " + key + " - " + t.message + t.toString())
                }
            }
            //break
        }

        /* coroutineScope.launch {
             var getTokenDeferred = SpotifyApi.retrofitService.(
                 requestBody
             )
             try {
                 _bearerToken.value = getTokenDeferred.await()
             } catch (t: Throwable) {
                 Log.i("RegisterViewModel", "Catch - Response: " + t.message + t.toString())
             }
         }*/
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}