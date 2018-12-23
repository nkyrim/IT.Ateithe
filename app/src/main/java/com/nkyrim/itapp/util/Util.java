package com.nkyrim.itapp.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.nkyrim.itapp.ItApp;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Util {
	public static String getString(int resId) {
		return ItApp.getAppContext().getString(resId);
	}

	public static String getString(int resId, Object... params) {
		return ItApp.getAppContext().getString(resId, params);
	}

	public static String[] getStringArray(int resId) {
		return ItApp.getAppContext().getResources().getStringArray(resId);
	}

	public static void showToast(Context context, int textid) {
		Toast.makeText(context, textid, Toast.LENGTH_LONG).show();
	}

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static URL makeURL(String urlString) {
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String readableSize(long size) {
		if(size <= 1024) return String.valueOf(size) + " B";
		else if(size <= 1024 * 1024) return String.valueOf(size / 1024) + " KB";
		else return String.valueOf((size / (1024 * 1024))) + " MB";
	}

	public static File getDownloadDir() {
		String dirStr = Settings.getString(Key.DOWNLOADS, null);
		if(dirStr != null) {
			File dirFile = new File(dirStr);
			if(dirFile.canWrite()) return dirFile;
		}

		return null;
	}

	public static File resolveFilename(File dlDir, String filename) {
		filename = filename.replaceAll("[\\\\/:\"*?<>|]", "_");
		String ext = filename.substring(filename.lastIndexOf("."));
		String name = filename.substring(0, filename.lastIndexOf("."));

		File file = new File(dlDir, filename);
		int i = 1;
		while (file.exists()) {
			file = new File(dlDir, name + "(" + i + ")" + ext);
			i++;
			System.out.println(i);
		}

		return file;
	}

	public static boolean permissionsGranted(Context context, String... permissions) {
		for (String s : permissions) {
			if(ActivityCompat.checkSelfPermission(context, s) != PackageManager.PERMISSION_GRANTED) return false;
		}

		return true;
	}
}
