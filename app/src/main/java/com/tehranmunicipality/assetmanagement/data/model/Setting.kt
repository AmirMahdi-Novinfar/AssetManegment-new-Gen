package com.tehranmunicipality.assetmanagement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Setting(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "username")
    var username: String = "",
    @ColumnInfo(name = "password")
    var password: String = "",
    @ColumnInfo(name = "isOnlineMode")
    var isOnlineMode: Boolean = false,
)
