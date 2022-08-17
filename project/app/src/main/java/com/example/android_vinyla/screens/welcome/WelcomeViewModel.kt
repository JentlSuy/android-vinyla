package com.example.android_vinyla.screens.welcome

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android_vinyla.database.UserSettings
import com.example.android_vinyla.database.UserSettingsDatabaseDao
import com.example.android_vinyla.network.VinylaApi
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class WelcomeViewModel(val database: UserSettingsDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private val _bearerToken = MutableLiveData<String>()
    val bearerToken: LiveData<String> get() = _bearerToken

    init {
        Log.i("WelcomeViewModel", "Init WelcomeViewModel")
        initializeBearerToken()
    }

    /**
     * Initializes the [UserSettings] by launching the [viewModelScope].
     */
    private fun initializeBearerToken() {
        viewModelScope.launch {
            _bearerToken.value = getBearerTokenFromDatabase()
        }
    }

    /**
     * Calls the Room Database to retrieve the user's [_bearerToken].
     * Uses a try catch block to check if the Room Database is contains data.
     * If not it creates an empty [UserSettings] object.
     * Else it will automaticly log the user in.
     */
    private fun getBearerTokenFromDatabase(): String {
        val callable = Callable { database.getUserSettings() }
        val future = Executors.newSingleThreadExecutor().submit(callable)
        val userSettings = future!!.get()
        var bearerToken: String
        var email: String

        Log.i("WelcomeViewModel", "getBearerTokenFromDatabase")

        try {
            bearerToken = userSettings!!.bearerToken
            email = userSettings!!.email
        } catch (e: NullPointerException) {
            // If null -> Roomdb = empty so create new.
            saveBearerTokenInRoom(
                UserSettings(
                    1,
                    "com.spotify.music",
                    VinylaApi.getBearerToken(),
                    VinylaApi.getEmail()
                )
            )
            return getBearerTokenFromDatabase()
        }
        Log.i("WelcomeViewModel", "token $bearerToken")
        Log.i("WelcomeViewModel", "email $email")
        VinylaApi.setEmail(email)
        VinylaApi.setBearerToken(bearerToken)
        return bearerToken
    }

    /**
     * Clears the Room [database].
     */
    private fun clear() {
        val callable = Callable { database.clear() }
        Executors.newSingleThreadExecutor().submit(callable)
        Log.i("WelcomeViewModel", "Clear UserSettingsDb")
        //database.clear()
    }

    /**
     * Inserts an updated / new [userSettings] object into the Room [database].
     */
    private fun insert(userSettings: UserSettings) {
        val callable = Callable { database.insert(userSettings) }
        Executors.newSingleThreadExecutor().submit(callable)
        Log.i("WelcomeViewModel", "Insert into UserSettingsDb")
        //database.insert(userSettings)
    }

    /**
     * Help functions which calls [clear] and [insert].
     */
    private fun saveBearerTokenInRoom(userSettings: UserSettings) {
        Log.i("WelcomeViewModel", "saveBearerTokenFromDatabase")
        clear()
        insert(userSettings)
    }
}