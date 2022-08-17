package com.example.android_vinyla.screens.main

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.service.autofill.UserData
import android.util.Log
import androidx.lifecycle.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android_vinyla.database.UserSettings
import com.example.android_vinyla.database.UserSettingsDatabaseDao
import com.example.android_vinyla.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future


enum class VinylaApiStatus { LOADING, ERROR, DONE }

/**
 * This class implements the [MainViewModel] which is the main screen of the application.
 * It is used to display the user's favorite top [MAX_ITEMS] [artists].
 * @param [UserSettingsDatabaseDao] to keep track of the user's settings.
 */
class MainViewModel(val database: UserSettingsDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    /**
     * Maximum allowed number of artists to retrieve from the API.
     */
    private val MAX_ITEMS = 30

    /**
     * Effective number of artists retrieved from the API. Can be lower then [MAX_ITEMS] because
     * the user could have less then [MAX_ITEMS] in their favorites.
     * Private MutableLiveData and public LiveData version.
     */
    private val _itemsUsed = MutableLiveData<Int>()
    val itemsUsed: LiveData<Int> get() = _itemsUsed

    /**
     * A sorted List of Maps containing the [Artists] and the amount of albums the user has saved of that specific [Artist].
     * Needed as an attribute in [MainViewModel] because it is required in multiple functions and- or the [MainFragment].
     */
    private var _sortedMapAlbumCount: List<Map.Entry<String, Int>> = ArrayList()

    /**
     * Temporary list of [ArtistProperty], needed in a synchronized function.
     * Needed as an attribute in [MainViewModel] because it is required in multiple functions.
     */
    private var artistsListTemp = ArrayList<ArtistProperty>()

    /**
     * An ArrayList containing the retrieved [MAX_ITEMS] [ArtistProperty] artists from the API.
     * These are used in the fragments to display the artists.
     * Private MutableLiveData and public LiveData version.
     */
    private val _artists = MutableLiveData<ArrayList<ArtistProperty>>()
    val artists: LiveData<ArrayList<ArtistProperty>> get() = _artists

    /**
     * Boolean which is set to true if the User has no albums [AlbumProperty] in their collection.
     * If true the fragment will display a message instead of the list of [Artists].
     * Private MutableLiveData and public LiveData version.
     */
    private var _emptyCollection = MutableLiveData<Boolean>().apply { postValue(false) }
    val emptyCollection: LiveData<Boolean> get() = _emptyCollection

    /**
     * An ArrayList of the [Artists] selected by the user to create a custom station.
     * Contains only the names as Strings of the [Artists]
     * Private MutableLiveData and public LiveData version.
     */
    private val _selectedArtists = MutableLiveData<ArrayList<String>>()
    val selectedArtists: LiveData<ArrayList<String>> get() = _selectedArtists

    /**
     * One 'large' String of [_selectedArtists], separated by comma's.
     * Needed as an attribute in [MainViewModel] because it is required in multiple functions and- or the [MainFragment].
     * Private MutableLiveData and public LiveData version.
     */
    private val _selectedArtistsString = MutableLiveData<String>()
    val selectedArtistsString: LiveData<String> get() = _selectedArtistsString

    /**
     * Boolean needed as confirmation if the user has selected 0 [Artists].
     * Needed as an attribute in [MainViewModel] because it is required in multiple functions and- or the [MainFragment].
     * Private MutableLiveData and public LiveData version.
     */
    private var _emptySelection = MutableLiveData<Boolean>().apply { postValue(true) }
    val emptySelection: LiveData<Boolean> get() = _emptySelection

    /**
     * String containing the package of the user's set streaming service. Needed as Intent to redirect to different app.
     * Needed as an attribute in [MainViewModel] because it is required in multiple functions and- or the [MainFragment].
     * Private MutableLiveData and public LiveData version.
     */
    private val _streamingServicePackage = MutableLiveData<String>()
    val streamingServicePackage: LiveData<String> get() = _streamingServicePackage

    /**
     * The status of the API calls. Also used in the [BindingAdapters].
     * Private MutableLiveData and public LiveData version.
     */
    private val _status = MutableLiveData<VinylaApiStatus>()
    val status: LiveData<VinylaApiStatus> get() = _status

    /**
     * Needed to communicate with the API's.
     */
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        initializeUserSettings()
        _selectedArtists.value = ArrayList()
        _itemsUsed.value = MAX_ITEMS
        getSpotifyToken()
    }

    /**
     * Initializes the [UserSettings] by launching the [viewModelScope].
     */
    private fun initializeUserSettings() {
        viewModelScope.launch {
            _streamingServicePackage.value = getUserSettingsFromDatabase()
        }
    }

    /**
     * Calls a query from the [database] to load all the user's settings.
     * Checks if the database isn't empty, if so it creates a new [UserSettings].
     * Returns a String containing the [streamingServicePackage].
     */
    private fun getUserSettingsFromDatabase(): String {
        val callable = Callable { database.getUserSettings() }
        val future = Executors.newSingleThreadExecutor().submit(callable)
        val userSettings = future!!.get()
        var streamingService: String

        try {
            streamingService = userSettings!!.streamingService
        } catch (e: NullPointerException) {
            // If null -> Roomdb = empty so create new.
            saveStreamingServiceInRoom(
                UserSettings(
                    1,
                    "com.spotify.music",
                    VinylaApi.getBearerToken(),
                    VinylaApi.getEmail()
                )
            )
            return getUserSettingsFromDatabase()
        }
        return streamingService
    }

    /**
     * Clears the [database].
     */
    private fun clear() {
        val callable = Callable { database.clear() }
        Executors.newSingleThreadExecutor().submit(callable)
        Log.i("MainViewModel", "Clear UserSettingsDb")
        //database.clear()
    }

    /**
     * Inserts the new, updated [userSettings] into the [database].
     */
    private fun insert(userSettings: UserSettings) {
        val callable = Callable { database.insert(userSettings) }
        Executors.newSingleThreadExecutor().submit(callable)
        Log.i("MainViewModel", "Insert into UserSettingsDb")
        //database.insert(userSettings)
    }

    /**
     * Help functions which first clears the [database] and then inserts the new [userSettings].
     */
    private fun saveStreamingServiceInRoom(userSettings: UserSettings) {
        clear()
        insert(userSettings)
    }

    /**
     * Function that gets the [_SPOTIFY_TOKEN] from [SpotifyApiGetToken].
     * When this is done it automatically calls [getVinylaProperties].
     */
    private fun getSpotifyToken() {
        coroutineScope.launch {
            _status.value = VinylaApiStatus.LOADING
            var getTokenDeferred = SpotifyApiGetToken.retrofitService.getSpotifyToken()
            try {
                var result = getTokenDeferred.await()
                SpotifyApiGetToken.setSpotifyToken(result.accessToken)
                //_token.value = "Success- Token = ${result.accessToken}"
            } catch (t: Throwable) {
                Log.i("MainViewModel", "Failure: " + t.message)
            }
            getVinylaProperties()
        }
    }
    /**
     * Retrieves a list of [AlbumProperty] from [VinylaApi].
     * Creates a sorted map by artist appearance, [_sortedMapAlbumCount].
     * When this is done it automatically calls [getArtistImageUrls].
     */
    private fun getVinylaProperties() {
        coroutineScope.launch {
            Log.i("MainViewModel", "getAll(${VinylaApi.getEmail()})")
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

                Log.i(
                    "MainViewModel",
                    "Artists found: " + _sortedMapAlbumCount.size + " - Limited to: " + MAX_ITEMS
                )

                if (_sortedMapAlbumCount.size < MAX_ITEMS)
                    _itemsUsed.value = _sortedMapAlbumCount.size

                getArtistImageUrls()

            } catch (t: Throwable) {
                Log.i("MainViewModel", "Failure: " + t.message)
            }
        }
    }

    /**
     * Loops trough the map [_sortedMapAlbumCount] and retrieve all the image url's from [SpotifyApi].
     * Calls [setData] in the end by waiting an x amount of seconds for all the [ArtistProperty]'s to be retrieved.
     * The formula to calculate the amount of ms is: (log10(amount of artists) + 2) * 1000 milliseconds
     */
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
        }, ((Math.log10(_itemsUsed.value!!.toDouble()) + 2) * 1000).toLong())
    }

    /**
     * Sets the data for the [PhotoGridAdapter]. If this is empty it sets [_emptyCollection] to false.
     * The fragment displays the List of artists or a String depending on [_emptyCollection].
     */
    private fun setData() {
        _artists.value = artistsListTemp
        _status.value = VinylaApiStatus.DONE
        if (_itemsUsed.value!! == 0) {
            _emptyCollection.value = true
        }
        Log.i("MainViewModel", "Data set: " + _artists.value!!.count() + " artists")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * When the user clicks on an artist, it sets the data for the [selectedArtists] and [selectedArtistsString].
     */
    fun selectArtist(artistProperty: ArtistProperty) {
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
        Log.i(
            "MainViewModel",
            "Selected List Edited - now contains: " + _selectedArtists.value.toString()
        )
    }

    /**
     * Refreshes the data by clearing the [artists] and retrieving the data gain from the API's.
     */
    fun refresh() {
        _artists.value!!.clear()
        getSpotifyToken()
    }

    /**
     * Clears all the data in the API class and Room databases and logs the user out.
     * Returns to the welcome fragment.
     */
    fun logout() {
        VinylaApi.setBearerToken("")
        VinylaApi.setEmail("")
        clear()
    }

    /**
     * Help function to fix a color bug in the fragment of this class.
     */
    fun overrideEmptySelectionColorBug(override: Boolean) {
        if (override)
            _emptySelection.value = false
        else if (!override)
            _emptySelection.value = true
    }

    /**
     * Retrieves a [streamingServicePackageString] from the fragment and
     * sets the [_streamingServicePackage] which is connected with the [database].
     */
    fun setStreamingService(streamingServicePackageString: String) {
        _streamingServicePackage.value = streamingServicePackageString
        saveStreamingServiceInRoom(
            UserSettings(
                1,
                streamingServicePackageString,
                VinylaApi.getBearerToken(),
                VinylaApi.getEmail()
            )
        )
        Log.i("MainViewModel", "Streaming service set: " + _streamingServicePackage.value)
    }
}