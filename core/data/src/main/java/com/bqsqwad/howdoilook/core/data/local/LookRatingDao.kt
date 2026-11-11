package com.bqsqwad.howdoilook.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LookRatingDao {

    @Query("SELECT * FROM look_ratings ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<LookRatingEntity>>

    @Query("SELECT * FROM look_ratings WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun observeFavorites(): Flow<List<LookRatingEntity>>

    @Query("SELECT * FROM look_ratings ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 10): Flow<List<LookRatingEntity>>

    @Query("SELECT * FROM look_ratings WHERE id = :id")
    suspend fun getById(id: String): LookRatingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LookRatingEntity)

    @Update
    suspend fun update(entity: LookRatingEntity)

    @Query("DELETE FROM look_ratings WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM look_ratings")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM look_ratings")
    suspend fun count(): Int
}
