package com.nkyrim.itapp.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.nkyrim.itapp.ItApp;
import com.nkyrim.itapp.domain.courseselection.CourseSelection;
import com.nkyrim.itapp.domain.timetable.Timetable;
import com.nkyrim.itapp.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Controller for the application settings stored in SharedPreferences
 */
public final class Settings {
	private static final String TAG = "Settings";

	private static SharedPreferences getPrefs() {
		return ItApp.getAppContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE);
	}

	public static boolean getBoolean(Key key, boolean defValue) {
		return getPrefs().getBoolean(key.name(), defValue);
	}

	public static int getInt(Key key, int defValue) {
		return getPrefs().getInt(key.name(), defValue);
	}

	public static long getLong(Key key, long defValue) {
		return getPrefs().getLong(key.name(), defValue);
	}

	public static String getString(Key key, String defValue) {
		return getPrefs().getString(key.name(), defValue);
	}

	public static void setBoolean(Key key, boolean value) {
		getPrefs().edit().putBoolean(key.name(), value).commit();
	}

	public static void setInt(Key key, int value) {
		getPrefs().edit().putInt(key.name(), value).commit();
	}

	public static void setLong(Key key, long value) {
		getPrefs().edit().putLong(key.name(), value).commit();
	}

	public static void setString(Key key, String value) {
		getPrefs().edit().putString(key.name(), value).commit();
	}

	@NonNull
	public static CourseSelection getSelectedCourses() {
		CourseSelection cs = new CourseSelection();
		File file = new File(ItApp.getAppContext().getFilesDir(), Key.COURSES.name());
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			cs = (CourseSelection) in.readObject();
		} catch (ClassNotFoundException ex) {
			// problem with saved file, corrupted or old version
			Logger.e(TAG, "Problem with saved CourseSelection file, corrupted or old version", ex);
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		} catch (IOException ex) {
			Logger.e(TAG, "IO error", ex);
			// probably file not found exception,  do nothing
		}

		return cs;
	}

	public static Timetable getTimetable() {
		Timetable tt = null;
		File file = new File(ItApp.getAppContext().getFilesDir(), Key.TIMETABLE.name());
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			tt = (Timetable) in.readObject();
		} catch (ClassNotFoundException ex) {
			// problem with saved file, corrupted or old version
			Logger.e(TAG, "Problem with saved Timetable file, corrupted or old version", ex);
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		} catch (IOException ex) {
			Logger.e(TAG, "IO error", ex);
			// do nothing
		}

		return tt;
	}

	public static boolean storeSelectedCourses(CourseSelection sel) {
		File file = new File(ItApp.getAppContext().getFilesDir(), Key.COURSES.name());
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(sel);
			out.close();
			return true;
		} catch (IOException ex) {
			Logger.e(TAG, "Error saving courses", ex);
			// do nothing
		}

		return false;
	}

	public static boolean storeTimetable(Timetable timetable) {
		File file = new File(ItApp.getAppContext().getFilesDir(), Key.TIMETABLE.name());
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(timetable);
			out.close();
			return true;
		} catch (IOException ex) {
			Logger.e(TAG, "Error saving courses", ex);
			// do nothing
		}

		return false;
	}

	public static void setupFirstRun() {
		if(getBoolean(Key.IS_FIRST_RUN, true)) {
			File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			setString(Key.DOWNLOADS, dir.getAbsolutePath());
			setBoolean(Key.IS_FIRST_RUN, false);
		}
	}
}
