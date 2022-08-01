package com.example.android_vinyla.screens.register

import android.text.TextUtils
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

class RegisterViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _emailAvailable = MutableLiveData<String>()
    val emailAvailable: LiveData<String> get() = _emailAvailable

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    private val _firstname = MutableLiveData<String>()
    val firstname: LiveData<String> get() = _firstname

    private val _lastname = MutableLiveData<String>()
    val lastname: LiveData<String> get() = _lastname

    fun checkEmail(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            Log.i("RegisterViewModel", "EMPTY EMAIL")
            return false;
        } else {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _email.value = email
                Log.i("RegisterViewModel", "VALID EMAIL -> ${_email.value.toString()}")
                return true
            }
            Log.i("RegisterViewModel", "INVALID EMAIL -> $email")
            return false
        }
    }

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun checkEmailInUse() {
        coroutineScope.launch {
            // 3
            Log.i("RegisterViewModel", "Checking " + _email.value.toString())
            var getResultDeferred = VinylaApi.retrofitService.checkEmailInUse(
                _email.value.toString()
            )

            try {
                _emailAvailable.value = getResultDeferred.await()
                Log.i("RegisterViewModel", "Try - Response: " + _emailAvailable.value.toString())
            } catch (t: Throwable) {
                Log.i("RegisterViewModel", "Catch - Response: " + t.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun checkPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (password.filter { it.isDigit() }.firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isUpperCase() }
                .firstOrNull() == null) return false
        if (password.filter { it.isLetter() }.filter { it.isLowerCase() }
                .firstOrNull() == null) return false
        if (password.filter { !it.isLetterOrDigit() }.firstOrNull() == null) return false
        _password.value = password
        return true
    }

    fun checkName(name: String, firstname: Boolean): Boolean {
        if (name.length < 2) return false
        if (firstname)
            _firstname.value = name
        else
            _lastname.value = name
        return true
    }
}