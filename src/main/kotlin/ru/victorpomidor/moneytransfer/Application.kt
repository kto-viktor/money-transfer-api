package ru.victorpomidor.moneytransfer

import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider
import ru.victorpomidor.moneytransfer.config.ApiResourceConfig
import ru.victorpomidor.moneytransfer.config.DiConfig
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val baseUri = if (args.isNotEmpty()) {
                    URI.create(args[0])
                } else {
                    URI.create("http://localhost:8081/")
                }

                val resourceConfig = ApiResourceConfig(DiConfig())
                val server = NettyHttpContainerProvider.createHttp2Server(baseUri, resourceConfig, null)

                Runtime.getRuntime().addShutdownHook(Thread(Runnable { server.close() }))

                println("Application started")
                Thread.currentThread().join()
            } catch (ex: InterruptedException) {
                Logger.getLogger(ApiResourceConfig::class.java.name).log(Level.SEVERE, null, ex)
            }
        }
    }
}
