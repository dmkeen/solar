package org.keen.solar.solcast.measurement;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ser.std.StdSerializer;
import org.keen.solar.solcast.measurement.domain.Measurement;
import tools.jackson.databind.SerializationContext;

import java.time.format.DateTimeFormatter;

/**
 * Serializes Measurements according to the requirements of the
 * <a href="https://docs.solcast.com.au/#measurements-rooftop-site">Solcast API</a>.
 */
public class MeasurementSerializer extends StdSerializer<Measurement> {

    public static final String PERIOD_END_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    public MeasurementSerializer() {
        super(Measurement.class);
    }

    @Override
    public void serialize(Measurement value, JsonGenerator gen, SerializationContext provider) {
        gen.writeStartObject();
        gen.writeStringProperty("period_end", value.getPeriod_end().format(DateTimeFormatter.ofPattern(PERIOD_END_FORMAT)));
        gen.writeStringProperty("period", value.getPeriod().toString());
        gen.writeNumberProperty("total_power", value.getTotal_power());
        gen.writeEndObject();
    }
}
