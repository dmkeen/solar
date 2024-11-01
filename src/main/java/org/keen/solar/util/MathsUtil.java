package org.keen.solar.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathsUtil {

    private MathsUtil() {}

    /**
     * Returns the given measurement, rounded to two decimal places.<br/>
     * <br/>
     * The Fronius API sometimes returns measurements that are slightly above
     * or below a particular value, apparently due to the imprecision of
     * representing a floating point value. For example, it might return
     * a value for P_PV of 1931.2000000001 or 1931.19999999999. These are best
     * interpreted as 1931.20.
     *
     * @param measurement the measurement returned from the API
     * @return measurement as a double
     */
    public static BigDecimal roundMeasurement(BigDecimal measurement) {
        return measurement
                .setScale(2, RoundingMode.HALF_UP);
    }

}
