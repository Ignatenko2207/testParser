package com.mywork.finance.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
public class UrlUtils {

	public static void printAllHeaders(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			Enumeration<String> headers = request.getHeaders(headerName);
			while (headers.hasMoreElements()) {
				String headerValue = headers.nextElement();
				log.info("Header %s = %s", headerName, headerValue);
			}
		}
	}

	public static String getOriginDomain(HttpServletRequest request) {
		String originHeader = request.getHeader("Origin");
		if (StringUtils.isNotBlank(originHeader)) {
			return originHeader.replaceAll(".*\\.(?=.*\\.)", "");
		} else {
			return null;
		}
	}
}
