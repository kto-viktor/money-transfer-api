package ru.victorpomidor.moneytransfer.resource

import entity
import org.junit.Assert.assertEquals
import org.junit.Test
import readBody
import ru.victorpomidor.moneytransfer.ApiIntegrationTest
import ru.victorpomidor.moneytransfer.model.Account
import ru.victorpomidor.moneytransfer.model.TopUp
import ru.victorpomidor.moneytransfer.model.Transfer
import ru.victorpomidor.moneytransfer.model.TransferStatus
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.CURRENCIES_NOT_MATCH
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.INSUFFICIENT_FUNDS
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.SOURCE_ACCOUNT_NOT_FOUND
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.SUCCESS
import ru.victorpomidor.moneytransfer.model.TransferStatusCode.TARGET_ACCOUNT_NOT_FOUND
import java.math.BigDecimal
import java.util.UUID
import javax.ws.rs.core.Response

class TransferIntegrationTest : ApiIntegrationTest() {
    private val topUp = TopUp(UUID.randomUUID().toString(), "system_id", BigDecimal.TEN)

    @Test
    fun shouldReturnSuccessTransfer() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount()
        callTopUp(sourceAccount, topUp)
        val transfer = getTransfer(sourceAccount, targetAccount)

        val response = callTransfer(transfer)

        assertEquals(200, response.status)
        val responseTransfer: Transfer = response.readBody()
        assertEquals(transfer.copy(status = TransferStatus(SUCCESS)), responseTransfer)
    }

    @Test
    fun shouldChangeBalance() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount()
        callTopUp(sourceAccount, topUp)
        val transfer = getTransfer(sourceAccount, targetAccount)

        callTransfer(transfer)
        val updatedSourceAccount = getAccount(sourceAccount.id)
        val updatedTargetAccount = getAccount(targetAccount.id)

        assertEquals(BigDecimal(4), updatedSourceAccount.balance)
        assertEquals(BigDecimal(6), updatedTargetAccount.balance)
    }

    @Test
    fun shouldCheckCurrency() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount(currency = "EUR")
        val transfer = getTransfer(sourceAccount, targetAccount)

        val response = callTransfer(transfer)

        assertEquals(CURRENCIES_NOT_MATCH, response.readBody<Transfer>().status!!.code)
    }

    @Test
    fun shouldReturnSourceAccountNotFound() {
        val sourceAccount = Account(UUID.randomUUID(), "name", currency = "USD")
        val targetAccount = createAccount()
        val transfer = getTransfer(sourceAccount, targetAccount)

        val response = callTransfer(transfer)

        assertEquals(SOURCE_ACCOUNT_NOT_FOUND, response.readBody<Transfer>().status!!.code)
    }

    @Test
    fun shouldReturnTargetAccountNotFound() {
        val sourceAccount = createAccount()
        val targetAccount = Account(UUID.randomUUID(), "name", currency = "USD")
        val transfer = getTransfer(sourceAccount, targetAccount)

        val response = callTransfer(transfer)

        assertEquals(TARGET_ACCOUNT_NOT_FOUND, response.readBody<Transfer>().status!!.code)
    }

    @Test
    fun shouldCheckBalance() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount()
        val transfer = getTransfer(sourceAccount, targetAccount)

        val response = callTransfer(transfer)

        assertEquals(INSUFFICIENT_FUNDS, response.readBody<Transfer>().status!!.code)
    }

    @Test
    fun idempotenceStatusSuccessTest() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount()
        callTopUp(sourceAccount, topUp)
        val transfer = getTransfer(sourceAccount, targetAccount)

        callTransfer(transfer)
        val response = callTransfer(transfer)

        assertEquals(SUCCESS, response.readBody<Transfer>().status!!.code)
    }

    @Test
    fun idempotenceBalanceShouldChangeOnceTest() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount()
        callTopUp(sourceAccount, topUp)
        val transfer = getTransfer(sourceAccount, targetAccount)

        callTransfer(transfer)
        callTransfer(transfer)
        val updatedSourceAccount = getAccount(sourceAccount.id)
        val updatedTargetAccount = getAccount(targetAccount.id)

        assertEquals(BigDecimal(4), updatedSourceAccount.balance)
        assertEquals(BigDecimal(6), updatedTargetAccount.balance)
    }

    @Test
    fun idempotenceConcurrentTest() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount()
        callTopUp(sourceAccount, topUp)
        val transfers = (1..100).map {
            getTransfer(
                id = "idempotence",
                sourceAccount = sourceAccount,
                targetAccount = targetAccount
            )
        }

        val responses = asyncCalls(transfers.map { { callTransfer(it) } })
        val updatedSourceAccount = getAccount(sourceAccount.id)
        val updatedTargetAccount = getAccount(targetAccount.id)

        responses.forEach {
            assertEquals(SUCCESS, it.readBody<Transfer>().status!!.code)
        }
        assertEquals(BigDecimal(4), updatedSourceAccount.balance)
        assertEquals(BigDecimal(6), updatedTargetAccount.balance)
    }

    @Test
    fun lotOfTransfersConcurrentTest() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount()
        callTopUp(sourceAccount, topUp.copy(amount = BigDecimal(1001)))
        val transfers = (1..100).map {
            getTransfer(sourceAccount, targetAccount, amount = BigDecimal.TEN)
        }

        val responses = asyncCalls(transfers.map { { callTransfer(it) } })
        val updatedSourceAccount = getAccount(sourceAccount.id)
        val updatedTargetAccount = getAccount(targetAccount.id)

        responses.forEach {
            assertEquals(SUCCESS, it.readBody<Transfer>().status!!.code)
        }
        assertEquals(BigDecimal(1), updatedSourceAccount.balance)
        assertEquals(BigDecimal(1000), updatedTargetAccount.balance)
    }

    @Test
    fun lotOfTransfersRaceConcurrentTest() {
        val sourceAccount = createAccount()
        val targetAccount = createAccount()
        callTopUp(sourceAccount, topUp.copy(amount = BigDecimal(11)))
        val transfers = (1..100).map {
            getTransfer(sourceAccount, targetAccount, amount = BigDecimal.TEN)
        }

        val responses = asyncCalls(transfers.map { { callTransfer(it) } })
        val updatedSourceAccount = getAccount(sourceAccount.id)
        val updatedTargetAccount = getAccount(targetAccount.id)
        val responseStatuses = responses.map { it.readBody<Transfer>().status!!.code }

        assertEquals(1, responseStatuses.filter { it == SUCCESS }.size)
        assertEquals(99, responseStatuses.filter { it == INSUFFICIENT_FUNDS }.size)
        assertEquals(BigDecimal(1), updatedSourceAccount.balance)
        assertEquals(BigDecimal(10), updatedTargetAccount.balance)
    }

    private fun getTransfer(
        sourceAccount: Account,
        targetAccount: Account,
        id: String = UUID.randomUUID().toString(),
        amount: BigDecimal = BigDecimal(6)
    ): Transfer {
        return Transfer(
            requestId = id,
            systemId = "system_id",
            sourceAccountId = sourceAccount.id,
            targetAccountId = targetAccount.id,
            amount = amount
        )
    }

    private fun callTransfer(transfer: Transfer): Response {
        return target("/transfers")
            .request()
            .post(transfer.entity())
    }
}
