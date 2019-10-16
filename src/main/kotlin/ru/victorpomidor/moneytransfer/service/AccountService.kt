package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.Account
import ru.victorpomidor.moneytransfer.model.ChangeBalanceResult
import java.math.BigDecimal
import java.util.UUID

interface AccountService {
    fun getAccount(accountId: UUID): Account?

    fun createAccount(account: Account): Account

    fun addBalance(accountId: UUID, value: BigDecimal): ChangeBalanceResult

    fun subtractBalance(accountId: UUID, value: BigDecimal): ChangeBalanceResult
}
