package com.nkyrim.itapp;

import android.content.Intent;
import android.text.TextUtils;

import com.nkyrim.itapp.domain.mail.ForwardMail;
import com.nkyrim.itapp.domain.mail.SendMail;
import com.nkyrim.itapp.http.HttpClient;
import com.nkyrim.itapp.http.Request;
import com.nkyrim.itapp.http.Response;
import com.nkyrim.itapp.services.DownloadHydraService;
import com.nkyrim.itapp.services.DownloadPithiaService;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Cons;
import com.nkyrim.itapp.util.Logger;
import com.nkyrim.itapp.util.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * A facade for all the network related operations of the app(mainly using HttpClient)
 */
public final class ItNet {
	private static final String TAG = "ItNet";
	private final static HttpClient client = new HttpClient();

	private final static int SESSION_TIMEOUT = 10 * 60 * 1000;

	private ItNet() {
	}

	public static TaskResult<String> connect(int accountOption) {
		// Check if already logged in
		if(ItNet.isLoggedIn(accountOption)) {
			return new TaskResult<>(true);
		}

		// Not logged in, check for a stored account
		if(!isAccountStored(accountOption)) return new TaskResult<>(false, Util.getString(R.string.error_no_account));

		// Stored account found, login
		String username;
		String password;
		TaskResult<String> result;
		if(accountOption == Cons.ACCOUNT_OPTION_HYDRA) {
			username = Settings.getString(Key.HYDRA_USERNAME, null);
			password = Settings.getString(Key.HYDRA_PASSWORD, null);
			result = ItNet.loginHydra(username, password);
		} else if(accountOption == Cons.ACCOUNT_OPTION_PITHIA) {
			username = Settings.getString(Key.PITHIA_USERNAME, null);
			password = Settings.getString(Key.PITHIA_PASSWORD, null);
			result = ItNet.loginPithia(username, password);
		} else if(accountOption == Cons.ACCOUNT_OPTION_AETOS) {
			username = Settings.getString(Key.AETOS_USERNAME, null);
			password = Settings.getString(Key.AETOS_PASSWORD, null);
			result = ItNet.loginAetos(username, password);
		} else {
			throw new IllegalArgumentException("Account option: " + accountOption + " not supported");
		}

		return result;
	}

	public static TaskResult<String> retrieveBulletinList() {
		return retrieve(Cons.ACCOUNT_OPTION_HYDRA, Cons.URLS.HYDRA_BULLETINS);
	}

	public static TaskResult<String> retrieveGrades() {
		return retrieve(Cons.ACCOUNT_OPTION_PITHIA, Cons.URLS.PITHIA_GRADES);
	}

	public static TaskResult<String> retrievePersonalInfo() {
		return retrieve(Cons.ACCOUNT_OPTION_PITHIA, Cons.URLS.PITHIA_INFO);
	}

	public static TaskResult<String> retrieveRequestList() {
		return retrieve(Cons.ACCOUNT_OPTION_PITHIA, Cons.URLS.PITHIA_REQUESTS);
	}

	public static TaskResult<String> retrieveStaff(String memberDetailId) {
		return retrieve(Cons.ACCOUNT_OPTION_HYDRA, Cons.URLS.HYDRA_STAFF_DETAIL + memberDetailId);
	}

	public static TaskResult<String> retrieveStaffList() {
		return retrieve(Cons.ACCOUNT_OPTION_HYDRA, Cons.URLS.HYDRA_STAFF);
	}

	public static TaskResult<String> retrieveThesis(String thesisId) {
		return retrieve(Cons.ACCOUNT_OPTION_HYDRA, Cons.URLS.HYDRA_THESIS_DETAIL + thesisId);
	}

	public static TaskResult<String> retrieveThesisList(int filter) {
		return retrieve(Cons.ACCOUNT_OPTION_HYDRA, String.format(Cons.URLS.HYDRA_THESIS, filter));
	}

