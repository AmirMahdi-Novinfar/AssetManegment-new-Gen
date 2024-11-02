package com.tehranmunicipality.assetmanagement.util

enum class SearchType {
    NationalCode {
        override fun getPersianName() = "کد ملی"
    },
    Username {
        override fun getPersianName() = "نام تحویل گیرنده"
    },
    Barcode {
        override fun getPersianName() = "بارکد"
    },
    TagText {
        override fun getPersianName() = "برچسب"
    },
    Location {
        override fun getPersianName() = "محل استقرار"
    },

    AssetName {
        override fun getPersianName() = "نام اموال"
    };

    abstract fun getPersianName(): String
}