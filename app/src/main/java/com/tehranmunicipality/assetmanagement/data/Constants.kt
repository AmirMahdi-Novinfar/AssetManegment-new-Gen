package com.tehranmunicipality.assetmanagement.data

const val DATABASE_NAME = "app_database"
var REQUEST_READ_TIMEOUT = 60L
var REQUEST_CONNECT_TIMEOUT = 60L
const val ESB_TOKEN_URL = "https://srv165-apigateway.tehran.ir/ApiContainer.sso.RCL1/connect/token"


const val GRANT_TYPE = "password"
const val SCOPE = "" //scope is empty string

const val PREFERENCE_NAME = "ASSET_MANAGEMENT_PREFERENCES"
const val PREFERENCE_TOKEN = "PREFERENCE_TOKEN"
const val PREFERENCE_APP_USER = "PREFERENCE_APPUSER"

//end user session after this time of inactivity
const val INACTIVITY_TIMEOUT = 10 * 60 * 1000L

const val BARCODE_LENGTH = 9

const val RECYCLER_TYPE_GROUP = 1

const val RECYCLER_TYPE_CHILD = 1