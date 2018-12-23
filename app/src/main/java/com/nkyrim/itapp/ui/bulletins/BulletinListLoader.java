package com.nkyrim.itapp.ui.bulletins;

import android.content.Context;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.settings.Key;
import com.nkyrim.itapp.settings.Settings;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

class BulletinListLoader extends BaseLoader<TaskResult<ArrayList<Bulletin>>> {

	public BulletinListLoader(Context context) {
		super(context);
	}

	@Override
	public TaskResult<ArrayList<Bulletin>> loadInBackground() {
		TaskResult<String> r = ItNet.retrieveBulletinList();
		if(r.isSuccessful()) {
			long time = System.currentTimeMillis();
			Document doc = Jsoup.parse(r.getResult());
			Elements bulletins = doc.select("[onmouseover]");
			// allow for clean up
			doc = null;
			r = null;

			int i = 0;
			int max = Settings.getInt(Key.NOTIFICATIONS_MAX, 0);
			max = max == 0 ? 1000 : max;

			String temp, title, text, author, date, attachment, board;
			ArrayList<Bulletin> bulletinList = new ArrayList<>(max);

			for (Element e : bulletins) {
				author = e.select("td.left").get(1).text();
				date = e.select("td.center").first().text();
				board = e.select("td.left").first().text();
				// if there is an attachment
				if(e.select("a").size() == 3) attachment = e.select("a").get(1).attr("href").substring(3);
				else attachment = null;
				// Get title author from inside the onmouseover js function
				temp = e.attr("onmouseover");
				doc = Jsoup.parse(temp);
				title = doc.select("div.title").first().text().replace("\\", "");
				text = doc.select("div.text").first().text().replace("\\r\\r", "\n").replace("\\'", "'").replace("\\\"", "\"");
				bulletinList.add(new Bulletin(title, text, author, board, date, attachment));
				if(++i > max) break;
			}

			Logger.i(TAG, String.valueOf(System.currentTimeMillis() - time));
			result = new TaskResult<>(bulletinList);
		} else {
			result = new TaskResult<>(false, r.getMessage());
		}

		return result;
	}
}
