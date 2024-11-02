package com.tehranmunicipality.assetmanagement.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tehranmunicipality.assetmanagement.data.model.AppUser
import com.tehranmunicipality.assetmanagement.data.model.Setting
import com.tehranmunicipality.assetmanagement.di.ApplicationScope
import com.tehranmunicipality.assetmanagement.util.DataConverter
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [
        Setting::class, AppUser::class],
    version = 1
)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}