package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.Account
import ru.victorpomidor.moneytransfer.model.ChangeBalanceResult
import ru.victorpomidor.moneytransfer.model.ChangeBalanceResult.NOT_ENOUGH_BALANCE
import ru.victorpomidor.moneytransfer.model.ChangeBalanceResult.SUCCESS
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryAccountService : AccountService {
    private val accounts: MutableMap<UUID, Account> = ConcurrentHashMap()

    override fun createAccount(account: Account): Account {
        val newAccount = account.copy(balance = ZERO)
        accounts[account.id] = newAccount
        return newAccount
    }

    override fun addBalance(accountId: UUID, value: BigDecimal): ChangeBalanceResult {
        require(value > ZERO) { "value should be positive" }
        return changeBalance(accountId, value)
    }

    override fun subtractBalance(accountId: UUID, value: BigDecimal): ChangeBalanceResult {
        require(value > ZERO) { "value should be positive" }
        return changeBalance(accountId, value.negate())
    }

    override fun getAccount(accountId: UUID): Account? {
        return accounts[accountId]
    }

    private fun changeBalance(accountId: UUID, value: BigDecimal): ChangeBalanceResult {
        var oldAccount: Account
        do {
            oldAccount = accounts[accountId] ?: throw IllegalArgumentException("account doesn't exist")
            if (value < ZERO && oldAccount.balance < value.abs()) {
                return NOT_ENOUGH_BALANCE
            }
        } while (!checkAndSet(accountId, oldAccount, value))
        return SUCCESS
    }

    private fun checkAndSet(accountId: UUID, oldAccount: Account, value: BigDecimal): Boolean {
        return accounts.replace(accountId, oldAccount, oldAccount.copy(balance = oldAccount.balance.add(value)))
    }
}
