package ru.victorpomidor.moneytransfer.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import ru.victorpomidor.moneytransfer.model.Account

class AsyncCallbackService(private val subscribeService: SubscribeService) : CallbackService {
    private val httpClient: OkHttpClient = OkHttpClient()
    private val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun sendCallback(account: Account) {
        val subscribe = subscribeService.getSubscribe(account.id)
        if (subscribe != null) {
            runBlocking {
                launch {
                    val body: RequestBody = RequestBody.create(JSON, objectMapper.writeValueAsString(account))
                    httpClient
                        .newCall(buildPost(subscribe.callbackUrl, body))
                        .execute()
                        .use { response ->
                            println("Callback ${subscribe.callbackUrl} response code: ${response.code}")
                        }
                }
            }
        }
    }

    private fun buildPost(
        callbackUrl: String,
        body: RequestBody
    ) = Request.Builder()
        .url(callbackUrl)
        .post(body)
        .build()
}
