package com.bqsqwad.howdoilook.core.data.repository

import com.bqsqwad.howdoilook.core.data.local.LookRatingDao
import com.bqsqwad.howdoilook.core.data.local.LookRatingEntity
import com.bqsqwad.howdoilook.core.domain.model.LookRating
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LookRepository @Inject constructor(
    private val dao: LookRatingDao
) {
    fun observeAll(): Flow<List<LookRating>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeRecent(limit: Int = 10): Flow<List<LookRating>> =
        dao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    fun observeFavorites(): Flow<List<LookRating>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: String): LookRating? =
        dao.getById(id)?.toDomain()

    suspend fun save(rating: LookRating) {
        dao.insert(LookRatingEntity.fromDomain(rating))
    }

    suspend fun update(rating: LookRating) {
        dao.update(LookRatingEntity.fromDomain(rating))
    }

    suspend fun delete(id: String) {
        dao.deleteById(id)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun count(): Int = dao.count()
}
