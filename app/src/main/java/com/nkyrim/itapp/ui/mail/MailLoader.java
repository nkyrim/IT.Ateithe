package com.nkyrim.itapp.ui.mail;

import android.content.Context;

import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.util.Logger;
import com.sun.mail.imap.IMAPStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeUtility;

class MailLoader extends BaseLoader<ArrayList<Email>> {
	private String folder;

	public MailLoader(Context context, String folder) {
		super(context);
		this.folder = folder;
	}

	@Override
	public ArrayList<Email> loadInBackground() {
		String user = Settings.getString(Key.AETOS_USERNAME, null);
		String password = Settings.getString(Key.AETOS_PASSWORD, null);

		IMAPStore store = null;
		Folder inbox = null;
		Multipart multipart;
		BodyPart bp;
		ArrayList<String> at;
		result = new ArrayList<>();

		// properties for proper filename decoding
		System.setProperty("mail.mime.encodeparameters", "true");
		System.setProperty("mail.mime.decodeparameters", "true");
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);

		try {
			Properties properties = new Properties();
			properties.put("mail.imaps.host", "aetos.it.teithe.gr");
			Session session = Session.getInstance(properties);

			store = (IMAPStore) session.getStore("imaps");
			store.connect(user, password);

			inbox = store.getFolder(folder);
			inbox.open(Folder.READ_ONLY);

			// get all messages
			for (Message m : inbox.getMessages()) {
				at = new ArrayList<>();

				// Get attachment filenames
				if(m.isMimeType("multipart/*")) {
					multipart = (Multipart) m.getContent();
					String s;

					for (int i = 0; i < multipart.getCount(); i++) {
						bp = multipart.getBodyPart(i);
						if(Part.ATTACHMENT.equalsIgnoreCase(bp.getDisposition())) {
							s = bp.getFileName();
							if(s == null || s.isEmpty()) s = "unknown";
							else s = MimeUtility.decodeText(s);
							at.add(s);
						}
					}
				}

				// check if mail is read
				boolean read = m.isSet(Flags.Flag.SEEN);
				// check if mail is (marked)deleted
				boolean deleted = m.isSet(Flags.Flag.DELETED);

				result.add(new Email(folder, MimeUtility.decodeText(m.getFrom()[0].toString()), m.getSubject(),
									 m.getSentDate(), m.getMessageNumber(), at, read, deleted));
			}
		} catch (IOException | MessagingException ex) {
			Logger.e(TAG, "Error loading mail folders", ex);
		} finally {
			try {
				if(inbox != null && inbox.isOpen()) inbox.close(false);
				if(store != null&& store.isConnected()) store.close();
			} catch (MessagingException ex) {
				Logger.e(TAG, "Error mail inbox/store close", ex);
			}
		}

		Collections.sort(result);
		Collections.reverse(result);

		return result;
	}
}
