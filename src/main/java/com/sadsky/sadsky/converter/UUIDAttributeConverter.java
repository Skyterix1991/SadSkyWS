package com.sadsky.sadsky.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.UUID;

/**
 * Prevents MySQL from having issues with storing UUID.
 */
@Converter(autoApply = true)
class UUIDAttributeConverter implements AttributeConverter<UUID, String> {
    /**
     * Converts UUID to String.
     *
     * @param uuid Public id.
     * @return Converted String.
     */

    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return uuid.toString();
    }

    /**
     * Converts String to UUID.
     *
     * @param uuid String to be converted.
     * @return Converted UUID.
     */
    @Override
    public UUID convertToEntityAttribute(String uuid) {
        return UUID.fromString(uuid);
    }
}