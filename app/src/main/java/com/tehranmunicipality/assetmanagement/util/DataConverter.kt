package com.tehranmunicipality.assetmanagement.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {
    @TypeConverter
    fun saveIntList(list: List<Int>): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun getIntList(list: String): List<Int> {
        return Gson().fromJson(
            list,
            object : TypeToken<List<Int>>() {}.type
        )
    }

    @TypeConverter
    fun saveStringList(list: List<String>): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun getStringList(list: String): List<String> {
        return Gson().fromJson(
            list,
            object : TypeToken<List<String>>() {}.type
        )
    }

    @TypeConverter
    fun saveStringArrayList(list: ArrayList<String>): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun getStringArrayList(list: String): ArrayList<String> {
        return Gson().fromJson(
            list,
            object : TypeToken<ArrayList<String>>() {}.type
        )
    }

//    @TypeConverter
//    fun saveGetQRTicketTypePriceResponseItemList(list: List<GetQRTicketTypePriceResponseItem>): String? {
//        return Gson().toJson(list)
//    }
//
//    @TypeConverter
//    fun getGetQRTicketTypePriceResponseItemList(list: String): List<GetQRTicketTypePriceResponseItem> {
//        return Gson().fromJson(
//            list,
//            object : TypeToken<List<GetQRTicketTypePriceResponseItem>>() {}.type
//        )
//    }

}