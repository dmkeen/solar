CREATE SCHEMA IF NOT EXISTS solar
SET SCHEMA solar
CREATE TABLE IF NOT EXISTS current_power (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, inverter_timestamp VARCHAR(50), inverter_epoch_timestamp BIGINT, application_timestamp VARCHAR(50), generation NUMERIC(6,2), consumption NUMERIC(6,2), uploaded BIT)
CREATE TABLE IF NOT EXISTS generation_forecast (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, pv_estimate DOUBLE, pv_estimate10 DOUBLE, pv_estimate90 DOUBLE, period_end VARCHAR(50), period_end_epoch BIGINT, period VARCHAR(50))
CREATE TABLE IF NOT EXISTS string_power (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, period_end VARCHAR(50), period_end_epoch BIGINT, period VARCHAR(50), string1_volts DOUBLE, string1_amps DOUBLE, string1_power DOUBLE, string2_volts DOUBLE, string2_amps DOUBLE, string2_power DOUBLE)