package ru.victorpomidor.moneytransfer.model

import java.math.BigDecimal
import java.util.UUID

data class Transfer(
    val requestId: String,
    val systemId: String,
    val sourceAccountId: UUID,
    val targetAccountId: UUID,
    val amount: BigDecimal,
    val currency: String,
    var status: TransferStatus? = null
) {
    fun requestKey() = RequestKey(requestId, systemId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Transfer

        if (requestId != other.requestId) return false

        return true
    }

    override fun hashCode(): Int {
        return requestId.hashCode()
    }
}

data class TransferStatus(
    val code: TransferStatusCode,
    val errorMessage: String = ""
)

enum class TransferStatusCode(private val code: Int) {
    SUCCESS(0),
    IN_PROGRESS(1),
    INSUFFICIENT_FUNDS(2),
    CURRENCIES_NOT_MATCH(3),
    SOURCE_ACCOUNT_NOT_FOUND(4),
    TARGET_ACCOUNT_NOT_FOUND(5)
}
