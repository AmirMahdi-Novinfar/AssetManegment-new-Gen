package com.tehranmunicipality.assetmanagement.ui.base

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tehranmunicipality.assetmanagement.data.model.*
import com.tehranmunicipality.assetmanagement.repository.PrefRepository
import com.tehranmunicipality.assetmanagement.repository.Repository
import com.tehranmunicipality.assetmanagement.util.Resource
import com.tehranmunicipality.assetmanagement.util.SearchType
import com.tehranmunicipality.assetmanagement.util.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    private val context: Context,
    private val repository: Repository,
    private val prefRepository: PrefRepository,
) : ViewModel() {

    private val _appUserResponse = MutableLiveData<Resource<AppUser>>()
    val appUserResponse: LiveData<Resource<AppUser>> = _appUserResponse

    private val _insertUserResponse = MutableLiveData<Resource<Long>>()
    val insertUserResponse: LiveData<Resource<Long>> = _insertUserResponse

    private val _esbTokenResponse = MutableLiveData<Resource<ESBTokenResponse>>()
    val esbTokenResponse: LiveData<Resource<ESBTokenResponse>> = _esbTokenResponse

    private val _getArticlePatternListsResponse =
        MutableLiveData<Resource<GetArticlePatternListResponse>>()
    val getArticlePatternListsResponse: LiveData<Resource<GetArticlePatternListResponse>> =
        _getArticlePatternListsResponse

    private val _getAssetListResponse = MutableLiveData<Resource<GetAssetListResponse>>()
    val getAssetListResponse: LiveData<Resource<GetAssetListResponse>> = _getAssetListResponse


  private val _getAssetListResponseformoreasset = MutableLiveData<Resource<GetAssetListResponse>>()
    val getAssetListResponseformoreasset: LiveData<Resource<GetAssetListResponse>> = _getAssetListResponseformoreasset

    private val _assetStatusListResponse = MutableLiveData<Resource<GetAssetStatusListResponse>>()
    val assetStatusListResponse: LiveData<Resource<GetAssetStatusListResponse>> =
        _assetStatusListResponse

    private val _getCostCenterListAsyncResponse =
        MutableLiveData<Resource<GetCostCenterListAsyncResponse>>()
    val getCostCenterListAsyncResponse: LiveData<Resource<GetCostCenterListAsyncResponse>> =
        _getCostCenterListAsyncResponse

    private val _getGoodListResponse = MutableLiveData<Resource<GetGoodListResponse>>()
    val getGoodListResponse: LiveData<Resource<GetGoodListResponse>> = _getGoodListResponse

    private val _getLocationListResponse = MutableLiveData<Resource<GetLocationListResponse>>()
    val getLocationListResponse: LiveData<Resource<GetLocationListResponse>> =
        _getLocationListResponse

    private val _getModifyAssetResponse = MutableLiveData<Resource<GetModifyAssetResponse>>()
    val getModifyAssetResponse: LiveData<Resource<GetModifyAssetResponse>> = _getModifyAssetResponse

    private val _setBarcodeForAssetResponse = MutableLiveData<Resource<GetModifyAssetResponse>>()
    val setBarcodeForAssetResponse: LiveData<Resource<GetModifyAssetResponse>> =
        _setBarcodeForAssetResponse
   private val _setAssetMoreClickedResponse = MutableLiveData<Resource<GetModifyAssetResponse>>()
    val setAssetMoreClickedResponse: LiveData<Resource<GetModifyAssetResponse>> =
        _setAssetMoreClickedResponse

    private val _getModifyAssetHistoryResponse =
        MutableLiveData<Resource<GetModifyAssetHistoryResponse>>()
    val getModifyAssetHistoryResponse: LiveData<Resource<GetModifyAssetHistoryResponse>> =
        _getModifyAssetHistoryResponse

    private val _getPersonListResponse = MutableLiveData<Resource<GetPersonListResponse>>()
    val getPersonListResponse: LiveData<Resource<GetPersonListResponse>> = _getPersonListResponse

//    private val _getPersonsResponse = MutableLiveData<Resource<GetPersonListResponse>>()
//    val getPersonsResponse: LiveData<Resource<GetPersonListResponse>> = _getPersonsResponse

    private val _getSubCostCenterListResponse =
        MutableLiveData<Resource<GetSubCostCenterListResponse>>()
    val getSubCostCenterListResponse: LiveData<Resource<GetSubCostCenterListResponse>> =
        _getSubCostCenterListResponse

    private val _modifyAssetResponse = MutableLiveData<Resource<GetModifyAssetResponse>>()
    val modifyAssetResponse: LiveData<Resource<GetModifyAssetResponse>> = _modifyAssetResponse

    private val _sharedPrefsTokenResponse = MutableLiveData<Resource<String>>()
    val sharedPrefsTokenResponse: LiveData<Resource<String>> = _sharedPrefsTokenResponse


    fun saveTokenInSharedPrefs(token: String) {
        prefRepository.setToken(token)
    }

    fun getUserFromSharedPrefs(): AppUser? {
        return prefRepository.getUser()
    }

    fun saveUserInSharedPreferences(appUser: AppUser) {
        prefRepository.saveUser(appUser)
    }

    fun deleteUserFromSharedPrefs() {
        prefRepository.deleteUser()
    }

    fun getTokenFromSharedPrefs() {
        viewModelScope.launch(Dispatchers.IO) {
            _sharedPrefsTokenResponse.postValue(Resource.loading(null))
            try {
                val sharedPrefsTokenResponse = prefRepository.getToken()
                _sharedPrefsTokenResponse.postValue(Resource.success(sharedPrefsTokenResponse))
            } catch (exception: Exception) {
                Log.i("DEBUG", "BaseViewModel _sharedPrefsTokenResponse error:${exception.message}")
                _sharedPrefsTokenResponse.postValue(
                    Resource.error(
                        exception.message.toString(),
                        null
                    )
                )
            }
        }
    }

    fun deleteTokenFromSharedPrefs() {
        prefRepository.clearToken()
    }

    fun getESBToken(username: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {
            var esbTokenResponse = ESBTokenResponse()
            _esbTokenResponse.postValue(Resource.loading(null))
            try {
                Log.i("DEBUG", "BaseViewModel getESBToken called")
                esbTokenResponse = repository.getESBToken(username, password)
                Log.i("DEBUG", "BaseViewModel esbTokenResponse insideTry=$esbTokenResponse")
                _esbTokenResponse.postValue(Resource.success(esbTokenResponse))
            } catch (e: Exception) {
                Log.i("DEBUG", "BaseViewModel getESBToken error:${e.message}")
                _esbTokenResponse.postValue(Resource.error(e.message.toString(), null))
            }
        }

    }

    fun getUserNormal() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getUserNormal()
                Log.i("DEBUG", "BaseViewModel getUser success.data=$result")
                _appUserResponse.postValue(Resource.success(result))
            } catch (exception: Exception) {
                Log.i("DEBUG", "BaseViewModel method saveUser")
                _appUserResponse.postValue(Resource.error(exception.message.toString(), null))
            }
        }
    }

    fun getUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getUser().cancellable().collect {
                    _appUserResponse.postValue(Resource.success(it))
                }
            } catch (exception: Exception) {
                Log.i("DEBUG", "BaseViewModel method getUser")
            }
        }
    }

    fun insertUser(username: String, password: String, displayName: String, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _insertUserResponse.postValue(Resource.loading(null))
                val appUser = AppUser(1, username, password, displayName, token)
                val result = repository.insertAppUser(appUser)
                _insertUserResponse.postValue(Resource.success(result))
            } catch (exception: Exception) {
                Log.i("DEBUG", "BaseViewModel method registerUser error.->${exception.message}")
                _insertUserResponse.postValue(Resource.error(exception.message.toString(), null))
            }
        }
    }

    fun updateUser(username: String, password: String, displayName: String, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val appUser = AppUser(1, username, password, displayName, token)
                repository.updateAppUser(appUser)
            } catch (exception: Exception) {
                Log.i("DEBUG", "BaseViewModel method updateUser error.->${exception.message}")
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAppUser()
        }
    }

    fun getArticlePatternList(accessToken: String) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getArticlePatternListsResponse.postValue(Resource.loading(null))
                try {
                    val getArticlePatternListResponse =
                        repository.getArticlePatternList(accessToken)
                    _getArticlePatternListsResponse.postValue(
                        Resource.success(
                            getArticlePatternListResponse
                        )
                    )
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel getArticlePatternList error:${exception.message}")
                    _getArticlePatternListsResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getAssetList(
        searchType: SearchType,
        accessToken: String,
        searchText: String
    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getAssetListResponse.postValue(Resource.loading(null))
                try {
                    val listAssetListResponse =
                        repository.getAssetList(
                            searchType,
                            accessToken,
                            searchText
                        )
                    _getAssetListResponse.postValue(Resource.success(listAssetListResponse))
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel getAssetList error:${exception.message}")
                    _getAssetListResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getAssetStatusList(accessToken: String) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _assetStatusListResponse.postValue(Resource.loading(null))
                try {
                    val assetStatusListResponse = repository.getAssetStatusList(accessToken)
                    _assetStatusListResponse.postValue(Resource.success(assetStatusListResponse))
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel getAssetStatusList error:${exception.message}")
                    _assetStatusListResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getCostCenterListAsync(accessToken: String) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getCostCenterListAsyncResponse.postValue(Resource.loading(null))
                try {
                    val getCostCenterListAsyncResponse =
                        repository.getCostCenterListAsync(accessToken)
                    _getCostCenterListAsyncResponse.postValue(
                        Resource.success(
                            getCostCenterListAsyncResponse
                        )
                    )
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel methodName error:${exception.message}")
                    _getCostCenterListAsyncResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getGoodList(
        accessToken: String,
        goodCode: Int,
        articlePatternId: Int,
        productName: String
    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getGoodListResponse.postValue(Resource.loading(null))
                try {
                    val getGoodListResponse =
                        repository.getGoodList(
                            accessToken,
                            goodCode,
                            articlePatternId,
                            productName
                        )
                    _getGoodListResponse.postValue(Resource.success(getGoodListResponse))
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel getGoodList error:${exception.message}")
                    _getGoodListResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getLocationList(accessToken: String) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getLocationListResponse.postValue(Resource.loading(null))
                try {
                    val getLocationListResponse = repository.getLocationList(accessToken)
                    _getLocationListResponse.postValue(Resource.success(getLocationListResponse))
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel getLocationList error:${exception.message}")
                    _getLocationListResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun setBarcodeForAsset(
        accessToken: String,
        assetId: String,
        assetTypeCode: Int,
        barcode: String
    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _setBarcodeForAssetResponse.postValue(Resource.loading(null))
                try {
                    val result =
                        repository.setBarcodeForAsset(accessToken, assetId, assetTypeCode, barcode)
                    _setBarcodeForAssetResponse.postValue(Resource.success(result))

                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel setBarcodeForAsset error:${exception.message}")
                    _setBarcodeForAssetResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun setAssetMoreClicked(
        accessToken: String,
        assetId: String,
        assetTypeCode: Int,
        barcode: String

    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _setAssetMoreClickedResponse.postValue(Resource.loading(null))
                try {
                    val result =
                        repository.setAssetMoreClicked(accessToken, assetId, assetTypeCode, barcode)
                    _setAssetMoreClickedResponse.postValue(Resource.success(result))

                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel setBarcodeForAsset error:${exception.message}")
                    _setAssetMoreClickedResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getModifyAsset(
        accessToken: String,
        assetId: Int,
        assetTypeCode: Int,
        barcode: String,
        note: String,
        productId: Int
    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getModifyAssetResponse.postValue(Resource.loading(null))
                try {
                    val getModifyAssetResponse = repository.getModifyAsset(
                        accessToken,
                        assetId, assetTypeCode,
                        barcode, note, productId
                    )
                    _getModifyAssetResponse.postValue(Resource.success(getModifyAssetResponse))

                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel getModifyAsset error:${exception.message}")
                    _getModifyAssetResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getModifyAssetHistory(
        accessToken: String,
        actorId: Int?,
        assetHistoryDate: String,
        assetHistoryId: Int?,
        assetId: Int,
        assetLocationId: Int,
        assetStatusCode: Int,
        note: String,
        subCostCenterId: Int

    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getModifyAssetHistoryResponse.postValue(Resource.loading(null))
                try {
                    val getModifyAssetHistoryResponse = repository.getModifyAssetHistory(
                        accessToken,
                        actorId, assetHistoryDate, assetHistoryId,
                        assetId, assetLocationId, assetStatusCode, note, subCostCenterId
                    )
                    _getModifyAssetHistoryResponse.postValue(
                        Resource.success(
                            getModifyAssetHistoryResponse
                        )
                    )

                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel methodName error:${exception.message}")
                    _getModifyAssetHistoryResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getPersonList(
        accessToken: String,
        subCostCenterId: Int
    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getPersonListResponse.postValue(Resource.loading(null))
                try {
                    val getPersonListResponse =
                        repository.getPersonList(accessToken, subCostCenterId)
                    _getPersonListResponse.postValue(Resource.success(getPersonListResponse))
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel methodName error:${exception.message}")
                    _getPersonListResponse
                        .postValue(Resource.error(exception.message.toString(), null))
                }
            }
        }
    }

    fun getPersonList(accessToken: String, name: String = "", identityNo: String = "") {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getPersonListResponse.postValue(Resource.loading(null))
                try {
                    val getPersonsResponse = repository.getPersonList(accessToken, name, identityNo)
                    _getPersonListResponse.postValue(
                        Resource.success(
                            getPersonsResponse
                        )
                    )
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel getPersons error:${exception.message}")
                    _getPersonListResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun getSubCostCenterList(
        accessToken: String,
        costCenterId: Int
    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _getSubCostCenterListResponse.postValue(Resource.loading(null))
                try {
                    val getSubCostCenterListResponse = repository.getSubCostCenter(
                        accessToken,
                        costCenterId
                    )
                    _getSubCostCenterListResponse.postValue(
                        Resource.success(
                            getSubCostCenterListResponse
                        )
                    )
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel methodName error:${exception.message}")
                    _getSubCostCenterListResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

    fun modifyAsset(
        accessToken: String,
        assetId: Int?, assetTypeCode: Int, barcode: String, note: String, productId: Int
    ) {
        if (!context.let { isNetworkAvailable(it) }) {
            val errorMessage = "اتصال اینترنت برقرار نیست"
            _modifyAssetResponse.postValue(Resource.error(errorMessage, null))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _modifyAssetResponse.postValue(Resource.loading(null))
                try {
                    val modifyAssetResponse =
                        repository.modifyAsset(
                            accessToken,
                            assetId,
                            assetTypeCode,
                            barcode,
                            note,
                            productId
                        )
                    _modifyAssetResponse.postValue(Resource.success(modifyAssetResponse))
                } catch (exception: Exception) {
                    Log.i("DEBUG", "BaseViewModel modifyAsset error:${exception.message}")
                    _modifyAssetResponse.postValue(
                        Resource.error(
                            exception.message.toString(),
                            null
                        )
                    )
                }
            }
        }
    }

}