package com.nkyrim.itapp.ui.requests;

import android.content.Context;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.student.PithiaRequest;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.ui.util.TaskResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

class RequestLoader extends BaseLoader<ArrayList<PithiaRequest>> {

	public RequestLoader(Context context) {
		super(context);
	}

	@Override
	public ArrayList<PithiaRequest> loadInBackground() {
		TaskResult<String> r = ItNet.retrieveRequestList();
		String body = r.getResult();

		if(r.isSuccessful()) {
			Document doc = Jsoup.parse(body);
			result = new ArrayList<>();

			Elements reqs = doc.select("table#tablemain>tbody>tr>td>table>tbody>tr");
			String sn, date, type, link;
			for (Element e : reqs) {
				sn = e.select("td").get(0).text();
				date = e.select("td").get(1).text();
				type = e.select("td").get(2).text();
				link = e.select("a").first().attr("href");
				result.add(new PithiaRequest(sn, date, type, link));
			}
		} else {
			result = null;
		}

		return result;
	}
}
