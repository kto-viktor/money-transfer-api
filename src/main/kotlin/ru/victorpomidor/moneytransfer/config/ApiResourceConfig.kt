package ru.victorpomidor.moneytransfer.config

import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.logging.LoggingFeature.DEFAULT_LOGGER_NAME
import org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY
import org.glassfish.jersey.server.ResourceConfig
import java.util.logging.Level
import java.util.logging.Logger

class ApiResourceConfig : ResourceConfig() {
    companion object {
        const val MAX_ENTITY_SIZE = 10000
    }

    init {
        val diContainer = DiConfig()

        register(diContainer.accountResource)
        register(diContainer.transferResource)

        register(
            LoggingFeature(Logger.getLogger(DEFAULT_LOGGER_NAME), Level.INFO, PAYLOAD_ANY, MAX_ENTITY_SIZE)
        )
        register(ObjectMapperContextResolver::class.java)
        register(ExceptionLogger::class.java)
    }
}
