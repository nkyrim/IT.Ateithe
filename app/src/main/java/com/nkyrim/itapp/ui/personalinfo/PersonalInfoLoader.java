package com.nkyrim.itapp.ui.personalinfo;

import android.content.Context;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.student.PersonalInfo;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.ui.util.TaskResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

class PersonalInfoLoader extends BaseLoader<TaskResult<PersonalInfo>> {

	public PersonalInfoLoader(Context context) {
		super(context);
	}

	@Override
	public TaskResult<PersonalInfo> loadInBackground() {
		TaskResult<String> r = ItNet.retrievePersonalInfo();
		String body = r.getResult();

		PersonalInfo personalInfo;

		if(r.isSuccessful()) {
			Document doc = Jsoup.parse(body);
			Elements info = doc.select("div table tr[height=20] td");
			Elements reginfo = doc.select("span.tablecell");
			Elements address = doc.select("td>table>tbody>tr>td>table>tbody>tr>td");
			Elements contact = doc.select("td.tablecell");

			if(info.isEmpty() || reginfo.isEmpty()) {
				return new TaskResult<>(false, r.getMessage());
			} else {
				String name, lname, am, dep, sem, prog, rate, year, period, regsem, regmode;
				String address1, zip1, city1, country1, address2, zip2, city2, country2, phone1, phone2, email;

				// general
				// un = doc.select("td[width=250]").first().text();
				lname = info.get(1).text();
				name = info.get(3).text();
				am = info.get(5).text();
				dep = info.get(7).text();
				sem = info.get(9).text();
				prog = info.get(11).text();
				rate = reginfo.get(4).text();
				// registration
				year = reginfo.get(0).text();
				period = reginfo.get(1).text();
				regsem = reginfo.get(2).text();
				regmode = reginfo.get(3).text();
				// address permanent
				address1 = address.get(8).text();
				zip1 = address.get(10).text();
				city1 = address.get(12).text();
				country1 = address.get(14).text();
				// address current
				address2 = address.get(8).text();
				zip2 = address.get(10).text();
				city2 = address.get(12).text();
				country2 = address.get(14).text();
				// contact
				phone1 = contact.get(0).ownText().trim();
				phone2 = contact.get(1).ownText().trim();
				email = contact.get(2).ownText();

				personalInfo = new PersonalInfo(lname, name, am, dep, sem, prog,
												rate, year, period, regsem, regmode, address1,
												zip1, city1, country1, address2, zip2, city2,
												country2, phone1, phone2, email);
			}
			return new TaskResult<>(personalInfo);
		} else {
			return new TaskResult<>(false);
		}
	}
}