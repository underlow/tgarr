package me.underlow.tgarr.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding


@ConstructorBinding
@ConfigurationProperties("tgbot")
data class TgBotConfiguration(
    val botToken: String,
)
