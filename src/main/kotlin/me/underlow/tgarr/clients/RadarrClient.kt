package me.underlow.tgarr.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import me.underlow.tgarr.configuration.ArrConfiguration
import me.underlow.tgarr.models.radarr.AddMovieOptions
import me.underlow.tgarr.models.radarr.MovieResource
import me.underlow.tgarr.models.radarr.MovieStatusType
import me.underlow.tgarr.service.Movie
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RadarrClient(private val configuration: ArrConfiguration) {

    private val radarrUrl = when (configuration.radarr.url.endsWith("/")) {
        true -> configuration.radarr.url
        false -> configuration.radarr.url + "/"
    }
    private val movieLookupUrl = "${radarrUrl}api/v3/movie/lookup/imdb"

    fun addMovie(imdbLink: Movie): ActionResult {
        // lookup movie
        logger.info { "Looking for a movie $imdbLink" }
        val movie = lookup(imdbLink)
        if (movie == null) {
            logger.info { "Cannot find movie $imdbLink" }
            return Error("Cannot find movie $imdbLink")
        }

        logger.info { "Lookup $imdbLink completed with result $movie" }

        // add movie
        val result = addMovie(movie)
        logger.info { "Adding movie got $result " }
        return result
    }

    private fun lookup(imdbLink: Movie): MovieResource? {
        try {
            val httpBuilder = movieLookupUrl.toHttpUrlOrNull()!!
                .newBuilder()
                .addQueryParameter("imdbId", imdbLink.imdbId)

            val request: Request = Request.Builder()
                .url(httpBuilder.build())
                .addHeader("X-Api-Key", configuration.radarr.key)
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

    private fun addMovie(movie: MovieResource): ActionResult {
        movie.id = 0
        movie.rootFolderPath = configuration.radarr.rootFolderPath
        movie.monitored = configuration.radarr.monitored
        movie.qualityProfileId = configuration.radarr.qualityProfileId
        movie.minimumAvailability = MovieStatusType.valueOf(configuration.radarr.minimumAvailability)
        val addMovieOptions = movie.addOptions ?: AddMovieOptions()
        movie.addOptions = addMovieOptions.copy(searchForMovie = configuration.radarr.searchForMovie)

        val body: RequestBody = objectMapper.writeValueAsString(movie).toRequestBody()

        val request: Request = Request.Builder()
            .url("${radarrUrl}api/v3/movie")
            .addHeader("X-Api-Key", configuration.radarr.key)
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
}

private var client = OkHttpClient()
private var objectMapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule())


sealed interface ActionResult{
    val message: String
}

data class Success(override val message: String) : ActionResult
data class Error(override val message: String) : ActionResult

private val logger = KotlinLogging.logger { }
