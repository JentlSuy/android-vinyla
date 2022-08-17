package com.example.android_vinyla.screens.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android_vinyla.network.LoginRequestProperty
import com.example.android_vinyla.network.VinylaApi
import com.example.android_vinyla.network.VinylaApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _bearerToken = MutableLiveData<String>()
    val bearerToken: LiveData<String> get() = _bearerToken

    private var viewModeljob = Job()
    private val coroutineScope = CoroutineScope(viewModeljob + Dispatchers.Main)

    private var _correctPassword = MutableLiveData<Boolean>().apply { postValue(false) }
    val correctPassword: LiveData<Boolean> get() = _correctPassword

    /**
     * Calls the API and logs the user in. Sets the [_bearerToken] which is retrieved from the login request.
     */
    fun logIn(email: String, password: String): Boolean {
        _email.value = email
        _password.value = password
        val requestBody = LoginRequestProperty(_email.value.toString(), _password.value.toString())

        Log.i("LoginViewModel", requestBody.toString())

        coroutineScope.launch {
            var getTokenDeferred = VinylaApi.retrofitService.login(
                requestBody
            )
            try {
                _bearerToken.value = getTokenDeferred.await()
                _correctPassword.value = true
            } catch (t: Throwable) {
                Log.i("LoginViewModel", "Response: " + t.message)
            }
        }
        VinylaApi.setBearerToken(_bearerToken.value.toString())
        VinylaApi.setEmail(_email.value.toString())
        return _correctPassword.value!!
    }

    override fun onCleared() {
        super.onCleared()
    }
}