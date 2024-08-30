package com.dyk.test.service.table

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Duration

data class PriceRequest(val tickers: List<String>, val startDate: String, val endDate: String)
data class BaseRequest(val tickers: List<String>)

@Service
class ApiService(
    restTemplateBuilder: RestTemplateBuilder,
    @Value("\${api.host}") private val apiHost: String,
    @Value("\${api.port}") private val apiPort: Int
) {

    private val restTemplate: RestTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(120))
        .build()

    fun fetchInfo(tickers: List<String>): BaseRequest? {
        val url = "http://$apiHost:$apiPort/fetch/info"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request =  HttpEntity(BaseRequest(tickers), headers)
        return restTemplate.exchange(url, HttpMethod.POST, request, BaseRequest::class.java).body

    }

    fun fetchPrice(tickers: List<String>): String? {
        val url = "http://$apiHost:$apiPort/fetch/price"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val request = HttpEntity(
            PriceRequest(tickers, "2018-01-01", "2024-07-31"), headers)

        return restTemplate.exchange(url, HttpMethod.POST, request, String::class.java).body
    }
}