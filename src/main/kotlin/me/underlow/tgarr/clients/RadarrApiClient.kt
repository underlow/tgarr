package me.underlow.tgarr.clients

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import me.underlow.tgarr.configuration.ArrConfiguration
import me.underlow.tgarr.models.radarr.MovieResource
import mu.KotlinLogging


class RadarrApiClient(private val configuration: ArrConfiguration.Radarr) {

    private val ktorClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        install(DefaultRequest) {
            header("X-Api-Key", configuration.key)
        }
    }

    private val baseUrl = if (configuration.url.endsWith("/"))
        configuration.url.removeSuffix("/")
    else
        configuration.url


    suspend fun lookup(imdbId: String): MovieResource? {
        val response = ktorClient.get(lookupUrl) {
            url {
                parameters.append("imdbId", imdbId)
            }
        }

        if (response.status.value != 200) {
            logger.error { "Cannot execute request to radarr" }
            return null
        }

        return response.body()
    }


    suspend fun addMovie(movie: MovieResource): ActionResult {
        val response = ktorClient.post(movieUrl) {
            contentType(ContentType.Application.Json)
            setBody(movie)
        }

        when (response.status) {
            // radarr is not very consistent with return code
            // sometimes it is 200 sometimes 400, keep 400 for now
            HttpStatusCode.BadRequest -> {
                logger.info { "Movie ${movie.title} has already been added" }
                return Success("Movie ${movie.title} has already been added")
            }

            HttpStatusCode.Created -> {
                logger.info { "Movie ${movie.title} added successfully" }
                return Success("Movie ${movie.title} added successfully")
            }

            else -> {
                logger.error { "Request to add movie failed with code ${response.status}" }
                return Error("Request to add movie failed with code ${response.status}")
            }
        }
    }

    private val lookupUrl = "${baseUrl}/api/v3/movie/lookup/imdb"
    private val movieUrl = "${baseUrl}/api/v3/movie"

}

private val logger = KotlinLogging.logger { }
