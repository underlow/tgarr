package me.underlow.tgarr.service

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.underlow.tgarr.clients.ActionResult
import me.underlow.tgarr.clients.Error
import me.underlow.tgarr.configuration.TgBotConfiguration
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component


@EnableConfigurationProperties(TgBotConfiguration::class)
@Component
class TgBotService(
    private val configuration: TgBotConfiguration,
    private val imdbService: ImdbService,
    private val arrService: ArrService
) {
    private final val bot: TelegramBot

    init {
        logger.info { "Initializing Telegram Bot with token ${configuration.botToken.take(4)}*****" }
        bot = TelegramBot(configuration.botToken)
        logger.info { "Telegram Bot successfully initialized" }

        bot.setUpdatesListener { updates ->// process updates
            logger.info { "Bot got ${updates.size} messages, processing" }
            updates.forEach { processMessage(it) }
            logger.info { "Successfully processed ${updates.size} messages" }
            UpdatesListener.CONFIRMED_UPDATES_ALL
        }
    }

    private fun processMessage(request: Update) {
        logger.info { "Message: ${request.message()}" }
        val link = request.message().text()
        val actionResult = when (val imdbLink = imdbService.parseLink(link)) {
            is Movie -> arrService.addMovie(imdbLink)
            is Series -> arrService.addSeries(imdbLink)
            null -> Error("$link is nor a valid link to IMDB title")
        }
        sendResponse(request, actionResult)
    }

    private fun sendResponse(request: Update, actionResult: ActionResult) {
        val sendMessage = SendMessage(request.message().chat().id(), actionResult.message)
            .parseMode(ParseMode.HTML)
            .disableWebPagePreview(true)
            .replyToMessageId(request.message().messageId())

        val sendResponse = bot.execute(sendMessage)
        // todo: retry?
        when (sendResponse.isOk) {
            true -> logger.info { "Response for user request ${request.message().text()} has been sent successfully" }
            false -> logger.info { "Response for user request ${request.message().text()} has not been sent" }
        }
    }
}

private val logger = KotlinLogging.logger { }
