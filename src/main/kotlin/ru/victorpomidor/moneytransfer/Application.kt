package ru.victorpomidor.moneytransfer

import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider
import ru.victorpomidor.moneytransfer.config.ApiResourceConfig
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger

private val BASE_URI = URI.create("http://localhost:8081/")
fun main(args: Array<String>) {
    try {
        val resourceConfig = ApiResourceConfig()
        val server = NettyHttpContainerProvider.createHttp2Server(BASE_URI, resourceConfig, null)

        Runtime.getRuntime().addShutdownHook(Thread(Runnable { server.close() }))

        println("Application started")
        Thread.currentThread().join()
    } catch (ex: InterruptedException) {
        Logger.getLogger(ApiResourceConfig::class.java.name).log(Level.SEVERE, null, ex)
    }
}