	public static TaskResult<String> retrieve(int accountOption, String url) {
		String enc;
		if(accountOption == Cons.ACCOUNT_OPTION_HYDRA) enc = Cons.UTF8;
		else if(accountOption == Cons.ACCOUNT_OPTION_PITHIA) enc = Cons.WINDOWS_1253;
		else enc = Cons.UTF8;

		// try to connect first
		TaskResult<String> connection = connect(accountOption);
		if(!connection.isSuccessful()) {
			return new TaskResult<>(false, connection.getMessage());
		}

		Request request = Request.get(url).charset(enc);
		Response response = client.send(request);

		// if the response is unsuccessful or code indicates problem set the result as unsuccessful
		if(!response.success()) new TaskResult<>(false, connection.getMessage());

		return new TaskResult<>(response.bodyString());
	}

	public static boolean isAccountStored(int accountOption) {
		switch (accountOption) {
			case Cons.ACCOUNT_OPTION_HYDRA:
				return (Settings.getString(Key.HYDRA_NAME, null) != null);
			case Cons.ACCOUNT_OPTION_PITHIA:
				return (Settings.getString(Key.PITHIA_NAME, null) != null);
			case Cons.ACCOUNT_OPTION_AETOS:
				return (Settings.getString(Key.AETOS_NAME, null) != null);
			default:
				throw new IllegalArgumentException("Account option: " + accountOption + " not supported");
		}
	}

	public static TaskResult<String> loginHydra(String username, String password) {
		// 1. Handshake - Get the session cookieStore
		Request request = Request.get(Cons.URLS.HYDRA_BASE_SECURE);
		Response response = client.send(request);

		//  if response unsuccessful or if HTTP error occurred, return result with message
		if(!response.success()) return new TaskResult<>(false, response.message());

		// 2. Post request - Login
		request = Request.post(Cons.URLS.HYDRA_LOGIN)
						 .form("am", username)
						 .form("pass", password)
						 .form("login", "Login");
		response = client.send(request);

		//  if response unsuccessful or if HTTP error occurred, return result with message
		if(!response.success()) return new TaskResult<>(false, response.message());

		Document doc = Jsoup.parse(response.bodyString(), Cons.URLS.HYDRA_BASE);
		Element error = doc.select("div.veh-msg-error").first();
		Element accName = doc.select("div.txt").last();
		response.body(null); // remove the response, we don't need it anymore

		if(error != null) {
			// error element exists, page presents an error
			return new TaskResult<>(false, error.text());
		} else if(accName == null) {
			// no error element, but no verified login
			return new TaskResult<>(false);
		} else {
			// successful login
			setLastHydra();
			return new TaskResult<>(accName.text());
		}
	}

	public static TaskResult<String> loginPithia(String username, String password) {
		// 1. Handshake - Get the session cookieStore
		Request request = Request.get(Cons.URLS.PITHIA_BASE);
		Response response = client.send(request);

		// if HTTP error occurred, return response with message
		if(!response.success()) return new TaskResult<>(false, response.message());

		// 2. Post request - Login
		request = Request.post(Cons.URLS.PITHIA_LOGIN)
						 .charset(Cons.WINDOWS_1253)
						 .form("userName", username)
						 .form("pwd", password)
						 .form("submit1", "Είσοδος")
						 .form("loginTrue", "login");
		response = client.send(request);

		if(!response.success()) return new TaskResult<>(false, response.message());

		Document doc = Jsoup.parse(response.bodyString(), Cons.URLS.PITHIA_BASE);
		Element accName = doc.select("span#header").first(); // Account name
		Element accUn = doc.select("td.subheader i").first(); // Account user name
		response.body(null); // remove the response, we don't need it anymore

		if(accName != null && accUn != null && accUn.text().contains(username)) {
			// Login Successful
			setLastPithia();
			return new TaskResult<>(accName.text());
		} else {
			// Get all script elements, one must contains the error message
			Elements scripts = doc.select("script");
			Element error = doc.select("table#show>tbody>tr").last();
			Elements er = doc.select("#main > div > table > tbody > tr > td.redfonts");

			// error in case of maintenance, possibly others
			String msg = null;
			if(er.size() == 1) {
				msg = er.first().text();
			} else if(scripts != null) {
				String s;
				for (Element e : scripts) {
					s = e.toString();
					if(s.contains("response.innerHTML") || s.contains("result.innerHTML")) {
						// found script element with error message
						msg = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
					}
				}
			} else if(error != null) {
				msg = error.text();
			}
			return new TaskResult<>(false, msg);
		}
	}

