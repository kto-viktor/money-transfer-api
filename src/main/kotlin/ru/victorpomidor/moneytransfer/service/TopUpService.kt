package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.TopUp
import java.util.UUID

interface TopUpService {
    fun executeTopUp(accountId: UUID, topUp: TopUp): TopUp
}
