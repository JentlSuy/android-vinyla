package com.example.android_vinyla.network

import com.squareup.moshi.Json

data class SpotifyArtistDetailsProperty(
    val artists: Artists
)

data class Artists(
    val href: String,
    val items: List<Item>,
    val limit: Long,
    val next: Any? = null,
    val offset: Long,
    val previous: Any? = null,
    val total: Long
)

data class Item(
    @Json(name = "external_urls")
    val externalUrls: ExternalUrls,

    val followers: Followers,
    val genres: List<String>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val popularity: Long,
    val type: String,
    val uri: String
)

data class ExternalUrls(
    val spotify: String
)

data class Followers(
    val href: Any? = null,
    val total: Long
)

data class Image(
    val height: Long,
    val url: String,
    val width: Long
)

data class BestMatch(
    val items: List<Item>
)