package com.example.android_vinyla.network

data class SpotifyTokenProperty(
    val clientId: String,
    val accessToken: String,
    val accessTokenExpirationTimestampMs: Double,
    val isAnonymous: Boolean
)
