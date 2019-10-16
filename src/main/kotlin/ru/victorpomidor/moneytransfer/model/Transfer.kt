package ru.victorpomidor.moneytransfer.model

import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal
import java.util.UUID

data class Transfer(
    val requestId: String,
    val systemId: String,
    val sourceAccountId: UUID,
    val targetAccountId: UUID,
    val amount: BigDecimal,
    var status: TransferStatus? = null
) {
    fun requestKey() = RequestKey(requestId, systemId)
}

data class TransferStatus(
    val code: TransferStatusCode,
    val errorMessage: String = ""
)

enum class TransferStatusCode(@get:JsonValue val code: Int) {
    SUCCESS(0),
    IN_PROGRESS(1),
    INSUFFICIENT_FUNDS(2),
    CURRENCIES_NOT_MATCH(3),
    SOURCE_ACCOUNT_NOT_FOUND(4),
    TARGET_ACCOUNT_NOT_FOUND(5)
}
