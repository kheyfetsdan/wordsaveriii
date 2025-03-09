package com.example.mysimpleapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TextDao {
    @Insert
    suspend fun insert(text: TextEntity)

    @Query("SELECT * FROM texts ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomText(): TextEntity?

    @Query("SELECT COUNT(*) FROM texts")
    suspend fun getWordsCount(): Int

    @Query("DELETE FROM texts")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(texts: List<TextEntity>)

    @Query("""
        SELECT * FROM texts 
        ORDER BY 
            CASE 
                WHEN wrongAnswers = 0 AND correctAnswers = 0 THEN 2  -- Новые слова
                WHEN wrongAnswers > 0 THEN 1                         -- Слова с ошибками
                ELSE 0                                              -- Остальные слова
            END DESC,
            RANDOM() 
        LIMIT 1
    """)
    suspend fun getRandomTextByWrongAnswers(): TextEntity?

    @Query("UPDATE texts SET correctAnswers = correctAnswers + 1 WHERE id = :textId")
    suspend fun incrementCorrectAnswers(textId: Int)

    @Query("UPDATE texts SET wrongAnswers = wrongAnswers + 1 WHERE id = :textId")
    suspend fun incrementWrongAnswers(textId: Int)

    @Query("""
        SELECT COUNT(*) FROM texts 
        WHERE text LIKE '%' || :query || '%' 
        OR translation LIKE '%' || :query || '%'
    """)
    suspend fun getSearchWordsCount(query: String): Int

    @Query("SELECT * FROM texts ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<TextEntity>

    @Update
    suspend fun update(text: TextEntity)
}