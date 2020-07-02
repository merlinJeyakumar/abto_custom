package com.data.repositories

import android.content.Context
import android.os.Environment
import androidx.annotation.VisibleForTesting
import com.data.utils.BaseLiveSharedPreferences
import com.domain.datasources.IAppSettingsDataSource
import com.domain.models.CurrentUserSipModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.support.shared_pref.Prefs
import io.reactivex.Single
import java.io.File


class AppSettingsRepository(
    private val applicationContext: Context,
    private val plainGson: Gson
) : IAppSettingsDataSource {

    private var liveSharedPreferences: BaseLiveSharedPreferences
    private val SP_NAME = "prefs_myapp" //TODO: RENAME

    companion object {

        private var INSTANCE: AppSettingsRepository? = null

        @JvmStatic
        fun getInstance(applicationContext: Context, plainGson: Gson): AppSettingsRepository {
            if (INSTANCE == null) {
                synchronized(AppSettingsRepository::javaClass) {
                    INSTANCE = AppSettingsRepository(applicationContext, plainGson)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }

        private const val PREFS_CURRENT_SIP_MODEL = "PREFS_SIP_ID"
        private const val PREFS_SIP_LIST = "PREFS_SIP_LIST"
        private const val PREFS_RECENT_SIP_LIST = "PREFS_RECENT_SIP_LIST"
        private const val PREFS_DRAFT_TEXT = "PREFS_DRAFT_TEXT"
    }

    init {
        Prefs.Builder()
            .setContext(applicationContext)
            .setMode(Context.MODE_PRIVATE)
            .setPrefsName(SP_NAME)
            .build()

        liveSharedPreferences = BaseLiveSharedPreferences(Prefs.getPreferences())
    }

    override fun getCurrentUserSipModel(): CurrentUserSipModel {
        val currentUserSipModel = Prefs.getString(PREFS_CURRENT_SIP_MODEL, "")
        if (currentUserSipModel.isNullOrEmpty()) {
            return CurrentUserSipModel()
        }
        return plainGson.fromJson(currentUserSipModel, CurrentUserSipModel::class.java)
    }

    override fun putCurrentUserSipModel(currentUserSipModel: CurrentUserSipModel) {
        Prefs.putString(PREFS_CURRENT_SIP_MODEL, plainGson.toJson(currentUserSipModel))
    }

    override fun getSipList(): List<String> {
        val currentUserSipModel = Prefs.getString(PREFS_SIP_LIST, "[]")
        return plainGson.fromJson(
            currentUserSipModel,
            object : TypeToken<List<String>>() {}.type
        )
    }

    override fun putSipList(list: List<String>) {
        Prefs.putString(PREFS_SIP_LIST, plainGson.toJson(list))
    }

    override fun getRecentDialledList(): MutableList<String> {
        return Prefs.getStringSet(PREFS_RECENT_SIP_LIST, setOf()).toMutableList()
    }

    override fun putRecentDialledList(list: List<String>) {
        return Prefs.putStringSet(PREFS_RECENT_SIP_LIST, list.toSet())
    }

    override fun getDraftText(): String {
        return Prefs.getString(PREFS_DRAFT_TEXT,"")
    }

    override fun putDraftText(draftText: String) {
        Prefs.putString(PREFS_DRAFT_TEXT,draftText)
    }

    override fun clearAll() {
        Prefs.clear()
    }
}