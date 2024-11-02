package com.tehranmunicipality.assetmanagement.repository

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.tehranmunicipality.assetmanagement.data.ESB_TOKEN_URL
import com.tehranmunicipality.assetmanagement.data.GRANT_TYPE
import com.tehranmunicipality.assetmanagement.data.SCOPE
import com.tehranmunicipality.assetmanagement.data.api.AssetManagementApi
import com.tehranmunicipality.assetmanagement.data.local.AppDao
import com.tehranmunicipality.assetmanagement.data.model.*
import com.tehranmunicipality.assetmanagement.util.SearchType
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.*
import java.net.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Repository @Inject constructor(
    private val assetManagementApi: AssetManagementApi,
    private val appDao: AppDao,
) {

    //    private lateinit var setting: Setting
//
    fun getSetting(): Flow<Setting> {

        val result = appDao.getSetting()
        Log.i("DEBUG", "result in repository is: $result")
        return result
    }

    suspend fun insertSetting(setting: Setting) {
        appDao.insertSetting(setting)
    }

    suspend fun updateSetting(setting: Setting) {
        appDao.updateSetting(setting)
        Log.i("DEBUG", "updateSetting")
    }

    suspend fun deleteSetting(setting: Setting) {
        appDao.deleteSetting(setting)
        Log.i("DEBUG", "deleteSetting")
    }

    suspend fun getUserNormal(
    ): AppUser {
        var result = AppUser()
        var results=AppUser()
        results.username.toString();

        Log.i("DEBUG", "getting user from database")
        return appDao.getAppUserNormal()
    }

    fun getUser(): Flow<AppUser> {
        return appDao.getAppUser()
    }

    suspend fun insertAppUser(appUser: AppUser): Long {
        return appDao.insertAppUser(appUser)
    }

    suspend fun updateAppUser(appUser: AppUser) {
        appDao.updateAppUser(appUser)
        Log.i("DEBUG", "updateAppUser")
    }

    suspend fun deleteAppUser() {
        appDao.deleteAppUser()
    }

    fun deleteAllTables() {

    }

    suspend fun getESBToken(
        username: String, password: String
    ): ESBTokenResponse {
        val gsonObject = JsonObject()
        gsonObject.addProperty("grant_type", GRANT_TYPE)
        gsonObject.addProperty("username", username)
        gsonObject.addProperty("password", password)
        gsonObject.addProperty("Scope", SCOPE)

        val response = assetManagementApi.getESBToken(
            ESB_TOKEN_URL, "application/x-www-form-urlencoded",
            "Basic b3gta3l2NlJ6VlF6OFlXVF9vcktQX1k5TjJBeFVTM09iYVhYeUoyR2d4RS5hcGlnYXRld2F5LnRlaHJhbi5pcjpPeTJwR3ZQdEExcnRNYzVraTR3OWlhUy0yYVliVml3N2owT255ZGxTb2tz",
            GRANT_TYPE,
            username,
            password
        )

        var esbTokenResponse = ESBTokenResponse()
        if (response.isSuccessful) {
            esbTokenResponse = response.body()!!
        } else {
            Log.i("DEBUG", "Repository getESBToken response is not successful")
            val errorBodyString = response.errorBody()?.string()
            Log.i("DEBUG", "errorBodyString=$errorBodyString")
            val jsonParser = JsonParser()
            val errorJsonObject: JsonObject = jsonParser.parse(errorBodyString).asJsonObject
            var error = ""
            var errorDescription = ""
            if (errorJsonObject.has("error")) {
                error = errorJsonObject.get("error").asString
            }

            if (errorJsonObject.has("error_description")) {
                errorDescription = errorJsonObject.get("error_description").asString
            }
            esbTokenResponse = ESBTokenResponse(error = error, error_description = errorDescription)
        }

        Log.i("DEBUG", "repository esbTokenResponse=$esbTokenResponse")

        return esbTokenResponse
    }

    suspend fun getArticlePatternList(accessToken: String): GetArticlePatternListResponse {

        val gsonObject = JsonObject()
        //
        //body properties
        gsonObject.addProperty("articlePatternCode", "")
        gsonObject.addProperty("articlePatternName", "")
        //
        return assetManagementApi.getArticlePatternList(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }

    suspend fun getAssetList(
        searchType: SearchType,
        accessToken: String,
        searchText: String

    ): GetAssetListResponse {

        val gsonObject = JsonObject()
        //
        //body properties

        when (searchType) {

            SearchType.NationalCode -> {
                gsonObject.addProperty("identityNo", searchText)
            }

            SearchType.Username -> {
                gsonObject.addProperty("actorName", searchText)
            }

            SearchType.Barcode -> {
                gsonObject.addProperty("barCode", searchText.toInt())
            }

            SearchType.TagText -> {
                gsonObject.addProperty("assetTag", searchText)
            }

            SearchType.Location -> {
                gsonObject.addProperty("assetLocationID", searchText.toInt())
            }

            SearchType.AssetName -> {
                gsonObject.addProperty("productName", searchText)
            }
        }

        //gsonObject.addProperty("actorName", actorName)


        //
        return assetManagementApi.getAssetList(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }

    suspend fun getAssetStatusList(accessToken: String): GetAssetStatusListResponse {
        return assetManagementApi.getAssetStatusList(
            authorization = "Bearer $accessToken",
        )
    }

    suspend fun getCostCenterListAsync(accessToken: String): GetCostCenterListAsyncResponse {
        return assetManagementApi.getCostCenterListAsync(
            authorization = "Bearer $accessToken",
        )
    }

    suspend fun getGoodList(
        accessToken: String,
        goodCode: Int,
        parentArticlePatternId: Int,
        productName: String
    ): GetGoodListResponse {

        val gsonObject = JsonObject()
        //
        //body properties
        //gsonObject.addProperty("goodCode", goodCode)
        gsonObject.addProperty("parentArticlePatternId", parentArticlePatternId)
        //gsonObject.addProperty("productName", productName)
        //
        return assetManagementApi.getGoodList(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }

    suspend fun getLocationList(accessToken: String): GetLocationListResponse {
        return assetManagementApi.getLocationList(
            authorization = "Bearer $accessToken"
        )
    }

    suspend fun getModifyAsset(
        accessToken: String,
        assetId: Int,
        assetTypeCode: Int,
        barcode: Int,
        note: String,
        productId: Int
    ): GetModifyAssetResponse {

        val gsonObject = JsonObject()
        //
        //body properties
        gsonObject.addProperty("assetId", assetId)
        gsonObject.addProperty("assetTypeCode", assetTypeCode)
        gsonObject.addProperty("barcode", barcode)
        gsonObject.addProperty("note", note)
        gsonObject.addProperty("productId", productId)
        //
        return assetManagementApi.getModifyAsset(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }

    suspend fun setBarcodeForAsset(
        accessToken: String,
        assetId: String,
        assetTypeCode: Int,
        barcode: String
    ): GetModifyAssetResponse {

        val gsonObject = JsonObject()
        //
        //body properties
        gsonObject.addProperty("assetId", assetId.toInt())
        gsonObject.addProperty("assetTypeCode", assetTypeCode)
        gsonObject.addProperty("barcode", barcode.toInt())
        //
        return assetManagementApi.getModifyAsset(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }


    suspend fun getModifyAssetHistory(
        accessToken: String,
        actorId: Int?,
        assetHistoryDate: String,
        assetHistoryId: Int?,
        assetId: Int,
        assetLocationId: Int,
        assetStatusCode: Int,
        note: String,
        subCostCenterId: Int
    ): GetModifyAssetHistoryResponse {

        val gsonObject = JsonObject()
        //
        //body properties
        gsonObject.addProperty("actorId", actorId)
        gsonObject.addProperty("assetHistoryDate", assetHistoryDate)
        gsonObject.addProperty("assetHistoryId", assetHistoryId)
        gsonObject.addProperty("assetId", assetId)
        gsonObject.addProperty("assetLocationId", assetLocationId)
        gsonObject.addProperty("assetStatusCode", assetStatusCode)
        gsonObject.addProperty("note", note)
        gsonObject.addProperty("subCostCenterId", subCostCenterId)
        //

        Log.i("DEBUG", "Repository gsonObject=$gsonObject")

        return assetManagementApi.getModifyAssetHistory(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }

    suspend fun getPersonList(accessToken: String, subCostCenterId: Int): GetPersonListResponse {

        val gsonObject = JsonObject()
        //
        //body properties
        gsonObject.addProperty("subCostCenterId", subCostCenterId)
        //
        return assetManagementApi.getPersonList(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }

    suspend fun getPersonList(
        accessToken: String,
        lastName: String = "",
        identityNo: String = ""
    ): GetPersonListResponse {

        val gsonObject = JsonObject()

        if (lastName.isNotEmpty()) {
            //body properties
            gsonObject.addProperty("lastName", lastName)
            //
        }
        if (identityNo.isNotEmpty()) {
            //body properties
            gsonObject.addProperty("IdentityNo", identityNo)
            //
        }
        return assetManagementApi.getPersonList(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )

    }

    suspend fun getSubCostCenter(
        accessToken: String,
        costCenterId: Int
    ): GetSubCostCenterListResponse {

        val gsonObject = JsonObject()
        //
        //body properties
        gsonObject.addProperty("costCenterID", costCenterId)
        //
        return assetManagementApi.getSubCostCenterList(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }

    suspend fun modifyAsset(
        accessToken: String,
        assetId: Int?,
        assetTypeCode: Int,
        barcode: Long,
        note: String,
        productId: Int
    ): GetModifyAssetResponse {

        val gsonObject = JsonObject()
        //
        //body properties
        gsonObject.addProperty("assetId", assetId)
        gsonObject.addProperty("assetTypeCode", assetTypeCode)
        gsonObject.addProperty("barcode", barcode)
        gsonObject.addProperty("note", note)
        gsonObject.addProperty("productId", productId)
        //
        return assetManagementApi.modifyAsset(
            authorization = "Bearer $accessToken",
            body = gsonObject
        )
    }


    //============================================================//
    @Throws(IOException::class)
    suspend fun getExternalIP(): Flow<String> = callbackFlow {
        var result = ""
        var ip: URL?
        var inp: BufferedReader? = null
        withContext(Dispatchers.IO) {
            try {
                ip = URL("http://checkip.amazonaws.com/")
                inp = BufferedReader(InputStreamReader(ip!!.openStream()))
                result = inp!!.readLine()
                trySend(result)

            } catch (e: Exception) {
                result = "unknown"
                trySend(result)
            }
        }
        awaitClose {
            ip = null
            inp?.close()
            inp = null
        }
    }

    @Throws(java.lang.Exception::class)
    fun getTimeFromInternet(): Flow<Long?> = callbackFlow {
        var elements: Elements
        var doc: Document
        withContext(Dispatchers.IO) {
            val url = "https://time.is/Unix_time_now"
            doc = Jsoup.connect(url).get()
            val tags = arrayOf(
                "div[id=time_section]",
                "div[id=clock0_bg]"
            )
            elements = doc.select(tags[0])
            for (i in tags.indices) {
                elements = elements.select(tags[i])
            }
            Log.i(
                "DEBUG",
                "getTimeFromInternet() repository: " + elements.text().toLong()
            )
            trySend(elements.text().toLong() * 1000)
        }

        awaitClose {
            elements.remove()
            doc.remove()
            channel.close()
        }
    }

    fun getDeviceDate(): Long {
        val timeInMiliSeconds = Date().time
        Log.i("DEBUG", "device Time: " + timeInMiliSeconds)
        return timeInMiliSeconds
    }

}