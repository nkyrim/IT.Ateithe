package com.nkyrim.itapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.util.Cons;
import com.nkyrim.itapp.util.Logger;
import com.nkyrim.itapp.util.Util;
import com.sun.mail.imap.IMAPStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeUtility;

import pocketknife.BindExtra;

/**
 * Service for the download of a specific email attachment
 */
public class DownloadMailAttachmentService extends IntentService {
	private static final String TAG = "DownloadMailAttachmentService";
	private NotificationManager manager;
	private boolean isRunning = false;

	@BindExtra int msgId;
	@BindExtra String filename;
	@BindExtra String folder;

	public DownloadMailAttachmentService() {
		super("DownloadMailAttachmentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// if already running return immediately. This in case of a "double-click" on download link
		if(isRunning) return;
		else isRunning = true;

		String user = Settings.getString(Key.AETOS_USERNAME, null);
		String password = Settings.getString(Key.AETOS_PASSWORD, null);

		if(user == null || msgId <= -1 || TextUtils.isEmpty(filename) || TextUtils.isEmpty(folder)) return;

		// Create temporary notification while we retrieve the file name
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle(filename)
				.setContentText(getString(R.string.downloading))
				.setSmallIcon(android.R.drawable.stat_sys_download)
				.setOngoing(true)
				.setProgress(1, 0, false);

		manager.notify(Cons.NOTI_ID_MAIL_DL, builder.build());

		IMAPStore store;
		Folder f;
		Multipart mp;
		BodyPart bp;
		File attachment = null;

		try {
			Properties properties = new Properties();
			properties.put("mail.imaps.host", "aetos.it.teithe.gr");
			Session session = Session.getInstance(properties);

			store = (IMAPStore) session.getStore("imaps");
			store.connect(user, password);

			f = store.getFolder(folder);
			f.open(Folder.READ_ONLY);

			Message m = f.getMessage(msgId);

			// Get attachment filenames
			if(m.isMimeType("multipart/*")) {
				mp = (Multipart) m.getContent();

				for (int i = 0; i < mp.getCount(); i++) {
					bp = mp.getBodyPart(i);

					if(bp.getFileName() == null || bp.getDisposition() == null) continue;

					String disposition = bp.getDisposition();
					String fn = MimeUtility.decodeText(bp.getFileName());

					if(Part.ATTACHMENT.equalsIgnoreCase(disposition) && filename.equalsIgnoreCase(fn)) {
						// get the download directory
						File dlDir = Util.getDownloadDir();
						if(dlDir != null) {
							attachment = Util.resolveFilename(dlDir, filename);
						} else {
							showError(filename);
							return;
						}

						InputStream is = bp.getInputStream();
						FileOutputStream fos = new FileOutputStream(attachment);
						byte[] buf = new byte[32 * 1024];
						int bytesRead;
						int size = bp.getSize() * 72 / 100; // vague size approximation
						int read = 0;
						while ((bytesRead = is.read(buf)) != -1) {
							read += bytesRead;
							builder.setProgress(size, read, false);
							manager.notify(Cons.NOTI_ID_MAIL_DL, builder.build());
							fos.write(buf, 0, bytesRead);
						}

						fos.close();
						break;
					}
				}
			}

			f.close(false);
			store.close();
		} catch (Exception ex) {
			Logger.e(TAG, "Download attachment failed", ex);
			// Cleanup
			if(attachment != null && attachment.exists()) {
				//noinspection ResultOfMethodCallIgnored
				attachment.delete();
				showError(attachment.getName());
			}

			return;
		}

		if(attachment == null) return;

		// Notification onClick action
		String ext = filename.substring(filename.lastIndexOf(".") + 1);
		String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(attachment), mime);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

		// When the loop is finished, update the notification
		Notification noti = builder
				.setContentTitle(attachment.getName())
				.setContentText(getString(R.string.download_complete))
				.setSmallIcon(R.drawable.ic_download)
				.setProgress(0, 0, false)
				.setContentIntent(pi)
				.setOngoing(false)
				.setAutoCancel(true)
				.setColor(ContextCompat.getColor(this, R.color.light_cyan))
				.setDefaults(Notification.DEFAULT_ALL).build();

		manager.notify(Cons.NOTI_ID_MAIL_DL, noti);
	}

	private void showError(String filename) {
		manager.cancel(Cons.NOTI_ID_MAIL_DL);
		Notification noti = new NotificationCompat.Builder(this)
				.setContentTitle(filename)
				.setContentText(getString(R.string.download_failed))
				.setSmallIcon(R.drawable.ic_warning)
				.setDefaults(Notification.DEFAULT_ALL).build();

		manager.notify(Cons.NOTI_ID_MAIL_DL, noti);
	}
}
