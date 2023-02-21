package me.underlow.tgarr.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import me.underlow.tgarr.configuration.ArrConfiguration
import me.underlow.tgarr.models.radarr.MovieResource
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class RadarrApiClient(private val configuration: ArrConfiguration.Radarr) {

    private val baseUrl = if (configuration.url.endsWith("/"))
        configuration.url.removeSuffix("/")
    else
        configuration.url


    fun lookup(imdbId: String): MovieResource? {
        try {
            val httpBuilder = lookupUrl.toHttpUrlOrNull()!!
                .newBuilder()
                .addQueryParameter("imdbId", imdbId)

            val request: Request = Request.Builder()
                .url(httpBuilder.build())
                .addHeader("X-Api-Key", configuration.key)
                .addHeader("Content-Type", "application/json")
                .get()
                .build()
            client.newCall(request).execute().use { response ->
                val body = response.body ?: return null
                return objectMapper.readValue(body.string(), MovieResource::class.java)
            }
        } catch (e: Exception) {
            logger.error(e) { "Cannot execute request to radarr" }
            return null
        }
    }


    fun addMovie(movie: MovieResource): ActionResult {
        val body: RequestBody = objectMapper.writeValueAsString(movie).toRequestBody()

        val request: Request = Request.Builder()
            .url(movieUrl)
            .addHeader("X-Api-Key", configuration.key)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        try {
            //todo: retry on HTTP 500
            client.newCall(request).execute().use { response ->
                when (response.code) {
                    // radarr is not very consistent with return code
                    // sometimes it is 200 sometimes 400, keep 400 for now
                    400 -> {
                        logger.info { "Movie ${movie.title} has already been added" }
                        return Success("Movie ${movie.title} has already been added")
                    }

                    201 -> {
                        logger.info { "Movie ${movie.title} added successfully" }
                        return Success("Movie ${movie.title} added successfully")
                    }

                    else -> {
                        logger.error { "Request to add movie failed with code ${response.code}" }
                        return Error("Request to add movie failed with code ${response.code}")
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Cannot execute request to radarr" }
            return Error("Cannot execute request to radarr")
        }
    }

    private val lookupUrl = "${baseUrl}/api/v3/movie/lookup/imdb"
    private val movieUrl = "${baseUrl}/api/v3/movie"

    private var client = OkHttpClient()
    private var objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())

}

private val logger = KotlinLogging.logger { }
