package com.nkyrim.itapp.ui.staff;

import android.content.Context;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.Staff;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.ui.util.TaskResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

class StaffListLoader extends BaseLoader<TaskResult<ArrayList<Staff>>> {

	public StaffListLoader(Context context) {
		super(context);
	}

	@Override
	public TaskResult<ArrayList<Staff>> loadInBackground() {
		TaskResult<String> r = ItNet.retrieveStaffList();
		String body = r.getResult();
		ArrayList<Staff> list;
		if(r.isSuccessful()) {
			Document doc = Jsoup.parse(body);

			Elements staff = doc.select("table.vehi-list tr.data");

			list = new ArrayList<>(100);
			String firstName, lastName, detailID, temp;

			for (Element e : staff) {
				lastName = e.select("td.left").get(0).text();
				firstName = e.select("td.left").get(1).text();
				temp = e.select("a").attr("href");
				detailID = temp.substring(temp.lastIndexOf("=") + 1);
				list.add(new Staff(detailID, firstName, lastName));
			}

			result =new TaskResult<>(list);
		} else {
			result = new TaskResult<>(false, r.getMessage());
		}

		return result;
	}
}
