package com.nkyrim.itapp.ui.mail;

import android.content.Context;

import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.util.Logger;
import com.sun.mail.imap.IMAPStore;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;

class MailMoveLoader extends BaseLoader<Boolean> {
	private Email mail;
	private String toFolder;

	public MailMoveLoader(Context context, Email mail, String toFolder) {
		super(context);
		this.mail = mail;
		this.toFolder = toFolder;
	}

	@Override
	public Boolean loadInBackground() {
		String user = Settings.getString(Key.AETOS_USERNAME, null);
		String password = Settings.getString(Key.AETOS_PASSWORD, null);

		if(user == null) return false;

		try {
			Properties properties = new Properties();
			properties.put("mail.imaps.host", "aetos.it.teithe.gr");
			Session session = Session.getInstance(properties);
			IMAPStore store = (IMAPStore) session.getStore("imaps");
			store.connect(user, password);

			Folder folder = store.getFolder(mail.getFolder());
			folder.open(Folder.READ_WRITE);

			Message m = folder.getMessage(mail.getNum());

			if(m != null) {
				// if we delete a message from the trash, delete permanently
				if(toFolder.equals("Trash") && mail.getFolder().equals("Trash")) {
					m.setFlag(Flags.Flag.DELETED, true);
				} else {
					folder.copyMessages(new Message[]{m}, store.getFolder(toFolder));
					m.setFlag(Flags.Flag.DELETED, true);
				}
			}

			folder.close(true);
			store.close();
		} catch (Exception ex) {
			Logger.e(TAG, "Error moving mail", ex);
			return false;
		}

		return true;
	}
}