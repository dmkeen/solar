package org.keen.solar.solcast.measurement;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ser.std.StdSerializer;
import org.keen.solar.solcast.measurement.domain.Measurements;
import tools.jackson.databind.SerializationContext;

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
    public void serialize(Measurements value, JsonGenerator gen, SerializationContext provider) {
        gen.writeStartObject();
        if (value.getMeasurements().size() == 1) {
            provider.defaultSerializeProperty("measurement", value.getMeasurements().get(0), gen);
        } else if (value.getMeasurements().size() > 1) {
            provider.defaultSerializeProperty("measurements", value.getMeasurements(), gen);
        }
        gen.writeEndObject();
    }
}
