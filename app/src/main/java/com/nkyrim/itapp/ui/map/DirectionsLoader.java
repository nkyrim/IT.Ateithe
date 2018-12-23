package com.nkyrim.itapp.ui.map;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.nkyrim.itapp.ItNet;
import com.nkyrim.itapp.domain.Route;
import com.nkyrim.itapp.ui.util.base.BaseLoader;
import com.nkyrim.itapp.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

public class DirectionsLoader extends BaseLoader<Route> {
	private static final LatLng tei = new LatLng(40.657059, 22.801612);

	private Location loc;

	public DirectionsLoader(Context context, Location loc) {
		super(context);
		this.loc = loc;
	}

	@Override
	public Route loadInBackground() {
		String request = "https://maps.googleapis.com/maps/api/directions/json?origin=" + String
				.valueOf(loc.getLatitude()) + "," + String.valueOf(loc.getLongitude()) + "&destination=" + String
				.valueOf(tei.latitude) + "," + String.valueOf(tei.longitude) + "&sensor=true&language=el&units=metric";

		JSONObject response = ItNet.getDirections(request);

		Route route = null;
		try {
			route = Route.createRoute(response);
		} catch (JSONException ex) {
			Logger.e(TAG, "Error parsing JSON route", ex, response);
			// do nothing
		}

		return route;
	}
}
