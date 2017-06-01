package com.declaratiiavere.common.utils;

import com.fasterxml.jackson.databind.JsonSerializer;

import java.io.IOException;
import java.util.Date;

/**
 * JSON date serializer.
 *
 * @author Razvan Dani
 */
public class JsonDateSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, com.fasterxml.jackson.databind.SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(DateUtilities.formatDate(date, DateUtilities.YEAR_MONTH_DAY_PATTERN));
    }
}
