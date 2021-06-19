# Solar monitoring app
Monitors solar panel output using the Fronius inverter API and power consumption in realtime and 
persists the data to a MySQL database for display in Grafana.

* Monitors instantaneous power generation and consumption
* Monitors per-string power generation
* Retrieves Solcast solar forecasts (https://solcast.com/)
* Uploads actual generation to tune Solcast forecast

# Usage
Configure solar panel system in Solcast

Setup and run the app:
* Build the application using Maven: `mvn clean package`
* Install and run MySQL (https://www.mysql.com/ or https://mariadb.org/)
* Setup the database using `/src/main/resources/mysql/setup.sql` (substitute your own passwords)
* Configure properties in `application.properties`
* Run the application

To view the output, use Grafana:
* Install and run Grafana
* Configure the MySQL datasource
* Import the Solar dashboard found in `/src/main/resources/grafana/Solar.json`
