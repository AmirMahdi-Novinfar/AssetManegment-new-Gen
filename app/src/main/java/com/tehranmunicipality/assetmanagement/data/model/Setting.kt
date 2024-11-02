package com.tehranmunicipality.assetmanagement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "username")
    val username: String = "",
    @ColumnInfo(name = "password")
    val password: String = "",
    @ColumnInfo(name = "isOnlineMode")
    val isOnlineMode: Boolean = false,
)
