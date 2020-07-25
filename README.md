# Solar monitoring app
Monitors solar panel output.
* Instantaneous power generation and consumption (via Fronius inverter API)
* Retrieves Solcast solar forecasts
* Uploads actual generation to tune Solcast forecast

## Roadmap
* Use RedisTimeSeries as backend (v2.0)
  * https://github.com/RedisTimeSeries/RedisTimeSeries
  * Java api https://github.com/RedisTimeSeries/JRedisTimeSeries/
  * Not sure if this integrates with Spring Data Redis https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#
* Add cost of energy (v2.1)