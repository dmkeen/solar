package org.keen.solar.solcast.measurement;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.keen.solar.solcast.measurement.domain.Measurement;

import java.io.IOException;
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
    public void serialize(Measurement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("period_end", value.getPeriod_end().format(DateTimeFormatter.ofPattern(PERIOD_END_FORMAT)));
        gen.writeStringField("period", value.getPeriod().toString());
        gen.writeNumberField("total_power", value.getTotal_power());
        gen.writeEndObject();
    }
}
