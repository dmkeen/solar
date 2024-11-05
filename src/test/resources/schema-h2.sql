CREATE SCHEMA IF NOT EXISTS solar
SET SCHEMA solar

CREATE TABLE IF NOT EXISTS current_power (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, epoch_timestamp BIGINT, inverter_zone_offset_seconds INT, app_time_difference BIGINT, generation NUMERIC(6,2), consumption NUMERIC(6,2), uploaded BIT)
CREATE TABLE IF NOT EXISTS generation_forecast (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, pv_estimate DOUBLE, pv_estimate10 DOUBLE, pv_estimate90 DOUBLE, period_end_epoch BIGINT, period_length_seconds INT)
CREATE TABLE IF NOT EXISTS string_power (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, period_end_epoch BIGINT, period_length_seconds INT, string1_volts DOUBLE, string1_amps DOUBLE, string1_power DOUBLE, string2_volts DOUBLE, string2_amps DOUBLE, string2_power DOUBLE)
CREATE TABLE IF NOT EXISTS tariff (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, feed_in BIT, start_effective_date_epoch BIGINT, end_effective_date_epoch BIGINT, day_of_week VARCHAR(9), start_of_period TIME, end_of_period TIME, price_per_kwh DECFLOAT(4))
CREATE TABLE IF NOT EXISTS power_cost (id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, cost DECFLOAT(12), period_end_epoch BIGINT, period_length_seconds INT)