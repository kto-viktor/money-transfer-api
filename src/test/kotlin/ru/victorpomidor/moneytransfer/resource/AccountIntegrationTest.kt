package ru.victorpomidor.moneytransfer.resource

import entity
import org.junit.Assert.assertEquals
import org.junit.Test
import readBody
import ru.victorpomidor.moneytransfer.ApiIntegrationTest
import ru.victorpomidor.moneytransfer.model.Account
import java.math.BigDecimal
import java.util.UUID

class AccountIntegrationTest : ApiIntegrationTest() {
    @Test
    fun notFound() {
        val response = target("/accounts/00112233-4455-6677-8899-aabbccddeef1")
            .request()
            .get()

        assertEquals(404, response.status)
    }

    @Test
    fun create() {
        val id = UUID.randomUUID()
        val account = Account(
            id,
            "my account",
            currency = "USD"
        )

        val response = target("/accounts/")
            .request()
            .post(account.entity())

        assertEquals(200, response.status)
        assertEquals(account, response.readBody<Account>())
    }

    @Test
    fun createdAccountBalanceAlwaysZero() {
        val id = UUID.randomUUID()
        val account = Account(
            id,
            "my account",
            BigDecimal.TEN,
            "USD"
        )

        val response = target("/accounts/")
            .request()
            .post(account.entity())

        assertEquals(200, response.status)
        assertEquals(account.copy(balance = BigDecimal.ZERO), response.readBody<Account>())
    }

    @Test
    fun createAndGet() {
        val id = UUID.randomUUID()
        val account = Account(
            id,
            "my account",
            currency = "USD"
        )
        target("/accounts/")
            .request()
            .post(account.entity())

        val response = target("/accounts/$id")
            .request()
            .get()

        assertEquals(200, response.status)
        assertEquals(account, response.readBody<Account>())
    }

    @Test
    fun idempotenceTest() {
        val id = UUID.randomUUID()
        val account = Account(
            id,
            "my account",
            currency = "USD"
        )
        target("/accounts/")
            .request()
            .post(account.entity())
        val response = target("/accounts/")
            .request()
            .post(account.entity())

        assertEquals(200, response.status)
        assertEquals(account, response.readBody<Account>())
    }

    @Test
    fun idempotenceConcurrentTest() {
        val id = UUID.randomUUID()
        val account = Account(
            id,
            "my account",
            currency = "USD"
        )
        target("/accounts/")
            .request()
            .post(account.entity())

        val responses = asyncCalls(100) {
            target("/accounts/")
                .request()
                .post(account.entity())
        }

        responses.forEach {
            assertEquals(200, it.status)
            assertEquals(account, it.readBody<Account>())
        }
    }
}
