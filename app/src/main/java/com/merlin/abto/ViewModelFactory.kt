package com.merlin.abto

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.data.database.RoomDataSource
import com.data.repositories.AppSettingsRepository
import com.domain.datasources.local.ILocalDataSource
import com.merlin.abto.abto.AbtoHelper
import com.merlin.abto.ui.activity.call.CallViewModel
import com.merlin.abto.ui.activity.main.MainViewModel
import com.merlin.abto.ui.activity.register.RegisterViewModel
import com.merlin.abto.ui.activity.splash.SplashViewModel

class ViewModelFactory private constructor(
    private val provideRoomDataSource: RoomDataSource,
    private val provideLocalDataSource: ILocalDataSource,
    private val provideAppDataSource: AppSettingsRepository,
    private val provideAbtoHelper: AbtoHelper
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(SplashViewModel::class.java) ->
                    SplashViewModel(provideAbtoHelper)
                isAssignableFrom(RegisterViewModel::class.java) ->
                    RegisterViewModel(provideAppDataSource, provideAbtoHelper)
                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(provideAppDataSource, provideAbtoHelper)
                isAssignableFrom(CallViewModel::class.java) ->
                    CallViewModel(provideAppDataSource, provideAbtoHelper)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application) =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideRoomDataSource(),
                    Injection.provideLocalDataSource(),
                    Injection.provideAppDataSource(),
                    Injection.provideAbtoHelper()
                )
                    .also { INSTANCE = it }
            }


        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
