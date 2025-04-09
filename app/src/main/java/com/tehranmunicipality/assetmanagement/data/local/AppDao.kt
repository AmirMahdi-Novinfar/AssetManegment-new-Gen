package com.tehranmunicipality.assetmanagement.data.local

import androidx.room.*
import com.tehranmunicipality.assetmanagement.data.model.AppUser
import com.tehranmunicipality.assetmanagement.data.model.Setting
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM Setting")
    fun getSetting(): Flow<Setting>

    @Query("SELECT * FROM Setting")
    fun getSettingNormal(): Setting

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSetting(setting: Setting)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: Setting)

    @Delete
    suspend fun deleteSetting(setting: Setting)

    @Query("SELECT * FROM AppUser")
    fun getAppUserNormal(): AppUser

    @Query("SELECT * FROM AppUser")
    fun getAppUser(): Flow<AppUser>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAppUser(appUser: AppUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppUser(appUser: AppUser): Long

    @Query("DELETE  FROM AppUser")
    suspend fun deleteAppUser()

}