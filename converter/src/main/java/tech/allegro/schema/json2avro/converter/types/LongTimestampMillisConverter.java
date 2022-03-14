package tech.allegro.schema.json2avro.converter.types;

import org.apache.avro.AvroTypeException;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Deque;

import static org.apache.avro.Schema.Type.LONG;
import static tech.allegro.schema.json2avro.converter.PathsPrinter.print;
import static tech.allegro.schema.json2avro.converter.types.AvroTypeConverter.isLogicalType;

public class LongTimestampMillisConverter implements AvroTypeConverter {
    public static final AvroTypeConverter INSTANCE = new LongTimestampMillisConverter(DateTimeFormatter.ISO_DATE_TIME);
    public static final String VALID_JSON_FORMAT = "date time string, timestamp number";

    private final DateTimeFormatter dateTimeFormatter;

    public LongTimestampMillisConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Object convert(Schema.Field field, Schema schema, Object jsonValue, Deque<String> path, boolean silently) {
        if (jsonValue instanceof String) {
            String dateString = (String) jsonValue;
            try {
                return Instant.from(dateTimeFormatter.parse(dateString)).toEpochMilli();
            } catch (DateTimeParseException exception) {
                if (silently) {
                    return new Incompatible(VALID_JSON_FORMAT);
                } else {
                    throw new AvroTypeException("Field " + print(path) + " should be a valid date time.");
                }
            }
        } else if (jsonValue instanceof Number) {
            return ((Number) jsonValue).longValue();
        }

        if (silently) {
            return new Incompatible(VALID_JSON_FORMAT);
        } else {
            throw new AvroTypeException("Field " + print(path) + " is expected to be type: java.lang.String or java.lang.Number.");
        }
    }

    @Override
    public boolean canManage(Schema schema, Deque<String> deque) {
        return LONG.equals(schema.getType()) && isLogicalType(schema, LogicalTypes.timestampMillis().getName());
    }
}
