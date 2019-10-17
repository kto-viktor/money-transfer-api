package ru.victorpomidor.moneytransfer.service

import ru.victorpomidor.moneytransfer.model.Account
import ru.victorpomidor.moneytransfer.model.ChangeBalanceResult
import java.math.BigDecimal
import java.util.UUID
import javax.validation.constraints.Positive

/**
 * Account CRU
 */
interface AccountService {
    fun getAccount(accountId: UUID): Account?

    fun createAccount(account: Account): Account

    /**
     * Add a balance to account
     * @param accountId account id
     * @param value, should be positive
     * @return ChangeBalanceResult.SUCCESS if success
     * @throws IllegalArgumentException if value is <= 0
     */
    fun addBalance(accountId: UUID, @Positive value: BigDecimal): ChangeBalanceResult

    /**
     * Subtract a balance from account
     * @param accountId account id
     * @param value, should be positive
     * @return ChangeBalanceResult.SUCCESS if success, NOT_ENOUGH_BALANCE, if balance is too low
     * @throws IllegalArgumentException if value is <= 0
     */
    fun subtractBalance(accountId: UUID, @Positive value: BigDecimal): ChangeBalanceResult
}
