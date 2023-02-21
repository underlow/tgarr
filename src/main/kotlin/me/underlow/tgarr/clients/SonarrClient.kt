package me.underlow.tgarr.clients

import kotlinx.coroutines.runBlocking
import me.underlow.tgarr.configuration.ArrConfiguration
import me.underlow.tgarr.models.sonarr.AddSeriesOptions
import me.underlow.tgarr.models.sonarr.SeriesResource
import me.underlow.tgarr.service.Series
import mu.KotlinLogging

class SonarrClient(private val configuration: ArrConfiguration) {

    private val sonarrApiClient = SonarrApiClient(configuration.sonarr)

    fun addSeries(imdbLink: Series): ActionResult = runBlocking {
        logger.info { "Looking for a movie $imdbLink" }
        val seriesList = sonarrApiClient.lookup(imdbLink.imdbId)

        val series = seriesList.firstOrNull { it.imdbId == imdbLink.imdbId }

        if (series == null) {
            logger.info { "Lookup $imdbLink failed, series not found" }
            return@runBlocking Error("Lookup $imdbLink failed, series not found")
        }
        logger.info { "Lookup $imdbLink completed with result $series" }

        val result = addMovieInSonarr(series)
        logger.info { "Adding movie got $result " }
        return@runBlocking result
    }


    private suspend fun addMovieInSonarr(series: SeriesResource): ActionResult {
        val addMovieOptions = series.addOptions ?: AddSeriesOptions()
        val request = series.copy(
            id = 0,
            rootFolderPath = configuration.sonarr.rootFolderPath,
            monitored = configuration.sonarr.monitored,
            qualityProfileId = configuration.sonarr.qualityProfileId,
            languageProfileId = configuration.sonarr.languageProfileId,
            addOptions = addMovieOptions.copy(searchForMissingEpisodes = configuration.sonarr.searchForMissingEpisodes),
        )

        return sonarrApiClient.addSeries(request)
    }
}

private val logger = KotlinLogging.logger { }
