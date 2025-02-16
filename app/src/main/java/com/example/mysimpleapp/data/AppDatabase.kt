package com.example.mysimpleapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

@Database(entities = [TextEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun textDao(): TextDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        insertPreloadedWords(db)
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        insertPreloadedWords(db)
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private fun insertPreloadedWords(db: SupportSQLiteDatabase) {
            PreloadedWords.words.forEach { word ->
                db.execSQL(
                    """
                    INSERT INTO texts (text, translation, correctAnswers, wrongAnswers)
                    VALUES ('${word.text}', '${word.translation}', 0, ${Random.nextInt(0, 21)})
                    """
                )
            }
        }
    }
}