package com.nkyrim.itapp.ui.thesis;

import android.content.Context;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

class ThesisListLoader extends BaseLoader<TaskResult<ArrayList<Thesis>>> {
	private int filter;

	public ThesisListLoader(Context context, int filter) {
		super(context);
		this.filter = filter;
	}

	@Override
	public TaskResult<ArrayList<Thesis>> loadInBackground() {
		TaskResult<String> r = ItNet.retrieveThesisList(filter);
		String body = r.getResult();

		ArrayList<Thesis> list;
		if(r.isSuccessful()) {
			Document doc = Jsoup.parse(body);
			Elements thesis = doc.select("[onmouseover]");

			list = new ArrayList<>(100);
			String shortTitle, teacher, detailID, temp, tags;
			boolean avail;

			for (Element e : thesis) {
				shortTitle = e.select("td.left").get(0).text();
				teacher = e.select("td.left").get(1).text();
				String x = e.select("td.center").get(3).text();
				avail = (!x.equals(""));
				// Up to 2 links, proposal doc and details. details is always available.
				temp = e.select("a").last().attr("href");
				detailID = temp.substring(temp.indexOf("["));
				//
				temp = e.attr("onmouseover");
				temp = Jsoup.parse(temp).toString();
				temp = temp.substring(temp.indexOf("Tags:") + 10, temp.indexOf("Σύνοψη"))
						   .replace("<br />", "").replace("<br>", "").replace("<b>", "").trim();
				if(temp.isEmpty()) tags = Util.getString(R.string.no_tags);
				else tags = temp;

				list.add(new Thesis(shortTitle, teacher, detailID, tags, avail));
			}

			result = new TaskResult<>(list);
		} else {
			result = new TaskResult<>(false, r.getMessage());
		}

		return result;
	}
}
