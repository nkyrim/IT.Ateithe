package com.nkyrim.itapp.ui.timetable;

import android.annotation.SuppressLint;
import android.content.Context;

import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.timetable.Timetable;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Cons;
import com.nkyrim.itapp.util.Logger;
import com.nkyrim.itapp.util.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class TimetableLoader extends BaseLoader<TaskResult<Timetable>> {

	public TimetableLoader(Context context) {
		super(context);
	}

	@SuppressLint("NewApi")
	@Override
	public TaskResult<Timetable> loadInBackground() {
		HttpURLConnection conn;

		URL url = Util.makeURL(Cons.URLS.TIMETABLE);
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException ex) {
			Logger.e(TAG, "Error connecting to timetable file", ex);
			return new TaskResult<>(false);
		}

		if(conn != null) {
			try (Scanner sc = new Scanner(conn.getInputStream())) {

				ArrayList<String> data = new ArrayList<>();

				while (sc.hasNextLine()) {
					data.add(sc.nextLine());
				}
				result = new TaskResult<>(Timetable.create(data));
			} catch (IOException ex) {
				Logger.e(TAG, "Error parsing timetable file", ex);
				result = new TaskResult<>(false, getContext().getString(R.string.error_reading_timetable));
			} finally {
				conn.disconnect();
			}
		}

		return result;
	}
}
