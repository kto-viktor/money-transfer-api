package ru.victorpomidor.moneytransfer

import entity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.netty.connector.NettyConnectorProvider
import org.glassfish.jersey.test.JerseyTest
import org.glassfish.jersey.test.netty.NettyTestContainerFactory
import org.glassfish.jersey.test.spi.TestContainerException
import org.glassfish.jersey.test.spi.TestContainerFactory
import readBody
import ru.victorpomidor.moneytransfer.config.ApiResourceConfig
import ru.victorpomidor.moneytransfer.config.DiConfig
import ru.victorpomidor.moneytransfer.model.Account
import ru.victorpomidor.moneytransfer.model.TopUp
import java.util.UUID
import javax.ws.rs.core.Application
import javax.ws.rs.core.Response

open class ApiIntegrationTest : JerseyTest() {
    override fun configure(): Application {
        return ApiResourceConfig(DiConfig())
    }

    override fun configureClient(clientConfig: ClientConfig) {
        clientConfig.connectorProvider(NettyConnectorProvider())
    }

    @Throws(TestContainerException::class)
    override fun getTestContainerFactory(): TestContainerFactory {
        return NettyTestContainerFactory()
    }

    fun createAccount(currency: String = "USD"): Account {
        val id = UUID.randomUUID()
        val account = Account(
            id,
            "my account",
            currency = currency,
            token = "123"
        )
        val response = target("/accounts/")
            .request()
            .post(account.entity())
        check(response.status == 200) { "account creation failed" }
        return response.readBody()
    }

    fun getAccount(id: UUID): Account {
        val response = target("/accounts/$id")
            .request()
            .get()
        check(response.status == 200) { "getAccount status is ${response.status}" }
        return response
            .readBody()
    }

    fun callTopUp(
        account: Account,
        topUp: TopUp
    ): Response {
        return target("/accounts/${account.id}/topup")
            .request()
            .post(topUp.entity())
    }

    fun asyncCalls(count: Int, call: () -> Response): List<Response> {
        lateinit var responses: List<Response>
        runBlocking {
            responses = (1..count).map {
                GlobalScope.async { call.invoke() }
            }.awaitAll()
        }
        return responses
    }

    fun asyncCalls(calls: List<() -> Response>): List<Response> {
        lateinit var responses: List<Response>
        runBlocking {
            responses = calls.map {
                GlobalScope.async { it.invoke() }
            }.awaitAll()
        }
        return responses
    }
}