	public static TaskResult<String> loginAetos(String username, String password) {
		// 1 step - "handshake"
		Request request = Request.get(Cons.URLS.MAIL_BASE);
		Response response = client.send(request);

		//  if response unsuccessful or if HTTP error occurred, return result with message
		if(!response.success() || response.code() >= 400) return new TaskResult<>(false);

		// 2 step - login
		request = Request.post(Cons.URLS.MAIL_LOGIN)
						 .form("actionID", "")
						 .form("url", "")
						 .form("load_frameset", "1")
						 .form("autologin", "0")
						 .form("anchor_string", "")
						 .form("server_key", "imapssl_aetos")
						 .form("new_lang", "el_GR")
						 .form("imapuser", username)
						 .form("pass", password);
		response = client.send(request);

		if(!response.success() || response.code() >= 400) {
			return new TaskResult<>(false);
		} else if(response.success() && response.bodyString().contains("imp_login")) {
			// response successful but login failed, get error msg
			Document doc = Jsoup.parse(response.bodyString());
			Elements e = doc.select("#imp_login > ul > li");
			String msg = "Unknown error";
			if(!e.isEmpty()) msg = e.first().text();
			return new TaskResult<>(false, msg);
		}

		// 3 step - open "info" page to get name if exists
		request = Request.get(Cons.URLS.MAIL_INFO);
		response = client.send(request);

		if(response.success()) {
			setLastAetos();
			Document doc = Jsoup.parse(response.bodyString());
			String e = doc.select("#fullname").first().attr("value");
			String name = TextUtils.isEmpty(e) ? username : e;
			return new TaskResult<>(name);
		} else {
			return new TaskResult<>(false, response.message());
		}
	}

	public static void downloadHydra(String urlString) {
		Intent i = new PocketKnifeIntents(ItApp.getAppContext()).getDownloadHydraService(urlString);
		ItApp.getAppContext().startService(i);
	}

	public static void downloadPithia(String urlString) {
		Intent i = new PocketKnifeIntents(ItApp.getAppContext()).getDownloadPithiaService(urlString);
		ItApp.getAppContext().startService(i);
	}

