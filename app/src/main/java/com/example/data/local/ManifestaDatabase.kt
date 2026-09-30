package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ManifestaDao
import com.example.data.local.entity.AccountEntity
import com.example.data.local.entity.AffirmationEntity
import com.example.data.local.entity.AiManifestationHistoryEntity
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.AudioAffirmationEntity
import com.example.data.local.entity.AudioPlaybackProgressEntity
import com.example.data.local.entity.AudioSessionEntity
import com.example.data.local.entity.DailyProgressEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.ManifestationEntity
import com.example.data.local.entity.ManifestationGoalEntity
import com.example.data.local.entity.SessionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VisionCardEntity

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        SessionEntity::class,
        ManifestationGoalEntity::class,
        AiManifestationHistoryEntity::class,
        AudioAffirmationEntity::class,
        AudioPlaybackProgressEntity::class,
        DailyProgressEntity::class,
        AppSettingsEntity::class,
        ManifestationEntity::class,
        AffirmationEntity::class,
        AudioSessionEntity::class,
        VisionCardEntity::class,
        JournalEntryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ManifestaDatabase : RoomDatabase() {
    abstract fun manifestaDao(): ManifestaDao

    companion object {
        @Volatile
        private var INSTANCE: ManifestaDatabase? = null

        fun getInstance(context: Context): ManifestaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ManifestaDatabase::class.java,
                    "manifesta.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
