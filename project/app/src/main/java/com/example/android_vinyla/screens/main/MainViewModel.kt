package com.example.android_vinyla.screens.main

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_vinyla.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class VinylaApiStatus { LOADING, ERROR, DONE }

class MainViewModel : ViewModel() {

    private val MAX_ITEMS = 30

    private val _itemsUsed = MutableLiveData<Int>()
    val itemsUsed: LiveData<Int> get() = _itemsUsed

    // SELF SELF
    private val _response = MutableLiveData<String>()
    val response: LiveData<String> get() = _response

    private var _spotifyTokenProperty = MutableLiveData<SpotifyTokenProperty>()
    val spotifyTokenProperty: LiveData<SpotifyTokenProperty> get() = _spotifyTokenProperty

    private var _sortedMapAlbumCount: List<Map.Entry<String, Int>> = ArrayList()

    private val _status = MutableLiveData<VinylaApiStatus>()
    val status: LiveData<VinylaApiStatus> get() = _status

    private val _artists = MutableLiveData<ArrayList<ArtistProperty>>()
    val artists: LiveData<ArrayList<ArtistProperty>> get() = _artists
    //val artist: ArtistProperty get() = artists.value!!.get(0)

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<ArtistProperty?>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: MutableLiveData<ArtistProperty?>
        get() = _navigateToSelectedProperty

    var artistsListTemp = ArrayList<ArtistProperty>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        _itemsUsed.value = MAX_ITEMS
        getSpotifyToken()
    }

    private fun getSpotifyToken() {
        coroutineScope.launch {
            _status.value = VinylaApiStatus.LOADING
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


                _sortedMapAlbumCount =
                    frequencyMap.toList().sortedBy { (_, key) -> key }.toMap().asIterable()
                        .reversed()

                var responseString = ""
                var counter = 0

                for ((key, value) in _sortedMapAlbumCount) {
                    responseString += "$key - $value\n"
                    counter++
                    if (counter == MAX_ITEMS)
                        break
                }

                _response.value = "$responseString"

                Log.i(
                    "MainViewModel",
                    "Artists found: " + _sortedMapAlbumCount.size + " - Limited to: " + MAX_ITEMS
                )

                if (_sortedMapAlbumCount.size < MAX_ITEMS)
                    _itemsUsed.value = _sortedMapAlbumCount.size

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
        var counter = 0
        for ((key) in _sortedMapAlbumCount) {
            Log.i("MainViewModel", "- " + key)

            coroutineScope.launch {
                _status.value = VinylaApiStatus.LOADING
                //Log.i("MainViewModel", "Key: " + key.replace("\\s".toRegex(), "_").lowercase())
                //Log.i("MainViewModel", "Token: " + SpotifyApiGetToken.getSpotifyToken())

                try {
                    var getArtistDeferred = SpotifyApi.retrofitService.getArtistDetails(
                        key
                    )
                    val temp = getArtistDeferred.await()
                    Log.i(
                        "MainViewModel",
                        "Try - Response: " + key + " - " + temp.artists.items[0].images[0].url
                    )
                    artistsListTemp.add(ArtistProperty(key, temp.artists.items[0].images[0].url))

//                    val handler = Handler(Looper.getMainLooper())
//                    handler.postDelayed({
//                        _status.value = VinylaApiStatus.DONE
//                        // 30 artiesten: 30 * (30 - (30 - 1)) * 10 = 300 ms/artiest ..... 300 * 30 = 9.000 ms/totaal
//                        // 5 artiesten: 5 * (30 - (5 - 1)) * 10 = 1300 ms/artiest  ..... 1300 * 5 = 6.500 ms/totaal
//                    }, (_itemsUsed.value!! * (MAX_ITEMS - (_itemsUsed.value!! - 1)) * 10).toLong())

                } catch (t: Throwable) {
                    _status.value = VinylaApiStatus.ERROR
                    Log.i(
                        "MainViewModel",
                        "Catch - Response: " + key + " - " + t.message + t.toString()
                    )
                }
            }



            counter++
            if (counter == MAX_ITEMS)
                break
        }


        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            setData()
//                        // 30 artiesten: 30 * (30 - (30 - 1)) * 10 = 300 ms/artiest ..... 300 * 30 = 9.000 ms/totaal
//                        // 5 artiesten: 5 * (30 - (5 - 1)) * 10 = 1300 ms/artiest  ..... 1300 * 5 = 6.500 ms/totaal
            Log.i("MainViewModel", "(" + _itemsUsed.value + " * (" + MAX_ITEMS + " - (" + _itemsUsed.value!! + " - 1)) * 10)")
        }, (_itemsUsed.value!! * (MAX_ITEMS - (_itemsUsed.value!! - 1)) * 50).toLong())



    }

    private fun setData() {
        _artists.value = artistsListTemp
        _status.value = VinylaApiStatus.DONE
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param marsProperty The [MarsProperty] that was clicked on.
     */
    fun displayPropertyDetails(artistProperty: ArtistProperty) {
        _navigateToSelectedProperty.value = artistProperty
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

}
