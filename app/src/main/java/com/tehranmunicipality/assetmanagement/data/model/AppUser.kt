package com.tehranmunicipality.assetmanagement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppUser(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "username")
    var username: String = "",
    @ColumnInfo(name = "password")
    var password: String = "",
    @ColumnInfo(name = "displayName")
    var displayName: String = "",
    @ColumnInfo(name = "token")
    var token: String = "",
) {
    constructor(username: String, password: String, displayName: String, token: String) : this(
        0,
        username,
        password,
        displayName,
        token
    )
}

