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

    @Query("SELECT * FROM texts")
    suspend fun getAllTexts(): List<TextEntity>

    @Query("SELECT * FROM texts LIMIT :limit OFFSET :offset")
    suspend fun getPagedWords(limit: Int, offset: Int): List<TextEntity>

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
        SELECT * FROM texts 
        WHERE text LIKE '%' || :query || '%' 
        OR translation LIKE '%' || :query || '%'
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchWords(query: String, limit: Int, offset: Int): List<TextEntity>

    @Query("""
        SELECT COUNT(*) FROM texts 
        WHERE text LIKE '%' || :query || '%' 
        OR translation LIKE '%' || :query || '%'
    """)
    suspend fun getSearchWordsCount(query: String): Int

    @Query("""
        SELECT * FROM texts 
        WHERE text LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%'
        ORDER BY 
            CASE 
                WHEN :sortBy = 'text_asc' THEN text
                WHEN :sortBy = 'text_desc' THEN text
                WHEN :sortBy = 'translation_asc' THEN translation
                WHEN :sortBy = 'translation_desc' THEN translation
                WHEN :sortBy = 'correctAnswers_asc' THEN correctAnswers
                WHEN :sortBy = 'correctAnswers_desc' THEN correctAnswers
                WHEN :sortBy = 'wrongAnswers_asc' THEN wrongAnswers
                WHEN :sortBy = 'wrongAnswers_desc' THEN wrongAnswers
            END,
            CASE 
                WHEN :sortBy LIKE '%_desc' THEN 1
                ELSE 0
            END DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPagedWordsSorted(
        query: String = "",
        sortBy: String = "text_asc",
        limit: Int,
        offset: Int
    ): List<TextEntity>

    @Query("""
        SELECT COUNT(*) FROM texts 
        WHERE text LIKE '%' || :query || '%' OR translation LIKE '%' || :query || '%'
    """)
    suspend fun getFilteredWordsCount(query: String = ""): Int

    @Query("SELECT * FROM texts ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<TextEntity>

    @Update
    suspend fun update(text: TextEntity)
}