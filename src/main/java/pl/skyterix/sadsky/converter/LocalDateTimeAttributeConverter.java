package pl.skyterix.sadsky.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Converter(autoApply = true)
class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    /**
     * Converts LocalDateTime to Timestamp.
     *
     * @param localDateTime LocalDateTime to be converted.
     * @return Converted TimeStamp.
     */
    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
        return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
    }

    /**
     * Converts Timestamp to LocalDateTime.
     *
     * @param timestamp TimeStamp to be converted.
     * @return Converted LocalDateTime.
     */
    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}