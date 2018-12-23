package com.nkyrim.itapp.ui.mail;

import android.content.Context;

import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.util.Logger;
import com.sun.mail.imap.IMAPStore;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Session;

class MailFolderLoader extends BaseLoader<ArrayList<String>> {

	public MailFolderLoader(Context context) {
		super(context);
	}

	@Override
	public ArrayList<String> loadInBackground() {
		String user = Settings.getString(Key.AETOS_USERNAME, null);
		String password = Settings.getString(Key.AETOS_PASSWORD, null);

		IMAPStore store;

		try {
			Properties properties = new Properties();
			properties.put("mail.imaps.host", "aetos.it.teithe.gr");
			Session session = Session.getInstance(properties);

			store = (IMAPStore) session.getStore("imaps");
			store.connect(user, password);

			Folder[] folders = store.getDefaultFolder().list();
			result = new ArrayList<>(folders.length);
			for (Folder folder : folders) {
				result.add(folder.getName());
			}

			store.close();
		} catch (Exception ex) {
			Logger.e(TAG, "Error retrieving mail folders", ex);
			// do nothing
		}

		return result;
	}
}
