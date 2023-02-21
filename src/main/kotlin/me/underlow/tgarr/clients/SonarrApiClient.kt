package me.underlow.tgarr.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import me.underlow.tgarr.configuration.ArrConfiguration
import me.underlow.tgarr.models.sonarr.SeriesResource
import me.underlow.tgarr.service.Series
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class SonarrApiClient(private val configuration: ArrConfiguration.Sonarr) {

    private val baseUrl = if (configuration.url.endsWith("/"))
        configuration.url.removeSuffix("/")
    else
        configuration.url

    fun lookup(imdbLink: Series): List<SeriesResource>? {
        try {
            val httpBuilder = movieLookupUrl.toHttpUrlOrNull()!!
                .newBuilder()
                .addQueryParameter("term", imdbLink.imdbId)

            val request: Request = Request.Builder()
                .url(httpBuilder.build())
                .addHeader("X-Api-Key", configuration.key)
                .addHeader("Content-Type", "application/json")
                .get()
                .build()
            client.newCall(request).execute().use { response ->
                val body = response.body ?: return null
                return objectMapper.readValue<List<SeriesResource>>(body.string())
            }
        } catch (e: Exception) {
            logger.error(e) { "Cannot execute request to sonarr" }
            return null
        }
    }


    fun addSeries(series: SeriesResource): ActionResult {
        val body: RequestBody = objectMapper.writeValueAsString(series).toRequestBody()

        val request: Request = Request.Builder()
            .url(seriesUrl)
            .addHeader("X-Api-Key", configuration.key)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        try {
            //todo: retry on HTTP 500
            client.newCall(request).execute().use { response ->
                when (response.code) {
                    // sonarr returns 400 on
                    400 -> {
                        logger.info { "Series ${series.title} has already been added" }
                        return Success("Series ${series.title} has already been added")
                    }
                    201 -> {
                        logger.info { "Series ${series.title} added successfully" }
                        return Success("Series ${series.title} added successfully")
                    }

                    else -> {
                        logger.error { "Request to add series failed with code ${response.code}" }
                        return Error("Request to add series failed with code ${response.code}")
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Cannot execute request to sonarr" }
            return Error("Cannot execute request to sonarr")
        }
    }

    private val movieLookupUrl = "${baseUrl}/api/v3/series/lookup"
    private val seriesUrl = "${baseUrl}/api/v3/series"

}

private var client = OkHttpClient()
private var objectMapper = ObjectMapper().registerModule(JavaTimeModule()).registerKotlinModule()

private val logger = KotlinLogging.logger { }
