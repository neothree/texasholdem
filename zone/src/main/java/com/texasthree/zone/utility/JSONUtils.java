package com.texasthree.zone.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

/**
 * @author: neo
 * @create: 2022-07-11 15:09
 */
public class JSONUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        var module = new JavaTimeModule();
        module.addSerializer(new LocalDateTimeSerializer(DateUtils.LOCAL_LONG_DATE_FORMAT));
        module.addSerializer(new LocalDateSerializer(DateUtils.LOCAL_SHORT_DATE_FORMAT));
        mapper.registerModule(module);
    }

    public static String toString(Object map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static <T> T convertValue(Object object, Class<T> toValueType) {
        return mapper.convertValue(object, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) throws IllegalArgumentException {
        return mapper.convertValue(fromValue, toValueTypeRef);
    }

    public static <T> T readValue(String object, Class<T> toValueType) throws JsonProcessingException, JsonMappingException {
        return mapper.readValue(object, toValueType);
    }

    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException, JsonMappingException {
        return mapper.readValue(content, valueTypeRef);
    }

    public static JsonNode readTree(String object) throws Exception {
        return mapper.readTree(object);
    }
}
