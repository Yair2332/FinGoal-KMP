package com.fingoal.app.data.mapper

import com.fingoal.app.data.local.entities.TransactionEntity
import com.fingoal.app.domain.model.Transaction

fun TransactionEntity.toDomain() = Transaction(
    id = this.id,
    remoteId = this.remoteId,
    title = this.title,
    description = this.description,
    amount = this.amount,
    category = this.category,
    date = this.date,
    isIncome = this.isIncome
)

fun Transaction.toEntity() = TransactionEntity(
    id = this.id,
    remoteId = this.remoteId,
    title = this.title,
    description = this.description,
    amount = this.amount,
    category = this.category,
    date = this.date,
    isIncome = this.isIncome
)