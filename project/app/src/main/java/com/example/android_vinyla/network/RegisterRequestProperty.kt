package com.example.android_vinyla.network

data class RegisterRequestProperty(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val passwordConfirmation: String
)
