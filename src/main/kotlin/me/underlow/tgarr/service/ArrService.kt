package me.underlow.tgarr.service

import me.underlow.tgarr.clients.ActionResult
import me.underlow.tgarr.clients.Error
import me.underlow.tgarr.clients.RadarrClient
import me.underlow.tgarr.clients.SonarrClient
import me.underlow.tgarr.configuration.ArrConfiguration
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component


@Component
@EnableConfigurationProperties(ArrConfiguration::class)
class ArrService(configuration: ArrConfiguration) {
    val radarrClient = RadarrClient(configuration)
    val sonarrClient = SonarrClient(configuration)

    fun addMovie(imdbLink: Movie): ActionResult = radarrClient.addMovie(imdbLink)

    fun addSeries(imdbLink: Series): ActionResult  = sonarrClient.addSeries(imdbLink)

}


private val logger = KotlinLogging.logger { }
