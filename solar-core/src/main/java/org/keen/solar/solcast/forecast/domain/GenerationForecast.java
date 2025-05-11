package org.keen.solar.solcast.forecast.domain;

/**
 * Solcast solar panel output forecast.
 *
 * @param pv_estimate           Solar panel power estimate in kilowatts
 * @param pv_estimate10         Solar panel power estimate in kilowatts, 10th percentile (low scenario)
 * @param pv_estimate90         Solar panel power estimate in kilowatts, 90th percentile (high scenario)
 * @param period_end_epoch      End of the averaging period, in number of seconds since the epoch
 * @param period_length_seconds Length of the averaging period in seconds
 */
public record GenerationForecast(double pv_estimate, double pv_estimate10, double pv_estimate90,
                                 long period_end_epoch, int period_length_seconds) {

}
