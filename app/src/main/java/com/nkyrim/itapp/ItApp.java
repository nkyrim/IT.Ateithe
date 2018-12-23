package com.nkyrim.itapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.nkyrim.itapp.settings.Settings;

/**
 * Application class to allow easy usage of Application Context in general purpose classes, and possibly store global objects.
 * <b>Necessary for MultiDex on pre-Lollipop devices</b>
 */
public class ItApp extends Application {
	private static Context context;

	public static Context getAppContext() {
		return context;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();

		Settings.setupFirstRun();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
