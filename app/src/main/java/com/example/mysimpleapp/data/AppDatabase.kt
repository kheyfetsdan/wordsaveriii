package com.example.mysimpleapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlin.random.Random

@Database(
    entities = [TextEntity::class, UserEntity::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun textDao(): TextDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        email TEXT NOT NULL PRIMARY KEY,
                        password TEXT NOT NULL
                    )
                """)
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Создаем временную таблицу с новой структурой
                database.execSQL("""
                    CREATE TABLE texts_temp (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        text TEXT NOT NULL,
                        translation TEXT NOT NULL,
                        correctAnswers REAL NOT NULL DEFAULT 0.0,
                        wrongAnswers REAL NOT NULL DEFAULT 0.0
                    )
                """)

                // Копируем данные, конвертируя INT в REAL
                database.execSQL("""
                    INSERT INTO texts_temp (id, text, translation, correctAnswers, wrongAnswers)
                    SELECT id, text, translation, CAST(correctAnswers AS REAL), CAST(wrongAnswers AS REAL)
                    FROM texts
                """)

                // Удаляем старую таблицу
                database.execSQL("DROP TABLE texts")

                // Переименовываем временную таблицу
                database.execSQL("ALTER TABLE texts_temp RENAME TO texts")
            }
        }

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
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private fun insertPreloadedWords(db: SupportSQLiteDatabase) {
            PreloadedWords.words.forEach { word ->
                val correctAnswers = Random.nextDouble(0.0, 100.0)
                val wrongAnswers = Random.nextDouble(0.0, 100.0)
                db.execSQL(
                    """
                    INSERT INTO texts (text, translation, correctAnswers, wrongAnswers)
                    VALUES ('${word.text}', '${word.translation}', $correctAnswers, $wrongAnswers)
                    """
                )
            }
        }
    }
}