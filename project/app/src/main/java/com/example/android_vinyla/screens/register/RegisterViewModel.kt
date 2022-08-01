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
                Log.i("RegisterViewModel", "CORRECT EMAIL -> ${_email.value.toString()}")
                return true
            }
            Log.i("RegisterViewModel", "INCORRECT EMAIL -> $email")
            return false
        }
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