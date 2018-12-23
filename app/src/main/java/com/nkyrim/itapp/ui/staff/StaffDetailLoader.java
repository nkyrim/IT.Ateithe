package com.nkyrim.itapp.ui.staff;

import android.content.Context;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.ui.util.base.BaseLoader;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

class StaffDetailLoader extends BaseLoader<TaskResult<Staff>> {
	private String sid;

	public StaffDetailLoader(Context context, String staffid) {
		super(context);
		this.sid = staffid;
	}

	@Override
	public TaskResult<Staff> loadInBackground() {
		TaskResult<String> r = ItNet.retrieveStaff(sid);
		String body = r.getResult();
		String firstName, lastName, officeTel, email, webpage, role, classes;

		if(r.isSuccessful()) {
			Elements details = Jsoup.parse(body).select("tr.data td.value");

			if(details != null) {
				lastName = details.get(0).text();
				firstName = details.get(1).text();
				officeTel = details.get(5).text();
				email = details.get(6).text();
				webpage = details.get(7).text();
				role = details.get(2).text();
				classes = details.get(3).text().replace(") ", ")\n");
				Staff member = new Staff(sid, firstName, lastName, officeTel, email, webpage, role, classes);

				result = new TaskResult<>(member);
			} else {
				result = new TaskResult<>(false);
			}
		} else {
			result = new TaskResult<>(false);
		}

		return result;
	}
}