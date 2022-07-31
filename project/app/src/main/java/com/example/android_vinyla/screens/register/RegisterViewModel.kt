package com.example.android_vinyla.screens.register

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = password

    fun checkEmail(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            Log.i("RegisterViewModel", "EMPTY")
            return false;
        } else {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _email.value = email
                Log.i("RegisterViewModel", "TRUE -> ${_email.value.toString()}")
                return true
            }
            Log.i("RegisterViewModel", "FALSE -> $email")
            return false
        }
    }

    fun checkPassword(password: String) {

    }

    fun test() {
        Log.i("RegisterViewModel", "ViewModelTest")
    }
}