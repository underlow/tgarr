# tgarr
Telegram bot for quick add movies/tv to sonarr/radarr, just send it a link to imdb title (for example with share button in IMDB mobile application)

## Deploy

Use docker image `underlow/tgarr:<version>`, and provide following properties:

```yaml

tgbot:
  botToken: your bot token

arr:
  radarr:
    url: url to your radarr instance
    key: radarr access key
    rootFolderPath: root folder path, find it in import dialog
    monitored: true if you want movie added in monitored state
    qualityProfileId: 
    minimumAvailability: 
    searchForMovie: true if you want to search for movie when it's added
  sonarr:
    url: url to your sonarr instance
    key: sonarr access key
    rootFolderPath: root folder path, find it in import dialog
    monitored: true if you want movie added in monitored state
    qualityProfileId: 
    searchForMissingEpisodes: true if you want to search for movie when it's added
    languageProfileId: 

```
Find all necessary ids either with brute force or corresponding api requests (for example try GET https://sonarr/api/v3/qualityprofile for quality profile )

Example of docker-compose file:
```yaml
version: "3.5"
services:
  tgarr:
    image: underlow/tgarr
    container_name: tgarr
    environment:
      - PUID=$USER_ID
      - PGID=$GROUP_ID
      - TZ=Europe/London
      - tgbot.botToken=
      - arr.radarr.url=http://radarr:7878
      - arr.radarr.key=
      - arr.radarr.rootFolderPath=/movies
      - arr.radarr.monitored=true
      - arr.radarr.qualityProfileId=7
      - arr.radarr.minimumAvailability=released
      - arr.radarr.searchForMovie=false
      - arr.sonarr.key=
      - arr.sonarr.url=http://sonarr:8989
      - arr.sonarr.rootFolderPath=/tv
      - arr.sonarr.monitored=false
      - arr.sonarr.qualityProfileId=6
      - arr.sonarr.searchForMissingEpisodes=false
      - arr.sonarr.languageProfileId=2
    restart: unless-stopped
```
