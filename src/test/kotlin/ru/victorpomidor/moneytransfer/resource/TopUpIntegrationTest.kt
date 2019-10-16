package ru.victorpomidor.moneytransfer.resource

import entity
import org.junit.Assert.assertEquals
import org.junit.Test
import readBody
import ru.victorpomidor.moneytransfer.ApiIntegrationTest
import ru.victorpomidor.moneytransfer.model.TopUp
import ru.victorpomidor.moneytransfer.model.TopUpStatus.ACCOUNT_NOT_FOUND
import ru.victorpomidor.moneytransfer.model.TopUpStatus.SUCCESS
import java.math.BigDecimal
import java.util.UUID

class TopUpIntegrationTest : ApiIntegrationTest() {

    @Test
    fun notFound() {
        val topUp = TopUp("request_id", "system_id", BigDecimal.TEN)

        val response = target("/accounts/00112233-4455-6677-8899-aabbccddeef1/topup")
            .request()
            .post(topUp.entity())

        assertEquals(200, response.status)
        val responseTopUp: TopUp = response.readBody()
        assertEquals(topUp.copy(status = ACCOUNT_NOT_FOUND), responseTopUp)
    }

    @Test
    fun shouldReturnSuccessTopUp() {
        val topUp = TopUp(UUID.randomUUID().toString(), "system_id", BigDecimal.TEN)
        val account = createAccount()

        val response = callTopUp(account, topUp)

        assertEquals(200, response.status)
        val responseTopUp: TopUp = response.readBody()
        assertEquals(topUp.copy(status = SUCCESS), responseTopUp)
    }

    @Test
    fun shouldAddBalance() {
        val topUp = TopUp(UUID.randomUUID().toString(), "system_id", BigDecimal.TEN)
        val account = createAccount()
        callTopUp(account, topUp)

        val updatedAccount = getAccount(account.id)

        assertEquals(BigDecimal.TEN, updatedAccount.balance)
    }

    @Test
    fun shouldAddBalanceTwice() {
        val topUp1 = TopUp(UUID.randomUUID().toString(), "system_id", BigDecimal.TEN)
        val topUp2 = TopUp(UUID.randomUUID().toString(), "system_id", BigDecimal.ONE)
        val account = createAccount()
        callTopUp(account, topUp1)
        callTopUp(account, topUp2)

        val updatedAccount = getAccount(account.id)

        assertEquals(BigDecimal.valueOf(11), updatedAccount.balance)
    }

    @Test
    fun idempotenceStatusTest() {
        val topUp1 = TopUp("request_id", "system_id", BigDecimal.TEN)
        val topUp2 = TopUp("request_id", "system_id", BigDecimal.ONE)
        val account = createAccount()
        callTopUp(account, topUp1)
        val response2 = callTopUp(account, topUp2)

        assertEquals(200, response2.status)
        assertEquals(SUCCESS, response2.readBody<TopUp>().status)
    }

    @Test
    fun idempotenceBalanceTest() {
        val topUp1 = TopUp("request_id", "system_id", BigDecimal.TEN)
        val topUp2 = TopUp("request_id", "system_id", BigDecimal.ONE)
        val account = createAccount()
        callTopUp(account, topUp1)
        callTopUp(account, topUp2)

        val updatedAccount = getAccount(account.id)

        assertEquals(BigDecimal.valueOf(10), updatedAccount.balance)
    }

    @Test
    fun concurrentRequestsResponsesCheck() {
        val topUps = (1..100).map { TopUp(UUID.randomUUID().toString(), "system_id", BigDecimal.TEN) }
        val account = createAccount()
        val responses = asyncCalls(topUps.map { { callTopUp(account, it) } })

        responses.forEach {
            assertEquals(200, it.status)
            val topUp = it.readBody<TopUp>()
            assertEquals(SUCCESS, topUp.status)
            assertEquals(BigDecimal.TEN, topUp.amount)
        }
    }

    @Test
    fun concurrentRequestsBalanceCheck() {
        val topUps = (1..100).map { TopUp(UUID.randomUUID().toString(), "system_id", BigDecimal.TEN) }
        val account = createAccount()

        asyncCalls(topUps.map { { callTopUp(account, it) } })
        val updatedAccount = getAccount(account.id)

        assertEquals(BigDecimal.valueOf(1000), updatedAccount.balance)
    }
}
