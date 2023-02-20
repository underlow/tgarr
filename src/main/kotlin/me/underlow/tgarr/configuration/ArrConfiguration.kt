package me.underlow.tgarr.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConstructorBinding
@ConfigurationProperties("arr")
data class ArrConfiguration(
    @NestedConfigurationProperty
    val radarr: Radarr,
    @NestedConfigurationProperty
    val sonarr: Sonarr
)

@ConstructorBinding
data class Sonarr(
    val url: String,
    val key: String,
    val rootFolderPath: String,
    val monitored: Boolean,
    val qualityProfileId: Int,
    val searchForMissingEpisodes: Boolean,
    val languageProfileId: Int
)

@ConstructorBinding
data class Radarr(
    val url: String,
    val key: String,
    val rootFolderPath: String,
    val monitored: Boolean,
    val qualityProfileId: Int,
    val minimumAvailability: String,
    val searchForMovie: Boolean
)
