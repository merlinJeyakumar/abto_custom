package com.merlin.abto

import android.content.Context
import com.data.database.RoomDataSource
import com.data.database.RoomManager
import com.data.repositories.AppSettingsRepository
import com.data.repositories.local.LocalDataRepositary
import com.data.repositories.remote.RestDataRepositary
import com.data.webservices.IService
import com.domain.datasources.local.ILocalDataSource
import com.domain.datasources.remote.IRestDataSource
import com.merlin.abto.data.RestService
import com.google.gson.Gson
import com.merlin.abto.abto.AbtoHelper
import com.merlin.abto.core.AppController

object Injection {

    fun provideAppDataSource(): AppSettingsRepository {
        return AppSettingsRepository.getInstance(provideContext(),Gson())
    }

    fun provideRestDataSource(): IRestDataSource {
        return RestDataRepositary.getInstance(provideService())
    }

    fun provideLocalDataSource(): ILocalDataSource {
        return LocalDataRepositary.getInstance(provideRoomDataSource())
    }

    fun provideService(): IService {
        return RestService.getInstance().getIService()
    }

    fun provideRoomDataSource(): RoomDataSource {
        return RoomManager.getInstance(provideContext())
    }

    fun provideContext(): Context {
        return AppController.instance
    }

    fun provideAbtoHelper(): AbtoHelper {
        return AbtoHelper.getInstance(provideAppDataSource())
    }

}