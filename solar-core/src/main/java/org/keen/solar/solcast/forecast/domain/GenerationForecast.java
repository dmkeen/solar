package org.keen.solar.solcast.forecast.domain;

import org.springframework.data.annotation.Id;

/**
 * Solcast solar panel output forecast.
 */
public class GenerationForecast {

    /**
     * Id for this forecast. Used by the persistence layer.
     */
    @Id
    private Long id;
    /**
     * Solar panel power estimate in kilowatts
     */
    private double pv_estimate;
    /**
     * Solar panel power estimate in kilowatts, 10th percentile (low scenario)
     */
    private double pv_estimate10;
    /**
     * Solar panel power estimate in kilowatts, 90th percentile (high scenario)
     */
    private double pv_estimate90;
    /**
     * End of the averaging period, in number of seconds since the epoch
     */
    private long period_end_epoch;
    /**
     * Length of the averaging period in seconds
     */
    private int period_length_seconds;

    public GenerationForecast() {
    }

    public GenerationForecast(double pv_estimate, double pv_estimate10, double pv_estimate90, long period_end_epoch, int period_length_seconds) {
        this.pv_estimate = pv_estimate;
        this.pv_estimate10 = pv_estimate10;
        this.pv_estimate90 = pv_estimate90;
        this.period_end_epoch = period_end_epoch;
        this.period_length_seconds = period_length_seconds;
    }

    public Long getId() {
        return id;
    }

    public double getPv_estimate() {
        return pv_estimate;
    }

    public double getPv_estimate10() {
        return pv_estimate10;
    }

    public double getPv_estimate90() {
        return pv_estimate90;
    }

    public long getPeriod_end_epoch() {
        return period_end_epoch;
    }

    public int getPeriod_length_seconds() {
        return period_length_seconds;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
