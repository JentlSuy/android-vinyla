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

    private val _response = MutableLiveData<String>()
    val response: LiveData<String> get() = _response

    private var _spotifyTokenProperty = MutableLiveData<SpotifyTokenProperty>()
    val spotifyTokenProperty: LiveData<SpotifyTokenProperty> get() = _spotifyTokenProperty

    private var _sortedMapAlbumCount: List<Map.Entry<String, Int>> = ArrayList()

    private val _status = MutableLiveData<VinylaApiStatus>()
    val status: LiveData<VinylaApiStatus> get() = _status

    private val _artists = MutableLiveData<ArrayList<ArtistProperty>>()
    val artists: LiveData<ArrayList<ArtistProperty>> get() = _artists

    private var _emptyCollection = MutableLiveData<Boolean>().apply { postValue(false) }
    val emptyCollection: LiveData<Boolean> get() = _emptyCollection

    private var _emptySelection = MutableLiveData<Boolean>().apply { postValue(true) }
    val emptySelection: LiveData<Boolean> get() = _emptySelection

    private val _selectedArtists = MutableLiveData<ArrayList<String>>()
    val selectedArtists: LiveData<ArrayList<String>> get() = _selectedArtists

    private val _selectedArtistsString = MutableLiveData<String>().apply { "" }
    val selectedArtistsString: LiveData<String> get() = _selectedArtistsString

    private val _streamingServicePackage = MutableLiveData<String>().apply { "com.spotify.music" }
    val streamingServicePackage: LiveData<String> get() = _streamingServicePackage

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<ArtistProperty?>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: MutableLiveData<ArtistProperty?>
        get() = _navigateToSelectedProperty

    var artistsListTemp = ArrayList<ArtistProperty>()

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        //com.spotify.music
        _streamingServicePackage.value = "com.google.android.apps.youtube.music"
        _selectedArtists.value = ArrayList()
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

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
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
                        artistsListTemp.add(
                            ArtistProperty(
                                key,
                                temp.artists.items[0].images[0].url
                            )
                        )

                    } catch (t: Throwable) {
                        _status.value = VinylaApiStatus.ERROR
                        Log.i(
                            "MainViewModel",
                            "Catch - Response: " + key + " - " + t.message + t.toString()
                        )
                    }
                }
            }, (50))

            counter++
            if (counter == MAX_ITEMS)
                break
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            setData()
            Log.i(
                "MainViewModel",
                "(" + _itemsUsed.value + " * (" + MAX_ITEMS + " - (" + _itemsUsed.value!! + " - 1)) * 10)"
            )
        }, ((Math.log10(_itemsUsed.value!!.toDouble()) + 2) * 1000).toLong())
    }

    private fun setData() {
        _artists.value = artistsListTemp
        _status.value = VinylaApiStatus.DONE
        if (_itemsUsed.value!! == 0) {
            _emptyCollection.value = true
        }
        Log.i("MainViewModel", "Data set: " + _artists.value!!.count())
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun displayPropertyDetails(artistProperty: ArtistProperty) {
        _selectedArtistsString.value = ""
        if (_selectedArtists.value!!.contains(artistProperty.name))
            _selectedArtists.value!!.remove(artistProperty.name)
        else if (!_selectedArtists.value!!.contains(artistProperty.name))
            _selectedArtists.value!!.add(artistProperty.name)

        if (_selectedArtists.value!!.isEmpty())
            _emptySelection.value = true
        else if (_selectedArtists.value!!.isNotEmpty()) {
            _emptySelection.value = false
            _selectedArtistsString.value =
                _selectedArtists.value.toString().replace("[", "").replace("]", "")
        }

        //_navigateToSelectedProperty.value = artistProperty
        Log.i(
            "MainViewModel",
            "Selected List Edited - now contains: " + _selectedArtists.value.toString()
        )
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    fun refresh() {
        _artists.value!!.clear()
        getSpotifyToken()
    }
}