	public static TaskResult<Void> sendMail(SendMail mail) {
		// connect
		TaskResult<String> r = connect(Cons.ACCOUNT_OPTION_AETOS);
		if(!r.isSuccessful()) return new TaskResult<>(false);

		// open "compose mail" form
		Request request = Request.get(Cons.URLS.MAIL_SEND);
		Response response = client.send(request);

		if(!response.success()) return new TaskResult<>(false, response.message());

		// get required info from the form
		String body = response.bodyString();
		String uniq = body.substring(body.indexOf("uniq=") + 5, body.indexOf("uniq=") + 17);
		Document doc = Jsoup.parse(body);
		String compose_requestToken = doc.getElementById("compose_requestToken").attr("value");
		String compose_formToken = doc.getElementById("compose_formToken").attr("value");
		String messageCache = doc.getElementById("messageCache").attr("value");

		request = Request.post(Cons.URLS.MAIL_SEND)
						 .multipart(true)
						 .query("uniq", uniq)
						 .form("MAX_FILE_SIZE", "2097152")
						 .form("actionID", "send_message")
						 .form("compose_requestToken", compose_requestToken)
						 .form("compose_formToken", compose_formToken)
						 .form("messageCache", messageCache)
						 .form("mailbox", "INBOX")
						 .form("thismailbox", "")
						 .form("page", "")
						 .form("start", "")
						 .form("popup", "")
						 .form("attachmentAction", "")
						 .form("reloaded", "1")
						 .form("rtemode", "")
						 .form("oldrtemode", "")
						 .form("index", "")
						 .form("charset", "UTF-8")
						 .form("save_sent_mail", "on")
						 .form("save_attachments_select", "0")
						 .form("to", mail.getTo())
						 .form("cc", mail.getCc())
						 .form("bcc", mail.getBcc())
						 .form("subject", mail.getTitle())
						 .form("message", mail.getMsg());
		for (int i = 0; i < mail.getAttachments().size(); i++) {
			request.form("upload_" + String.valueOf(i + 1), new File(mail.getAttachments().get(i)));
			request.form("upload_disposition_" + String.valueOf(i + 1), "attachment");
		}

		response = client.send(request);

		String msg = "";
		if(response.success()) {
			doc = Jsoup.parse(response.bodyString());
			if(doc.getElementsByTag("li") != null && doc.getElementsByTag("li").size() > 2) {
				Elements e = doc.getElementsByTag("li");
				if(e.size() > 2) msg = e.get(2).text();
			}
		}

		// No success message, probably failed
		if(!msg.contains("επιτυχώς")) return new TaskResult<>(false, msg);

		return new TaskResult<>(true);
	}

	public static TaskResult<Void> forwardMail(SendMail mail, ForwardMail fmail) {
		String formUrl = String.format(Cons.URLS.MAIL_FORWARD, fmail.getIndex(), fmail.getFolder());

		// 1 step connect
		TaskResult<String> r = connect(Cons.ACCOUNT_OPTION_AETOS);
		if(!r.isSuccessful()) return new TaskResult<>(false, r.getMessage());

		// 3 step - open "compose mail" form
		Request request = Request.get(formUrl);
		Response response = client.send(request);

		if(!response.success()) return new TaskResult<>(false, response.message());

		// get required info from the form
		String body = response.bodyString();
		String uniq = body.substring(body.indexOf("uniq=") + 5, body.indexOf("uniq=") + 17);
		Document doc = Jsoup.parse(body);
		String compose_requestToken = doc.getElementById("compose_requestToken").attr("value");
		String compose_formToken = doc.getElementById("compose_formToken").attr("value");
		String messageCache = doc.getElementById("messageCache").attr("value");
		String replyTo = doc.getElementById("in_reply_to").attr("value").replace("&lt;", "<").replace("&gt;", ">");

		request = Request.post(formUrl)
						 .multipart(true)
						 .query("uniq", uniq)
						 .form("MAX_FILE_SIZE", "2097152")
						 .form("actionID", "send_message")
						 .form("compose_requestToken", compose_requestToken)
						 .form("compose_formToken", compose_formToken)
						 .form("messageCache", messageCache)
						 .form("in_reply_to", replyTo)
						 .form("mailbox", "INBOX")
						 .form("thismailbox", fmail.getFolder())
						 .form("page", "")
						 .form("start", "")
						 .form("popup", "")
						 .form("attachmentAction", "")
						 .form("reloaded", "1")
						 .form("rtemode", "")
						 .form("oldrtemode", "")
						 .form("index", fmail.getIndex())
						 .form("charset", "UTF-8")
						 .form("save_sent_mail", "on")
						 .form("save_attachments_select", "0")
						 .form("to", mail.getTo())
						 .form("cc", mail.getCc())
						 .form("bcc", mail.getBcc())
						 .form("subject", mail.getTitle())
						 .form("message", mail.getMsg())
						 .form("index", mail.getTo())
						 .form("reply_type", "forward")
						 .form("message", mail.getMsg())
						 .form("reply_index", "")
						 .form("file_disposition_1", "inline")
						 .form("file_description_1", "");
		// start from 2 for attachments, 1 is the forwarded mail
		for (int i = 0; i < mail.getAttachments().size(); i++) {
			request.form("upload_" + String.valueOf(i + 1), new File(mail.getAttachments().get(i)));
			request.form("upload_disposition_" + String.valueOf(i + 1), "attachment");
		}

		response = client.send(request);

		String msg = "";
		if(response.success()) {
			doc = Jsoup.parse(response.bodyString());
			if(doc.getElementsByTag("li") != null && doc.getElementsByTag("li").size() > 2) {
				Elements e = doc.getElementsByTag("li");
				if(e.size() > 2) msg = e.get(2).text();
			}
		}

		// No success message, probably failed
		if(!msg.contains("επιτυχώς")) return new TaskResult<>(false, msg);

		return new TaskResult<>(true);
	}

