package com.domain.datasources

import com.domain.models.CurrentUserSipModel

interface IAppSettingsDataSource {
    fun getCurrentUserSipModel(): CurrentUserSipModel
    fun putCurrentUserSipModel(currentUserSipModel: CurrentUserSipModel)

    fun getSipList(): List<String>
    fun putSipList(list: List<String>)

    fun getRecentDialledList(): MutableList<String>
    fun putRecentDialledList(list: List<String>)

    fun getDraftText(): String
    fun putDraftText(draftText: String)

    fun clearAll()
}