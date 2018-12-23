package com.nkyrim.itapp.ui.thesis;

import android.content.Context;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.Thesis;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.ui.util.TaskResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

class ThesisDetailLoader extends BaseLoader<TaskResult<Thesis>> {
	private String tid;

	public ThesisDetailLoader(Context context, String id) {
		super(context);
		this.tid = id;
	}

	@Override
	public TaskResult<Thesis> loadInBackground() {
		TaskResult<Thesis> result = null;
		TaskResult<String> r = ItNet.retrieveThesis(tid);
		String body = r.getResult();

		if(r.isSuccessful()) {
			Document doc = Jsoup.parse(body);

			Elements details = doc.select("tr.data td.value");

			if(details != null) {
				String temp;
				String title, submitted, teacher, tags, summary, docID;
				boolean completed, avail;

				title = details.get(0).text();
				submitted = details.get(1).text();
				teacher = details.get(2).text();
				tags = details.get(3).text();
				completed = (details.get(4) != null && details.get(4).text().equals("Ναι"));
				avail = (details.get(5) != null && details.get(5).text().equals("Ναι"));
				summary = details.get(6).text();
				if(details.select("a").first() != null) {
					temp = details.select("a").first().attr("href");
					temp = temp.substring(temp.lastIndexOf("=") + 1);
					docID = temp;
				} else {
					docID = "";
				}

			} else {

			}
		} else {

		}

		return result;
	}
}
