package ru.victorpomidor.moneytransfer.config

import org.glassfish.jersey.server.monitoring.ApplicationEvent
import org.glassfish.jersey.server.monitoring.ApplicationEventListener
import org.glassfish.jersey.server.monitoring.RequestEvent
import org.glassfish.jersey.server.monitoring.RequestEventListener
import org.slf4j.LoggerFactory
import javax.ws.rs.WebApplicationException
import javax.ws.rs.ext.Provider

private val log = LoggerFactory.getLogger(ExceptionLogger::class.java)

/**
 * Hack for logging all exceptions in jersey
 */
@Provider
class ExceptionLogger : ApplicationEventListener, RequestEventListener {
    override fun onEvent(applicationEvent: ApplicationEvent) {
        // do nothing, we need only request events
    }

    override fun onRequest(requestEvent: RequestEvent): RequestEventListener {
        return this
    }

    override fun onEvent(paramRequestEvent: RequestEvent) {
        if (paramRequestEvent.type == RequestEvent.Type.ON_EXCEPTION) {
            val exception = paramRequestEvent.exception
            if (exception is WebApplicationException) {
                log.warn("Handled web exception", exception)
            } else {
                log.error("Request error handled", exception)
            }
        }
    }
}
