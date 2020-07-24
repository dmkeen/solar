# Solar monitoring app
Monitors solar panel output and persists it to a backend.
Also retrieves solar forecasts from Solcast and uploads actual output to tune the forecast.

## Roadmap
* Utilise Solcast's tuning API (v1.2)
* Use RedisTimeSeries as backend (v2.0)
  * https://github.com/RedisTimeSeries/RedisTimeSeries
  * Java api https://github.com/RedisTimeSeries/JRedisTimeSeries/
  * Not sure if this integrates with Spring Data Redis https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#
* Add cost of energy (v2.1)