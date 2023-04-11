-- Create schema and tables for storing data
CREATE SCHEMA IF NOT EXISTS solar
USE solar

-- TODO: Update schema to match CurrentPower
CREATE TABLE IF NOT EXISTS current_power (id SERIAL, inverter_epoch_timestamp BIGINT, inverter_zone_offset_seconds INT, app_time_difference BIGINT, generation NUMERIC(6,2), consumption NUMERIC(6,2), uploaded BOOLEAN, primary key (id), index idx_inverter_epoch_timestamp (inverter_epoch_timestamp))
CREATE TABLE IF NOT EXISTS generation_forecast (id SERIAL, pv_estimate NUMERIC(6,4), pv_estimate10 NUMERIC(6,4), pv_estimate90 NUMERIC(6,4), period_end VARCHAR(50), period_end_epoch BIGINT, period VARCHAR(50), primary key (id), index idx_period_end_epoch (period_end_epoch))
CREATE TABLE IF NOT EXISTS string_power (id SERIAL, period_end VARCHAR(50), period_end_epoch BIGINT, period VARCHAR(50), string1_volts NUMERIC(5,2), string1_amps NUMERIC(3,2), string1_power NUMERIC(7,3), string2_volts NUMERIC(5,2), string2_amps NUMERIC(3,2), string2_power NUMERIC(7,3), primary key (id), unique index idx_period_end_epoch (period_end_epoch))
-- Create application user
CREATE USER IF NOT EXISTS 'solarapp'@localhost IDENTIFIED BY 'put-your-password-here'
GRANT SELECT, INSERT, UPDATE ON solar.* TO solarapp@localhost
-- Create Grafana user
CREATE USER IF NOT EXISTS 'grafana'@localhost IDENTIFIED BY 'put-your-password-here'
GRANT SELECT ON solar.* TO grafana@localhost