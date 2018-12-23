package com.nkyrim.itapp.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.services.BulletinService;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.other.LoginActivity;
import com.nkyrim.itapp.ui.filepickers.DirectorySelectActivity;
import com.nkyrim.itapp.ui.courseselection.CourseSelectionActivity;
import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.ui.util.PocketKnifeIntents;
import com.nkyrim.itapp.util.Cons;
import com.nkyrim.itapp.util.Util;

import java.io.File;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if(EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		Preference hydra = findPreference(Key.HYDRA.name());
		Preference pithia = findPreference(Key.PITHIA.name());
		Preference aetos = findPreference(Key.AETOS.name());
		Preference notiMax = findPreference(Key.NOTIFICATIONS_MAX.name());
		Preference notiCheck = findPreference(Key.NOTIFICATIONS_ACTIVATION.name());
		Preference notiInterval = findPreference(Key.NOTIFICATIONS_INTERVAL.name());
		Preference downloads = findPreference(Key.DOWNLOADS.name());
		Preference courses = findPreference(Key.COURSES.name());

		hydra.setOnPreferenceClickListener(preference -> {
			Intent i = new PocketKnifeIntents(getActivity()).getLoginActivity(Cons.ACCOUNT_OPTION_HYDRA);
			startActivity(i);
			return true;
		});
		pithia.setOnPreferenceClickListener(preference -> {
			Intent i = new PocketKnifeIntents(getActivity()).getLoginActivity(Cons.ACCOUNT_OPTION_PITHIA);
			startActivity(i);
			return true;
		});
		aetos.setOnPreferenceClickListener(preference -> {
			Intent i = new PocketKnifeIntents(getActivity()).getLoginActivity(Cons.ACCOUNT_OPTION_AETOS);
			startActivity(i);
			return true;
		});

		notiMax.setOnPreferenceClickListener(preference -> {
			final NumberPicker picker = new NumberPicker(getActivity());
			picker.setMinValue(0);
			picker.setMaxValue(500);
			picker.setValue(Settings.getInt(Key.NOTIFICATIONS_MAX, 100));
			final FrameLayout parent = new FrameLayout(getActivity());
			parent.addView(picker, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT));

			new AlertDialog.Builder(getActivity())
					.setMessage(R.string.pref_notifications_max_dialogmsg)
					.setTitle(R.string.pref_notifications_max)
					.setView(parent)
					.setPositiveButton(R.string.ok, (dialog, which) -> {
						picker.clearFocus(); // needed to save value after keyboard input
						Settings.setInt(Key.NOTIFICATIONS_MAX, picker.getValue());
					})
					.create()
					.show();
			;

			return true;
		});

		notiCheck.setOnPreferenceChangeListener((preference, enabled) -> {
			Intent intent = new Intent(getActivity(), BulletinService.class);
			PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent, 0);
			AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

			if((Boolean) enabled) {
				long timeout = Settings.getLong(Key.NOTIFICATIONS_INTERVAL, 3600000);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeout, timeout, pintent);
				Settings.setBoolean(Key.NOTIFICATIONS_ACTIVATION, true);
				Settings.setLong(Key.NOTIFICATIONS_INTERVAL, timeout);
			} else {
				alarm.cancel(pintent);
				Settings.setBoolean(Key.NOTIFICATIONS_ACTIVATION, false);
			}

			return true;
		});
		notiInterval.setOnPreferenceChangeListener((preference, time) -> {
			Intent intent = new Intent(getActivity(), BulletinService.class);
			PendingIntent pintent = PendingIntent.getService(getActivity(), 0, intent, 0);

			AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
			long timeout = Long.parseLong((String) time);
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeout, timeout, pintent);
			Settings.setBoolean(Key.NOTIFICATIONS_ACTIVATION, true);
			Settings.setLong(Key.NOTIFICATIONS_INTERVAL, timeout);

			return true;
		});
		downloads.setOnPreferenceClickListener(preference -> {
			Intent i = new Intent(getActivity(), DirectorySelectActivity.class);
			startActivity(i);

			return true;
		});

		courses.setOnPreferenceClickListener(preference -> {
			Intent i = new Intent(getActivity(), CourseSelectionActivity.class);
			startActivity(i);

			return true;
		});
	}

	@SuppressWarnings("unused")
	@Subscribe
	public void onEventMainThread(BusEvents.FileSelectedEvent event) {
		if(event.uri != null) {
			File dir = new File(event.uri.getPath());
			if(dir.canWrite()) {
				Settings.setString(Key.DOWNLOADS, event.uri.getPath());
				Util.showToast(getActivity(), R.string.valid_directory);
			} else {
				Util.showToast(getActivity(), R.string.invalid_directory);
			}
		}
		EventBus.getDefault().removeStickyEvent(event);
	}

}
