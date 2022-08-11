package com.example.android_vinyla.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserSettingsDatabaseDao {
    @Query("SELECT * from user_settings_table")
    fun getUserSettings(): UserSettings?

    @Query("DELETE FROM user_settings_table")
    fun clear()

    @Insert
    fun insert(userSettings: UserSettings)
}