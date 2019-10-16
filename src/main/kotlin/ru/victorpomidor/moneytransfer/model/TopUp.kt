package ru.victorpomidor.moneytransfer.model

import java.math.BigDecimal

data class TopUp(
    val requestId: String,
    val systemId: String,
    val amount: BigDecimal,
    val status: TopUpStatus? = null
) {
    fun requestKey() = RequestKey(requestId, systemId)
}

enum class TopUpStatus(val code: Int) {
    SUCCESS(0),
    IN_PROGRESS(1),
    ACCOUNT_NOT_FOUND(2)
}
