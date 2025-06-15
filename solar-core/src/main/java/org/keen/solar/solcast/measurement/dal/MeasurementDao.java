package org.keen.solar.solcast.measurement.dal;

public interface MeasurementDao {

    /**
     * Returns the timestamp of the last uploaded measurement to Solcast in epoch seconds.
     *
     * @return the epoch timestamp of the last uploaded measurement, or 0 if no measurements have been uploaded yet
     */
    long getLastUploadedEpochTimestamp();

    /**
     * Sets the timestamp of the last uploaded measurement to Solcast in epoch seconds.
     *
     * @param epochSeconds the epoch timestamp to set as the last uploaded measurement time
     */
    void setLastUploadedEpochTimestamp(long epochSeconds);
}
