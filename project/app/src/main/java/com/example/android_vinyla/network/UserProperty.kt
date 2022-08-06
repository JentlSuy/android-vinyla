package com.example.android_vinyla.network

data class UserProperty(
    val id: Double,
    val created: String,
    val firstName: String,
    val surName: String,
    val email: String,
    val favoriteGenre: String,
    val favoriteArtist: String,
    val albums: Array<AlbumProperty>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserProperty

        if (id != other.id) return false
        if (created != other.created) return false
        if (firstName != other.firstName) return false
        if (surName != other.surName) return false
        if (email != other.email) return false
        if (favoriteGenre != other.favoriteGenre) return false
        if (favoriteArtist != other.favoriteArtist) return false
        if (!albums.contentEquals(other.albums)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + created.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + surName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + favoriteGenre.hashCode()
        result = 31 * result + favoriteArtist.hashCode()
        result = 31 * result + albums.contentHashCode()
        return result
    }
}
