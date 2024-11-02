package com.tehranmunicipality.assetmanagement.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "username")
    val username: String = "",
    @ColumnInfo(name = "password")
    val password: String = "",
    @ColumnInfo(name = "displayName")
    val displayName: String = "",
    @ColumnInfo(name = "token")
    val token: String = "",
) {
    constructor(username: String, password: String, displayName: String, token: String) : this(
        0,
        username,
        password,
        displayName,
        token
    )
}

