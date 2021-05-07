package com.fjd.social;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * @author I312177
 *
 */
public class ObjectMapperUtil {
    public static void initObjectMapper(ObjectMapper objectMapper) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        FilterProvider filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
        objectMapper.setFilterProvider(filterProvider);
    }
}
