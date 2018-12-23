package com.nkyrim.itapp.http;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpUtil {
	public final static String UTF8 = "utf-8";

	public static URL makeURL(String urlString) {
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Bad URL", e);
		}
	}

	@SuppressWarnings("unused")
	public static URI makeURI(String uriString) {
		try {
			return new URI(uriString);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String makeString(byte[] content, String encoding) {
		try {
			return new String(content, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Encoding: " + encoding + " not supported", e);
		}
	}
}
