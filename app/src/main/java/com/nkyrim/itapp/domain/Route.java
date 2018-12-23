package com.nkyrim.itapp.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class Route implements Parcelable {
	private final String origin;
	private final String destination;
	private final String distance;
	private final String duration;
	private final ArrayList<String> directions; // in HTML!
	private final ArrayList<LatLng> points;

	private Route(String distance, String duration, String origin, String destination,
				  ArrayList<String> directions, ArrayList<LatLng> points) {
		if(distance == null) throw new IllegalArgumentException("distance cannot be null");
		if(duration == null) throw new IllegalArgumentException("duration cannot be null");
		if(origin == null) throw new IllegalArgumentException("origin cannot be null");
		if(destination == null) throw new IllegalArgumentException("destination cannot be null");
		if(directions == null || directions.isEmpty()) throw new IllegalArgumentException("directions cannot be null or empty");
		if(points == null || points.isEmpty()) throw new IllegalArgumentException("points cannot be null or empty");

		this.distance = distance;
		this.duration = duration;
		this.origin = origin;
		this.destination = destination;
		this.directions = directions;
		this.points = points;
	}

	protected Route(Parcel in) {
		origin = in.readString();
		destination = in.readString();
		distance = in.readString();
		duration = in.readString();
		directions = in.createStringArrayList();
		points = in.createTypedArrayList(LatLng.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(origin);
		dest.writeString(destination);
		dest.writeString(distance);
		dest.writeString(duration);
		dest.writeStringList(directions);
		dest.writeTypedList(points);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Route> CREATOR = new Creator<Route>() {
		@Override
		public Route createFromParcel(Parcel in) {
			return new Route(in);
		}

		@Override
		public Route[] newArray(int size) {
			return new Route[size];
		}
	};

	/**
	 * Creates a new Route object based on the JSON response from the Google Directions API web service
	 *
	 * @param gmJSON - The JSON response from Google Directions web service
	 * @return - The route or null if error occurred
	 */
	public static Route createRoute(JSONObject gmJSON) throws JSONException {
		String distance, duration, origin, destination;
		ArrayList<String> directions;
		ArrayList<LatLng> points;

		String status = gmJSON.getString("status");
		if(!status.equals("OK")) return null;
		// we get only one route and one leg since we don't use waypoints
		JSONObject route = gmJSON.getJSONArray("routes").getJSONObject(0);
		JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
		// get the steps
		duration = leg.getJSONObject("duration").getString("text");
		distance = leg.getJSONObject("distance").getString("text");
		origin = leg.getString("start_address");
		destination = leg.getString("end_address");

		JSONArray steps = leg.getJSONArray("steps");
		directions = new ArrayList<>(steps.length());
		for (int i = 0; i < steps.length(); i++) {
			String s = steps.getJSONObject(i).getString("html_instructions");
			directions.add(s);
		}

		JSONObject polyline = route.getJSONObject("overview_polyline");
		points = decodePoly(polyline.getString("points"));

		return new Route(distance, duration, origin, destination, directions, points);
	}

	public ArrayList<String> getDirections() {
		return directions;
	}

	public ArrayList<LatLng> getPoints() {
		return points;
	}

	/**
	 * Code from: http://wptrafficanalyzer.in/blog/route-between-two-locations-with-waypoints-in-google-map-android-api-v2/
	 *
	 * @param encoded - The encoded polyline points
	 * @return - A list of LatLng objects denoting the polyline points of the route
	 */
	private static ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng(((lat / 1E5)), ((lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	@Override
	public String toString() {
		return "Route{" +
				"origin='" + origin + '\'' +
				", destination='" + destination + '\'' +
				", distance='" + distance + '\'' +
				", duration='" + duration + '\'' +
				", directions=" + directions +
				", points=" + points +
				'}';
	}
}
