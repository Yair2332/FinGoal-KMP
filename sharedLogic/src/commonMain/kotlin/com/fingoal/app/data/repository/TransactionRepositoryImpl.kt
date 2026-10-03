package com.fingoal.app.data.repository

import com.fingoal.app.data.local.UserPreferences
import com.fingoal.app.data.local.dao.TransactionDao
import com.fingoal.app.data.local.entities.TransactionEntity
import com.fingoal.app.data.mapper.toDomain
import com.fingoal.app.data.remote.TransactionApiService
import com.fingoal.app.data.remote.dto.TransactionRequest
import com.fingoal.app.domain.model.Transaction
import com.fingoal.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class TransactionRepositoryImpl(
    private val transactionApiService: TransactionApiService,
    private val transactionDao: TransactionDao,
    private val userPreferences: UserPreferences
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncTransactions() {
        try {
            val userId = userPreferences.userId.firstOrNull()
                ?: throw Exception("Usuario no logueado")

            val remoteTransactions =
                transactionApiService.getTransactions(userId)

            val localEntities = remoteTransactions.map { dto ->
                TransactionEntity(
                    remoteId = dto.id ?: "",
                    title = dto.title ?: "",
                    description = dto.description ?: "",
                    amount = dto.amount ?: 0.0,
                    category = dto.category ?: "",
                    date = dto.createdAt ?: 0L,
                    isIncome = dto.type == "INCOME"
                )
            }

            transactionDao.replaceAllTransactions(localEntities)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteTransaction(
        localId: Long,
        remoteId: String
    ) {
        try {
            transactionApiService.deleteTransaction(remoteId)
            transactionDao.deleteById(localId)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun addTransaction(
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {
        try {
            val userId = userPreferences.userId.firstOrNull()
                ?: throw Exception("No autorizado")

            val type = if (isIncome) {
                "INCOME"
            } else {
                "EXPENSE"
            }

            val request = TransactionRequest(
                userId = userId,
                title = title,
                description = description,
                amount = amount,
                category = category,
                type = type
            )

            val responseDto =
                transactionApiService.createTransaction(request)

            val localEntity = TransactionEntity(
                remoteId = responseDto.id ?: "",
                title = responseDto.title ?: "",
                description = responseDto.description ?: "",
                amount = responseDto.amount ?: 0.0,
                category = responseDto.category ?: "",
                date = responseDto.createdAt ?: 0L,
                isIncome = responseDto.type == "INCOME"
            )

            transactionDao.insertTransaction(localEntity)

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun updateTransaction(
        localId: Long,
        remoteId: String,
        title: String,
        amount: Double,
        category: String,
        isIncome: Boolean,
        description: String
    ) {
        try {
            val userId = userPreferences.userId.firstOrNull()
                ?: throw Exception("No autorizado")

            val type = if (isIncome) {
                "INCOME"
            } else {
                "EXPENSE"
            }

            val request = TransactionRequest(
                userId = userId,
                title = title,
                description = description,
                amount = amount,
                category = category,
                type = type
            )

            val responseDto =
                transactionApiService.updateTransaction(
                    remoteId,
                    request
                )

            val originalTransaction =
                transactionDao.getTransactionById(localId)

            val originalDate =
                originalTransaction?.date
                    ?: Clock.System.now().toEpochMilliseconds()

            val updatedEntity = TransactionEntity(
                id = localId,
                remoteId = remoteId,
                title = responseDto.title ?: "",
                description = responseDto.description ?: "",
                amount = responseDto.amount ?: 0.0,
                category = responseDto.category ?: "",
                date = originalDate,
                isIncome = responseDto.type == "INCOME"
            )

            transactionDao.insertTransaction(updatedEntity)

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}