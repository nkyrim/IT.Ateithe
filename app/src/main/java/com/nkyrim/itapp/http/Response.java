package com.nkyrim.itapp.http;

import java.util.List;
import java.util.Map;

/**
 * The response of a specific http request. Object creation with a fluent API. If no encoding is set, UTF-8 is used by default.
 *
 * @author Nick Kyrimlidis
 */
public class Response {
	private int code;
	private String message;
	private String encoding;
	private byte[] body;
	private Map<String, List<String>> headers;

	private Response(int code) {
		this.code = code;
		this.encoding = HttpUtil.UTF8;
	}

	public static Response create(int code) {
		return new Response(code);
	}

	//=========================================
	public int code() {
		return code;
	}

	public boolean success() {
		return code >= 200 && code < 300;
	}

	public String message() {
		return message;
	}

	@SuppressWarnings("unused")
	public Map<String, List<String>> headerFields() {
		return headers;
	}

	public byte[] bodyBytes() {
		return body;
	}

	public String bodyString() {
		return HttpUtil.makeString(body, encoding);
	}

	//=========================================
	public Response encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public Response message(String message) {
		this.message = message;
		return this;
	}

	public Response headers(Map<String, List<String>> headerFields) {
		this.headers = headerFields;
		return this;
	}

	public Response body(byte[] response) {
		this.body = response;
		return this;
	}
}
