package com.LR.openRana.utils;


import com.LR.openRana.common.LLException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
public final class JSONUtils {

    private static ObjectMapper objectMapper;

    /**
     * 获取ObjectMapper实例的方法。
     * 这个方法是静态的，可以通过类名直接调用，用于获取全局共享的ObjectMapper实例。
     *
     * @return ObjectMapper 返回一个ObjectMapper的实例。
     */
    public static ObjectMapper getInstance() {
        return objectMapper;
    }


    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
    }

    public static void setObjectMapper(ObjectMapper o) {
        objectMapper = o;
    }

    public static String toJSONString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("", e);
            throw new LLException("toJSONFailed");
        }
    }

    public static String toJSONString(Object o, boolean pretty) {
        try {

            return pretty ? objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o) : objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("", e);
            throw new LLException("toJSONFailed");
        }
    }

    public static Map<String, Object> toJSONMap(String json) {
        return toJavaObject(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public static Map<String, Object> toJSONMap(Object object) {
        return toJSONMap(toJSONString(object));
    }

    public static <T> T toJavaObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("", e);
            throw new LLException("toJSONFailed");
        }
    }

    public static <T> T toJavaObject(String json, TypeReference<? extends T> tTypeReference) {
        try {
            return objectMapper.readValue(json, tTypeReference);
        } catch (IOException e) {
            log.error("", e);
            throw new LLException("toJSONFailed");
        }
    }
}
