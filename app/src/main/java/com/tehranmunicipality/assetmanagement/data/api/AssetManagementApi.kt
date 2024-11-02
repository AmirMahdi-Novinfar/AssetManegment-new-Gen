package com.tehranmunicipality.assetmanagement.data.api

import com.google.gson.JsonObject
import com.tehranmunicipality.assetmanagement.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AssetManagementApi {

    @FormUrlEncoded
    @POST
    suspend fun getESBToken(
        @Url esbUrl: String,
        @Header("Content-Type") contentType: String,
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("Scope") Scope: String = "",
    ): Response<ESBTokenResponse>

    @POST("api/v1/Asset/GetArticlePatternList")
    suspend fun getArticlePatternList(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): GetArticlePatternListResponse

    @POST("api/v1/Asset/GetAssetList")
    suspend fun getAssetList(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): GetAssetListResponse

    @POST("api/v1/Asset/GetAssetStatusList")
    suspend fun getAssetStatusList(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
    ): GetAssetStatusListResponse

    @POST("api/v1/Asset/GetCostCenterListAsync")
    suspend fun getCostCenterListAsync(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
    ): GetCostCenterListAsyncResponse

    @POST("api/v1/Asset/GetGoodList")
    suspend fun getGoodList(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): GetGoodListResponse

    @POST("api/v1/Asset/GetLocationList")
    suspend fun getLocationList(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
    ): GetLocationListResponse

    @POST("api/v1/Asset/ModifyAsset")
    suspend fun getModifyAsset(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): GetModifyAssetResponse

    @POST("api/v1/Asset/ModifyAssetHistory")

    suspend fun getModifyAssetHistory(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): GetModifyAssetHistoryResponse

    @POST("api/v1/Asset/GetPersonList")
    suspend fun getPersonList(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): GetPersonListResponse

//    @POST("api/v1/Asset/GetPersonList")
//    suspend fun getPersonList(
//        @Header("Content-Type") contentType: String = "application/json",
//        @Header("Authorization") authorization: String,
//        @Body body: JsonObject
//    ): List<PersonInfo>

    @POST("api/v1/Asset/GetSubCostCenterList")
    suspend fun getSubCostCenterList(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): GetSubCostCenterListResponse

    @POST("api/v1/Asset/ModifyAsset")
    suspend fun modifyAsset(
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): GetModifyAssetResponse

}