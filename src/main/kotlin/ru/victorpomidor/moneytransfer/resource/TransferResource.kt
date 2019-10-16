package ru.victorpomidor.moneytransfer.resource

import org.glassfish.jersey.server.JSONP
import ru.victorpomidor.moneytransfer.model.Transfer
import ru.victorpomidor.moneytransfer.service.TransferService
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("transfers")
class TransferResource(private val transferService: TransferService) {

    @POST
    @JSONP
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun transfer(transfer: Transfer): Transfer {
        return transferService.executeTransfer(transfer)
    }
}
