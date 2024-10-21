package org.keen.solar.solcast.measurement;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.keen.solar.solcast.measurement.domain.Measurements;

import java.io.IOException;

/**
 * Serializes Measurements according to the requirements of the
 * <a href="https://docs.solcast.com.au/#measurements-rooftop-site">Solcast API</a>.
 * In particular, a single measurement is serialized into a field 'measurement', while multiple measurements
 * are serialized into a field 'measurements'.
 */
public class MeasurementsSerializer extends StdSerializer<Measurements> {

    public MeasurementsSerializer() {
        super(Measurements.class);
    }

    @Override
    public void serialize(Measurements value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        if (value.getMeasurements().size() == 1) {
            provider.defaultSerializeField("measurement", value.getMeasurements().get(0), gen);
        } else if (value.getMeasurements().size() > 1) {
            provider.defaultSerializeField("measurements", value.getMeasurements(), gen);
        }
        gen.writeEndObject();
    }
}
