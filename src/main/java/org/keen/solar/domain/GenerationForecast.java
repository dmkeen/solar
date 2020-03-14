package org.keen.solar.domain;

import org.springframework.data.annotation.Id;

import java.time.Duration;

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
    private Double pv_estimate;
    /**
     * Solar panel power estimate in kilowatts, 10th percentile (low scenario)
     */
    private Double pv_estimate10;
    /**
     * Solar panel power estimate in kilowatts, 90th percentile (high scenario)
     */
    private Double pv_estimate90;
    /**
     * End of the averaging period, in ISO8601 format (UTC).
     */
    private String period_end;
    /**
     * End of the averaging period, in number of seconds since the epoch
     */
    private Long period_end_epoch;
    /**
     * Length of the averaging period
     */
    private Duration period;

    public GenerationForecast() {
    }

    public GenerationForecast(Double pv_estimate, Double pv_estimate10, Double pv_estimate90, String period_end, Long period_end_epoch, Duration period) {
        this.pv_estimate = pv_estimate;
        this.pv_estimate10 = pv_estimate10;
        this.pv_estimate90 = pv_estimate90;
        this.period_end = period_end;
        this.period_end_epoch = period_end_epoch;
        this.period = period;
    }

    public Long getId() {
        return id;
    }

    public Double getPv_estimate() {
        return pv_estimate;
    }

    public Double getPv_estimate10() {
        return pv_estimate10;
    }

    public Double getPv_estimate90() {
        return pv_estimate90;
    }

    public String getPeriod_end() {
        return period_end;
    }

    public Long getPeriod_end_epoch() {
        return period_end_epoch;
    }

    public Duration getPeriod() {
        return period;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
