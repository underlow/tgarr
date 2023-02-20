package me.underlow.tgarr.service

import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.stereotype.Component


@Component
class ImdbService {
    fun parseLink(link: String): ImdbLink? {
        logger.debug { "Parsing $link" }

        val imdbId = getImdbIdOrNull(link)

        if (imdbId == null) {
            logger.debug { "$link is not the imdb link" }
            return null
        }

        // ok, now we have to understand if it is movie or series

        return when (getTitlePageAndParse(link)) {
            TitleType.Movie -> {
                logger.info { "$link is a movie with id $imdbId" }
                Movie(imdbId)
            }

            TitleType.Series -> {
                logger.info { "$link is a series with id $imdbId" }
                Series(imdbId)
            }
        }
    }

    private val regexp = Regex("(tt[0-9]+)")

    fun getImdbIdOrNull(link: String): String? {
        if (!link.contains(imdbString))
            return null

        val matchResult = regexp.find(link)

        if (matchResult != null && matchResult.groups.isNotEmpty()) {
            return matchResult.groups[0]?.value
        }
        return null
    }

    /**
     * There's two options here get imdb dataset to be more precise or parse webpage.
     * Data set is approx 300 Mb and this bot is not supposed to add hundreds of movies a day
     * so to keep traffic low let's parse webpage
     */
    fun getTitlePageAndParse(link: String): TitleType {
        logger.debug { "Getting imdb page and extracting title type $link" }
        val doc: Document = Jsoup.connect(link).get()
        val items: Elements = doc.getElementsByClass("ipc-inline-list__item")
        return when {
            items.any { it.text().contains("TV Series") } -> TitleType.Series
            else -> TitleType.Movie
        }
    }
}

private val logger = KotlinLogging.logger { }


private const val imdbString = "imdb.com"

enum class TitleType { Movie, Series }

sealed interface ImdbLink
data class Movie(val imdbId: String) : ImdbLink
data class Series(val imdbId: String) : ImdbLink
