package com.mywork.finance.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class JacksonUtils {

	private static ObjectMapper mapper = getMapper();

	private static ObjectMapper getMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
		return objectMapper;
	}

	public static String getJson(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Cannot get json from the object %s", object);
		}
		return null;
	}

	public static void getJson(Object object, File file) throws IOException {
		mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
	}

	public static String getJson(Class<?> view, Object object) throws JsonProcessingException {
		return mapper.writerWithView(view).writeValueAsString(object);
	}

	public static String getPrettyJson(Object object) throws JsonProcessingException {
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
	}

	public static String getPrettyJson(Class<?> view, Object object) throws JsonProcessingException {
		return mapper.writerWithDefaultPrettyPrinter().withView(view).writeValueAsString(object);
	}

	public static <T> T fromJson(Class<T> clazz, String json) {
		try {
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			log.error("Cannot object %s from a json %s", clazz, json);
		}
		return null;
	}

	public static <T> T fromJson(Class<T> clazz, File file) throws IOException {
		return mapper.readValue(file, clazz);
	}
}
