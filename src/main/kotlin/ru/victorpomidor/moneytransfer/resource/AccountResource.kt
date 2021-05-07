package ru.victorpomidor.moneytransfer.resource

import org.glassfish.jersey.server.JSONP
import ru.victorpomidor.moneytransfer.model.Account
import ru.victorpomidor.moneytransfer.model.Subscribe
import ru.victorpomidor.moneytransfer.model.TopUp
import ru.victorpomidor.moneytransfer.service.AccountService
import ru.victorpomidor.moneytransfer.service.SubscribeService
import ru.victorpomidor.moneytransfer.service.TopUpService
import java.util.UUID
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.NotFoundException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("accounts")
class AccountResource(
    private val accountService: AccountService,
    private val topUpService: TopUpService,
    private val subscribeService: SubscribeService
) {
    @GET
    @Path("{id}")
    @JSONP
    @Produces(MediaType.APPLICATION_JSON)
    fun getAccount(@PathParam("id") id: UUID): Account {
        return accountService.getAccount(id) ?: throw NotFoundException()
    }

    @POST
    @JSONP
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createAccount(account: Account): Account {
        return accountService.createAccount(account)
    }

    @POST
    @Path("{id}/topup")
    @JSONP
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun topUp(@PathParam("id") id: String, topUp: TopUp): TopUp {
        return topUpService.executeTopUp(UUID.fromString(id), topUp)
    }

    @POST
    @Path("{id}/subscribe")
    @JSONP
    @Consumes(MediaType.APPLICATION_JSON)
    fun subscribe(@PathParam("id") id: UUID, subscribe: Subscribe) {
        subscribeService.subscribe(id, subscribe)
    }
}
