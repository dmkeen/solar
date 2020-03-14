CREATE SCHEMA IF NOT EXISTS solar
USE solar
CREATE TABLE IF NOT EXISTS current_power (id INTEGER AUTO_INCREMENT, timestamp DATETIME, generation NUMERIC(6,2), consumption NUMERIC(6,2), primary key (id))
CREATE TABLE IF NOT EXISTS generation_forecast (id INTEGER AUTO_INCREMENT, pv_estimate NUMERIC(6,4), pv_estimate10 NUMERIC(6,4), pv_estimate90 NUMERIC(6,4), period_end DATETIME, period VARCHAR(50), primary key (id))