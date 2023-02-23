package me.underlow.tgarr.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import me.underlow.tgarr.configuration.ArrConfiguration
import me.underlow.tgarr.models.sonarr.SeriesResource
import mu.KotlinLogging
import okhttp3.OkHttpClient

class SonarrApiClient(private val configuration: ArrConfiguration.Sonarr) {

    private val ktorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        install(DefaultRequest) {
            header("X-Api-Key", configuration.key)
        }
    }

    val baseUrl = if (configuration.url.endsWith("/"))
        configuration.url.removeSuffix("/")
    else
        configuration.url

    suspend fun lookup(imdbId: String): List<SeriesResource> {
        val response = ktorClient.get(lookupUrl) {
            url {
                parameters.append("term", imdbId)
            }
        }

        if (response.status.value != 200) {
            logger.error { "Cannot execute request to radarr" }
            return emptyList()
        }

        return response.body()
    }


    suspend fun addSeries(series: SeriesResource): ActionResult {
        val response = ktorClient.post(seriesUrl) {
            contentType(ContentType.Application.Json)
            setBody(series)
        }

        return when (response.status) {
            HttpStatusCode.Created -> {
                logger.info { "Series ${series.title} added successfully" }
                Success("Series ${series.title} added successfully")
            }

            else -> {
                logger.error { "Request to add series failed with code ${response.status}" }
                Error("Request to add series failed with code ${response.status}")
            }
        }
    }


    private val lookupUrl = "${baseUrl}/api/v3/series/lookup"
    private val seriesUrl = "${baseUrl}/api/v3/series"

}

private val logger = KotlinLogging.logger { }
