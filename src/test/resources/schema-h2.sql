CREATE SCHEMA IF NOT EXISTS solar
SET SCHEMA solar
CREATE TABLE IF NOT EXISTS current_power (id IDENTITY PRIMARY KEY, inverter_timestamp VARCHAR(50), inverter_epoch_timestamp BIGINT, application_timestamp VARCHAR(50), generation NUMERIC(6,2), consumption NUMERIC(6,2))
CREATE TABLE IF NOT EXISTS generation_forecast (id INTEGER IDENTITY PRIMARY KEY, pv_estimate DOUBLE, pv_estimate10 DOUBLE, pv_estimate90 DOUBLE, period_end TIMESTAMP, period VARCHAR(50))