package com.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.data.database.dao.*
import com.domain.entity.*

@Database(
    entities = [QuickTextEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDataSource : RoomDatabase() {

    abstract val quickTextItemDao: QuickTextItemDao


}
