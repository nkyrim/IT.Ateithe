package com.nkyrim.itapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.webkit.MimeTypeMap;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Cons;
import com.nkyrim.itapp.util.Logger;
import com.nkyrim.itapp.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

import pocketknife.BindExtra;

public class DownloadHydraService extends IntentService {
	private static final String TAG = "DownloadHydraService";

	@BindExtra String urlString;

	private NotificationManager manager;

	public DownloadHydraService() {
		super("DownloadHydraService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		TaskResult r = ItNet.connect(Cons.ACCOUNT_OPTION_HYDRA);

		if(urlString == null || !r.isSuccessful()) return;

		// Create temporary notification while we retrieve the file name
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle(getString(R.string.download_start))
				.setContentText(getString(R.string.downloading))
				.setSmallIcon(android.R.drawable.stat_sys_download)
				.setOngoing(true)
				.setProgress(0, 0, true);

		// show it
		manager.notify(Cons.NOTI_ID_BULLETIN_DL, builder.build());

		// get the download file name
		HttpURLConnection conn;
		String filename = null;
		File download = null;

		try {
			conn = (HttpURLConnection) Util.makeURL(urlString).openConnection();
			filename = conn.getHeaderField("Content-Disposition");

			// convert on pre Lollipop devices
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				filename = new String(filename.getBytes("ISO-8859-1"), "UTF-8");
			}
			filename = filename.split("\"")[1];

			int size = conn.getContentLength();

			// get the download directory
			File dlDir = Util.getDownloadDir();
			if(dlDir != null) {
				download = Util.resolveFilename(dlDir, filename);
			} else {
				showError(filename);
				return;
			}

			// Show filename in notification
			builder.setContentTitle(filename);
			manager.notify(Cons.NOTI_ID_BULLETIN_DL, builder.build());

			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(download);
			byte[] buf = new byte[32 * 1024];
			int bytesRead;
			if(size > 0) {
				// we have the size of the file make notification determinate
				int read = 0;
				while ((bytesRead = is.read(buf)) != -1) {
					read += bytesRead;
					builder.setProgress(size, read, false);
					manager.notify(Cons.NOTI_ID_BULLETIN_DL, builder.build());
					fos.write(buf, 0, bytesRead);
				}
			} else {
				while ((bytesRead = is.read(buf)) != -1) {
					fos.write(buf, 0, bytesRead);
				}
			}

			fos.close();
			conn.disconnect();
		} catch (Exception ex) {
			Logger.e(TAG, "Download failed", ex);
			// ignore and Cleanup
			if(download != null) {
				//noinspection ResultOfMethodCallIgnored
				download.delete();
			}
			showError(filename);
			return;
		}

		// Notification onClick action
		String ext = filename.substring(filename.lastIndexOf(".") + 1);
		String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(download), mime);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

		// When the download is finished, update the notification
		Notification noti = builder.setContentTitle(download.getName())
								   .setContentText(getString(R.string.download_complete))
								   .setSmallIcon(R.drawable.ic_download)
								   .setProgress(0, 0, false)
								   .setContentIntent(pi)
								   .setOngoing(false)
								   .setAutoCancel(true)
								   .setColor(ContextCompat.getColor(this, R.color.light_cyan))
								   .setDefaults(Notification.DEFAULT_ALL)
								   .build();

		manager.notify(Cons.NOTI_ID_BULLETIN_DL, noti);
	}

	private void showError(String filename) {
		manager.cancel(Cons.NOTI_ID_BULLETIN_DL);
		Notification noti = new NotificationCompat.Builder(this)
				.setContentTitle(filename)
				.setContentText(getString(R.string.download_failed))
				.setSmallIcon(R.drawable.ic_warning)
				.setDefaults(Notification.DEFAULT_ALL).build();

		manager.notify(Cons.NOTI_ID_BULLETIN_DL, noti);
	}
}
