package com.tehranmunicipality.assetmanagement.util

enum class PersonSearchType {
    NationalCode {
        override fun getPersianName() = "کد ملی"
    },
    FirstName {
        override fun getPersianName() = "نام"
    },
    LastName {
        override fun getPersianName() = "نام خانوادگی"
    };

    abstract fun getPersianName(): String
}