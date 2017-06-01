package com.declaratiiavere.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * JSON  date deserializer.
 *
 * @author Razvan Dani
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException {
        return DateUtilities.parseDate(jsonParser.getText(), DateUtilities.YEAR_MONTH_DAY_PATTERN);
    }
}
