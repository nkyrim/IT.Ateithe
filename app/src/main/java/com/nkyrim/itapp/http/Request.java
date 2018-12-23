package com.nkyrim.itapp.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Options that set the encoding and additional headers or parameters for a specific request. Object creation with a method chaining. If no
 * encoding is set the default UTF-8 is used.
 *
 * @author Nick Kyrimlidis
 */
public class Request {
	public enum Type {GET, POST}

	private String url;
	private String encoding;
	private boolean multipart;
	private Type type;
	private HashMap<String, String> headers;
	private HashMap<String, String> formParams;
	private HashMap<String, String> queryParams;
	private HashMap<String, File> files;

	private Request() {
		this.encoding = HttpUtil.UTF8;
		this.headers = new HashMap<>();
		this.queryParams = new HashMap<>();
		this.formParams = new HashMap<>();
		this.files = new HashMap<>();
	}

	//==== create =============================
	public static Request get(String url) {
		Request r = new Request();
		r.url = url;
		r.type = Type.GET;

		return r;
	}

	public static Request post(String url) {
		Request r = new Request();
		r.url = url;
		r.type = Type.POST;

		return r;
	}

	//=======================================
	public String charset() {
		return encoding;
	}

	public Type type() {
		return type;
	}

	public String url() {
		String finalUrl = url;
		// add all params
		if(!queryParams.isEmpty()) {
			finalUrl += "?";
			for (Entry<String, String> e : queryParams.entrySet()) {
				finalUrl += e.getKey() + "=" + e.getValue() + "&";
			}
		}

		// remove possible "&" at the end
		if(finalUrl.endsWith("&")) finalUrl = finalUrl.substring(0, finalUrl.length() - 1);

		return finalUrl;
	}

	public boolean multipart() {
		return multipart;
	}

	public Set<Entry<String, String>> getHeaders() {
		return headers.entrySet();
	}

	public Set<Entry<String, String>> getForm() {
		return formParams.entrySet();
	}

	public Set<Entry<String, File>> getFiles() {
		return files.entrySet();
	}

	//=======================================
	public Request charset(String encoding) {
		this.encoding = encoding;
		return this;
	}

	/**
	 * Force the request to be multipart/form-data request regardless if files files are aadded or not
	 *
	 * @param multipart - if the request is multipart
	 */
	public Request multipart(boolean multipart) {
		this.multipart = multipart;
		return this;
	}

	/**
	 * Add cookies to the request, convenience method to use instead of header
	 *
	 * @param cookie The cookie to add
	 */
	@SuppressWarnings("unused")
	public void cookie(HttpCookie cookie) {
		String cookies = headers.get("Cookie");
		if(cookies != null) cookies = cookies + ";" + cookie.toString();
		else cookies = cookie.toString();

		headers.put("Cookie", cookies);
	}

	/**
	 * Add a header to the request
	 *
	 * @param name  - The name of the header
	 * @param value - The value of the header
	 */
	public Request header(String name, String value) {
		headers.put(name, value);
		return this;
	}

	/**
	 * Add parameters for a POST request
	 *
	 * @param name  - The name of the parameter
	 * @param value - The value of the parameter
	 */
	public Request query(String name, String value) {
		queryParams.put(name, value);

		return this;
	}

	/**
	 * Add parameters for a POST request
	 *
	 * @param name  - The name of the parameter
	 * @param value - The value of the parameter
	 */
	public Request form(String name, String value) {
		formParams.put(name, value);

		return this;
	}

	/**
	 * Add file parameters for a multipart/form-data POST request(file upload)
	 *
	 * @param name  - The name of the parameter
	 * @param value - The file to be uploaded
	 */
	public Request form(String name, File value) {
		files.put(name, value);
		multipart = true;

		return this;
	}

	//========= other methods =====================================================
	public byte[] getFormParams() {
		if(formParams.isEmpty()) throw new RuntimeException("No form parameters set");

		StringBuilder result = new StringBuilder();

		for (Entry<String, String> e : formParams.entrySet()) {

			// if there are already some formParams added
			if(result.length() > 0) {
				result.append("&");
			}

			result.append(e.getKey());
			result.append("=");
			try {
				result.append(URLEncoder.encode(e.getValue(), encoding));
			} catch (UnsupportedEncodingException ex) {
				throw new IllegalArgumentException(ex);
			}
		}

		byte[] params;
		try {
			params = result.toString().getBytes(encoding);
		} catch (UnsupportedEncodingException e1) {
			params = result.toString().getBytes();
		}

		return params;
	}

}
