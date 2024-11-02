package com.tehranmunicipality.assetmanagement.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.tehranmunicipality.assetmanagement.data.PREFERENCE_APP_USER
import com.tehranmunicipality.assetmanagement.data.PREFERENCE_NAME
import com.tehranmunicipality.assetmanagement.data.PREFERENCE_TOKEN
import com.tehranmunicipality.assetmanagement.data.model.AppUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefRepository @Inject constructor(
    val context: Context
) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()
    private val gson = Gson()

    private fun String.put(long: Long) {
        editor.putLong(this, long)
        editor.commit()
    }

    private fun String.put(int: Int) {
        editor.putInt(this, int)
        editor.commit()
    }

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.put(boolean: Boolean) {
        editor.putBoolean(this, boolean)
        editor.commit()
    }

    private fun String.getLong() = pref.getLong(this, 0)

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getString() = pref.getString(this, "")!!

    private fun String.getBoolean() = pref.getBoolean(this, false)

    fun saveUser(user: AppUser) {
        val userJson = Gson().toJson(user)
        PREFERENCE_APP_USER.put(userJson)
    }

    fun getUser(): AppUser? {
        return Gson().fromJson(PREFERENCE_APP_USER.getString(), AppUser::class.java)
    }

    fun deleteUser() {
        editor.clear()
        editor.commit()
    }

    fun setToken(token: String) {
        PREFERENCE_TOKEN.put(token)
    }

    fun getToken(): String {
        return PREFERENCE_TOKEN.getString()
    }

    fun clearToken() {
        editor.clear()
        editor.commit()
    }
}