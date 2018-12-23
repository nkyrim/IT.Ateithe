package com.nkyrim.itapp.util;

import android.util.Log;

import com.nkyrim.itapp.BuildConfig;

public class Logger {

	public static void d(String tag, String msg, Object... args) {
		if(BuildConfig.DEBUG) {
			Log.d(tag, "========================================================================");
			Log.d(tag, "TAG: " + tag);
			Log.d(tag, "------------------------------------------------------------------------");
			Log.d(tag, "MESSAGE: " + msg);
			if(args != null) {
				for (Object arg : args) {
					Log.d(tag, "------------------------------------------------------------------------");
					Log.d(tag, arg.toString());
				}
			}
			Log.d(tag, "========================================================================");
		}
	}

	public static void i(String tag, String msg, Object... args) {
		if(BuildConfig.DEBUG) {
			Log.i(tag, "========================================================================");
			Log.i(tag, "TAG: " + tag);
			Log.i(tag, "------------------------------------------------------------------------");
			Log.i(tag, "MESSAGE: " + msg);
			if(args != null) {
				for (Object arg : args) {
					Log.i(tag, "------------------------------------------------------------------------");
					Log.i(tag, arg.toString());
				}
			}
			Log.i(tag, "========================================================================");
		}
	}

	public static void e(String tag, String msg, Throwable exc, Object... args) {
		if(BuildConfig.DEBUG) {
			Log.e(tag, "========================================================================");
			Log.e(tag, "TAG: " + tag);
			Log.e(tag, "------------------------------------------------------------------------");
			Log.e(tag, "MESSAGE: " + msg, exc);
			if(args != null) {
				for (Object arg : args) {
					Log.e(tag, "------------------------------------------------------------------------");
					Log.e(tag, arg.toString());
				}
			}
			Log.e(tag, "========================================================================");
		}
	}
}
