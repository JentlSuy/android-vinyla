package com.example.android_vinyla.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings_table")
data class UserSettings(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "streaming_service")
    val streamingService: String,

    @ColumnInfo(name = "bearer_token")
    val bearerToken: String,

    @ColumnInfo(name = "email")
    val email: String
)