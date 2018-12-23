package com.nkyrim.itapp.ui.mail;

import android.content.Context;

import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.util.Logger;
import com.sun.mail.imap.IMAPStore;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;

class MailDetailLoader extends BaseLoader<Email> {
	private Email mail;

	public MailDetailLoader(Context context, Email mail) {
		super(context);
		this.mail = mail;
	}

	@Override
	public Email loadInBackground() {
		String user = Settings.getString(Key.AETOS_USERNAME, null);
		String password = Settings.getString(Key.AETOS_PASSWORD, null);

		if (user == null) return null;

		try {
			Properties properties = new Properties();
			properties.put("mail.imaps.host", "aetos.it.teithe.gr");
			Session session = Session.getInstance(properties);
			IMAPStore store = (IMAPStore) session.getStore("imaps");
			store.connect(user, password);

			Folder folder = store.getFolder(mail.getFolder());
			folder.open(Folder.READ_WRITE);

			Message m = folder.getMessage(mail.getNum());
			if (m != null) {
				mail.setText(getText(m));
				mail.setHtml(getHtml(m));
				m.getContent(); // this will mark the message as read
			}

			folder.close(false);
			store.close();
		} catch (Exception ex) {
			Logger.e(TAG, "Error retrieving mail content", ex);
			return null;
		}

		mail.setIsComplete(true);
		return mail;
	}

	private String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/plain")) {
			return (String) p.getContent();
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null) return s;
			}
		}

		return null;
	}

	private String getHtml(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/html")) {
			return (String) p.getContent();
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getHtml(mp.getBodyPart(i));
				if (s != null) return s;
			}
		}

		return null;
	}
}