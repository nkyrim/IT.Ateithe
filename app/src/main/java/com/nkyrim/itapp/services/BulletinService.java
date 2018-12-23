package com.nkyrim.itapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Bulletin;
import com.nkyrim.itapp.persistance.BulletinDbHelper;
import com.nkyrim.itapp.ui.bulletins.BulletinActivity;
import com.nkyrim.itapp.ui.util.TaskResult;
import com.nkyrim.itapp.util.Cons;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for the notification for new bulletins
 */
public class BulletinService extends IntentService {

	public BulletinService() {
		super("BulletinService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		TaskResult<String> result = ItNet.retrieveBulletinList();

		if(!result.isSuccessful()) return;

		BulletinDbHelper db = new BulletinDbHelper(this);
		Bulletin latestBulletin = db.getLatest();
		if(latestBulletin == null) latestBulletin = new Bulletin("", "", "", "", "", "");

		int count = 0;
		List<String> titles = new ArrayList<>();

		// Get bulletin elements
		Document doc = Jsoup.parse(result.getResult());
		Elements bulletins = doc.select("[onmouseover]");

		String temp, title, text, author, date, attachment, board;
		Bulletin b;

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

			b = new Bulletin(title, text, author, board, date, attachment);

			if(latestBulletin.equals(b)) break;
			else {
				count++;
				if(count < 5) titles.add(title);
			}
		}

		// No new bulletins
		if(count == 0) return;

		// Notification onClick action
		Intent noti_intent = new Intent(this, BulletinActivity.class);
		noti_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pintent = PendingIntent.getActivity(this, 0, noti_intent, 0);

		// Create notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle(getString(R.string.app_name))
				.setContentText(getString(R.string.noti_new_bulletins, String.valueOf(count)))
				.setSmallIcon(R.drawable.ic_stat_news)
				.setColor(ContextCompat.getColor(this, R.color.light_cyan))
				.setContentIntent(pintent)
				.setDefaults(Notification.DEFAULT_ALL);

		// Create Big View
		NotificationCompat.InboxStyle bigView = new NotificationCompat.InboxStyle()
				.setBigContentTitle(getString(R.string.app_name))
			   .setSummaryText(getString(R.string.noti_new_bulletins, String.valueOf(count)));

		for (String s : titles) {
			bigView.addLine(s);
		}

		// Add the Big View
		builder.setStyle(bigView).setAutoCancel(true);

		// Build the notification
		Notification noti = builder.build();

		// Get the NotificationManager
		NotificationManager notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Send it
		notiManager.notify(Cons.NOTI_ID_BULLETINS, noti);
	}
}
