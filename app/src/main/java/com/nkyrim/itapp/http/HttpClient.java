package com.nkyrim.itapp.http;

import com.nkyrim.itapp.util.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map.Entry;

/**
 * Http Client that uses HttpUrlConnection for the http requests
 *
 * @author Nick Kyrimlidis
 */
public class HttpClient {
	private final static String TAG = "HttpClient";
	private final String USER_AGENT = "HttpClient";
	private final int TIMEOUT = 20 * 1000;

	/**
	 * Create a new HttpClient and set an application wide cookieStore handler with a default InMemoryCookieStore
	 */
	public HttpClient() {
		CookieHandler.setDefault(new CookieManager());
	}

	public CookieStore cookieStore() {
		return ((CookieManager) CookieHandler.getDefault()).getCookieStore();
	}

	public Response send(Request request) {
		if(request.type() == Request.Type.GET) return get(request);
		else if(request.multipart()) return postMultipart(request);
		else return post(request);
	}

	private Response get(Request request) {
		HttpURLConnection conn = null;
		URL url = HttpUtil.makeURL(request.url());
		InputStream in = null;
		Response response;

		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept-Charset", request.charset());

			for (Entry<String, String> e : request.getHeaders()) {
				conn.setRequestProperty(e.getKey(), e.getValue());
			}

			in = conn.getInputStream();
			response = Response.create(conn.getResponseCode())
							   .message(conn.getResponseMessage())
							   .headers(conn.getHeaderFields())
							   .body(readBytes(in))
							   .encoding(request.charset());

		} catch (SocketTimeoutException ex) {
			response = Response.create(-1).message("Connection Timeout");
			Logger.e(TAG, "Connection Timeout", ex);
		} catch (IOException ex) {
			response = Response.create(-1);
			if(ex.getMessage() != null) response.message(ex.getMessage());
			Logger.e(TAG, "IO error", ex);
		} finally {
			if(conn != null) conn.disconnect();
			try {
				if(in != null) in.close();
			} catch (IOException ex) {
				Logger.e(TAG, "Closing connection stream error", ex);
			}
		}

		return response;
	}

	private Response post(Request request) {
		URL url = HttpUtil.makeURL(request.url());
		OutputStream out;
		HttpURLConnection conn = null;
		InputStream in = null;
		Response response;

		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept-Charset", request.charset());
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			for (Entry<String, String> e : request.getHeaders()) {
				conn.setRequestProperty(e.getKey(), e.getValue());
			}

			if(request.getFormParams().length > 0) {
				out = new BufferedOutputStream(conn.getOutputStream());
				out.write(request.getFormParams());
				out.close();
			}

			in = conn.getInputStream();
			response = Response.create(conn.getResponseCode())
							   .message(conn.getResponseMessage())
							   .headers(conn.getHeaderFields())
							   .body(readBytes(in))
							   .encoding(request.charset());
		} catch (SocketTimeoutException ex) {
			response = Response.create(-1).message("Connection Timeout");
			Logger.e(TAG, "Connection Timeout", ex);
		} catch (IOException ex) {
			response = Response.create(1);
			if(ex.getMessage() != null) response.message(ex.getMessage());
			Logger.e(TAG, "IO error", ex);
		} finally {
			if(conn != null) conn.disconnect();
			try {
				if(in != null) in.close();
			} catch (IOException ex) {
				Logger.e(TAG, "Closing connection stream error", ex);
			}
		}

		return response;
	}

	private Response postMultipart(Request request) {
		HttpURLConnection conn;
		URL url = HttpUtil.makeURL(request.url());
		Response response;

		// creates a unique boundary
		String boundary = "----HttpFormBoundarycegA5GizjVOANRnv";
		String twoHyphens = "--";
		String LINE_FEED = "\r\n";

		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			conn.setRequestProperty("User-Agent", USER_AGENT);
			for (Entry<String, String> e : request.getHeaders()) {
				conn.setRequestProperty(e.getKey(), e.getValue());
			}

			OutputStream outputStream = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

			// 1 write form fields
			for (Entry<String, String> e : request.getForm()) {
				writer.append(twoHyphens).append(boundary).append(LINE_FEED)
					  .append(String.format("Content-Disposition: form-data; name=\"%s\"", e.getKey())).append(LINE_FEED)
					  .append(LINE_FEED)
					  .append(e.getValue()).append(LINE_FEED)
					  .flush();
			}

			// 2 write files
			for (Entry<String, File> e : request.getFiles()) {
				String filename = e.getValue().getName();
				File file = e.getValue();
				String type = URLConnection.guessContentTypeFromName(filename);

				writer.append(twoHyphens).append(boundary).append(LINE_FEED)
					  .append(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", e.getKey(), filename))
					  .append(LINE_FEED)
					  .append(String.format("Content-Type: %s", type)).append(LINE_FEED)
					  .append(LINE_FEED)
					  .flush();

				FileInputStream inputStream = new FileInputStream(file);
				byte[] buffer = new byte[8 * 1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.flush();
				inputStream.close();

				writer.append(LINE_FEED);
				writer.flush();
			}

			// 3 finish
			writer.append(twoHyphens).append(boundary).append(twoHyphens).append(LINE_FEED);
			writer.flush();
			writer.close();

			// get result
			response = Response.create(conn.getResponseCode())
							   .headers(conn.getHeaderFields())
							   .message(conn.getResponseMessage())
							   .encoding(request.charset())
							   .body(readBytes(conn.getInputStream()));

		} catch (IOException ex) {
			response = Response.create(-1);
			if(ex.getMessage() != null) response.message(ex.getMessage());
			Logger.e(TAG, "Multipart post error", ex);
		}

		return response;
	}

	//========================= Private methods ===================================
	private byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024);
		BufferedInputStream bin = new BufferedInputStream(in);
		byte[] buffer = new byte[8 * 1024];

		int read;
		while ((read = bin.read(buffer)) != -1) {
			baos.write(buffer, 0, read);
		}

		return baos.toByteArray();
	}
}
