CREATE SCHEMA IF NOT EXISTS solar
USE solar
CREATE TABLE IF NOT EXISTS current_power (id SERIAL, inverter_timestamp VARCHAR(50), inverter_epoch_timestamp BIGINT, application_timestamp VARCHAR(50), generation NUMERIC(6,2), consumption NUMERIC(6,2), uploaded BOOLEAN, primary key (id))
CREATE TABLE IF NOT EXISTS generation_forecast (id SERIAL, pv_estimate NUMERIC(6,4), pv_estimate10 NUMERIC(6,4), pv_estimate90 NUMERIC(6,4), period_end VARCHAR(50), period_end_epoch BIGINT, period VARCHAR(50), primary key (id))