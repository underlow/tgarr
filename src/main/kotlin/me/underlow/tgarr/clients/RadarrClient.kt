package me.underlow.tgarr.clients

import kotlinx.coroutines.runBlocking
import me.underlow.tgarr.configuration.ArrConfiguration
import me.underlow.tgarr.models.radarr.AddMovieOptions
import me.underlow.tgarr.models.radarr.MovieResource
import me.underlow.tgarr.models.radarr.MovieStatusType
import me.underlow.tgarr.service.Movie
import mu.KotlinLogging

class RadarrClient(private val configuration: ArrConfiguration) {

    private val radarrAPIClient = RadarrApiClient(configuration.radarr)

    fun addMovie(imdbLink: Movie): ActionResult = runBlocking {
        // lookup movie
        logger.info { "Looking for a movie $imdbLink" }
        val movie = radarrAPIClient.lookup(imdbLink.imdbId)

        if (movie == null) {
            logger.info { "Cannot find movie $imdbLink" }
            return@runBlocking Error("Cannot find movie $imdbLink")
        }

        logger.info { "Lookup $imdbLink completed with result $movie" }

        // add movie
        val result = addMovieInRadarr(movie)
        logger.info { "Adding movie got $result " }
        return@runBlocking result
    }

    private suspend fun addMovieInRadarr(movie: MovieResource): ActionResult {
        val addMovieOptions = movie.addOptions ?: AddMovieOptions()
        val request = movie.copy(
            id = 0,
            rootFolderPath = configuration.radarr.rootFolderPath,
            monitored = configuration.radarr.monitored,
            qualityProfileId = configuration.radarr.qualityProfileId,
            minimumAvailability = MovieStatusType.valueOf(configuration.radarr.minimumAvailability),
            addOptions = addMovieOptions.copy(searchForMovie = configuration.radarr.searchForMovie)
        )

        return radarrAPIClient.addMovie(request)
    }
}

private val logger = KotlinLogging.logger { }
