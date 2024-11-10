# Solar monitoring app
Monitors solar panel output using the Fronius inverter API and power consumption in realtime and 
persists the data to a MySQL database for display in Grafana.

* Monitors instantaneous power generation and consumption
* Calculate cost of power used
* Monitors per-string power generation
* Retrieves Solcast solar forecasts (https://solcast.com/)
* Uploads actual generation to tune the Solcast forecast

# Usage
Configure solar panel system in Solcast

Setup and run the app:
* Build the application using Maven: `mvn clean package`
* Install and run MySQL (https://www.mysql.com/ or https://mariadb.org/)
* Setup the database using `/src/main/resources/mysql/setup.sql` (substitute your own passwords)
* Add power tariffs (see below for details)
* Configure properties in `application.properties`
  * `app.inverter.host` - hostname/IP of the inverter web API
  * `app.solcast.api-key` - your Solcast API key value
  * `app.solcast.site-id` - your Solcast site ID
  * `spring.datasource.username` - your MySQL app user
  * `spring.datasource.password` - your MySQL app user password
* Run the application

To view the output, use Grafana:
* Install and run Grafana
* Configure the MySQL datasource
* Import the Solar dashboard found in `/src/main/resources/grafana/Solar.json`

Sample output:
![Grafana dashboard](grafana-dashboard-hd.webp)

## Power tariff configuration
There are two types of tariffs:
* Usage - what you are charged for using electricity
* Feed-in - what you are paid (or possibly charged) for exporting electricity

**Both tariffs must be configured for each day of the week, from 00:00 to 23:59:59.**

The tariff table structure is:

| Column | Type    | Description                                                                                                                                    |
|--------|---------|------------------------------------------------------------------------------------------------------------------------------------------------|
| feed_in | boolean | `FALSE` for a usage tariff, `TRUE` for a feed-in tariff                                                                                        |
| start_effective_date_epoch | bigint | The time when this tariff begins to be effective, in seconds since the epoch                                                                   |
| end_effective_date_epoch | bigint | The time when this tariff stops being effective, in seconds since the epoch. Can be null.                                                      |
| day_of_week | varchar(9) | The day of the week, in capitals. For example SUNDAY.                                                                                          |
| start_of_period | time | The time of day when this tariff starts being effective. For example, 00:00 or 15:00.                                                          |
| end_of_period | time | The time of day when this tariff stops being effective. For example 10:00 or 15:00. Ensure that the last period for each day ends at 23:59:59. |
| price_per_kwh | numeric(5,4) | The price for one kilowatt-hour (kWh). For example, 0.2174.                                                                                    |

The following examples use Sunday, 10 November 2024 GMT (epoch time 1731196800) as the starting effective date for the tariff.
### Flat tariff example
If you are paid \$0.033 per kWh for export, regardless of the time of day, then the insert
statement for Sunday would look like:
`INSERT INTO tariff (feed_in, start_effective_date_epoch, day_of_week, start_of_period, end_of_period, price_per_kwh) VALUES (TRUE,1731196800,'SUNDAY','00:00','23:59:59',0.033);`

### Time-of-use tariff example
If you are charged different amounts depending on the time of day, then you will have
multiple rows per day.

If you are charged \$0.2174 between midnight and 3pm, \$0.4082 between 3pm and 9pm and
\$0.2174 between 9pm and midnight, the insert statements for Sunday would look like:

`INSERT INTO tariff (feed_in, start_effective_date_epoch, day_of_week, start_of_period, end_of_period, price_per_kwh) VALUES (FALSE,1731196800,'SUNDAY','00:00','15:00',0.2174);
INSERT INTO tariff (feed_in, start_effective_date_epoch, day_of_week, start_of_period, end_of_period, price_per_kwh) VALUES (FALSE,1731196800,'SUNDAY','15:00','21:00',0.4082);
INSERT INTO tariff (feed_in, start_effective_date_epoch, day_of_week, start_of_period, end_of_period, price_per_kwh) VALUES (FALSE,1731196800,'SUNDAY','21:00','23:59:59',0.2174);`