	public static JSONObject getDirections(String urlString) {
		Request request = Request
				.get(urlString)
				.header("Content-type", "application/json");

		Response result = client.send(request);

		if(!result.success()) return null;

		String jsonString = result.bodyString();

		JSONObject json = null;
		try {
			json = new JSONObject(jsonString);
		} catch (JSONException ex) {
			Logger.e(TAG, "Error parsing JSON string", ex, jsonString);
			// do nothing
		}

		return json;
	}

	//================== Private ==================================
	private static boolean isLoggedIn(int accountOption) {
		long lastLogin;
		HttpCookie cookie;

		if(accountOption == Cons.ACCOUNT_OPTION_HYDRA) {
			lastLogin = Settings.getLong(Key.HYDRA_LAST_LOGIN, 0);
			cookie = getHydraCookie();
		} else if(accountOption == Cons.ACCOUNT_OPTION_PITHIA) {
			lastLogin = Settings.getLong(Key.PITHIA_LAST_LOGIN, 0);
			cookie = getPithiaCookie();
		} else if(accountOption == Cons.ACCOUNT_OPTION_AETOS) {
			lastLogin = Settings.getLong(Key.AETOS_LAST_LOGIN, 0);
			cookie = getAetosCookie();
		} else {
			throw new IllegalArgumentException("Account option " + accountOption + " not supported");
		}

		return (cookie != null && (System.currentTimeMillis() - lastLogin < SESSION_TIMEOUT));
	}

	private static HttpCookie getAetosCookie() {
		// TODO: find cookieStore
		return null;
	}

	private static HttpCookie getHydraCookie() {
		List<HttpCookie> list;
		HttpCookie cookie = null;

		list = client.cookieStore().get(URI.create(Cons.URLS.HYDRA_BASE));
		if(list != null) {
			for (HttpCookie c : list) {
				if(c.getName().equals("TALOSSESSIONID")) {
					cookie = c;
					break;
				}
			}
		}

		return cookie;
	}

	private static HttpCookie getPithiaCookie() {
		List<HttpCookie> list;
		HttpCookie cookie = null;

		list = client.cookieStore().get(URI.create(Cons.URLS.PITHIA_BASE));
		if(list != null) {
			for (HttpCookie c : list) {
				if(c.getName().startsWith("ASPSESSIONID")) {
					cookie = c;
					break;
				}
			}
		}

		return cookie;
	}

	private static void setLastAetos() {
		Settings.setLong(Key.AETOS_LAST_LOGIN, System.currentTimeMillis());
	}

	private static void setLastHydra() {
		Settings.setLong(Key.HYDRA_LAST_LOGIN, System.currentTimeMillis());
	}

	private static void setLastPithia() {
		Settings.setLong(Key.PITHIA_LAST_LOGIN, System.currentTimeMillis());
	}
}
