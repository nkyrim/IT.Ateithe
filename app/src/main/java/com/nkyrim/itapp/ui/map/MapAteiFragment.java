package com.nkyrim.itapp.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.Route;
import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.util.Util;

import org.greenrobot.eventbus.EventBus;

import pocketknife.PocketKnife;
import pocketknife.SaveState;

@SuppressWarnings({"MissingPermission", "deprecation"})
public class MapAteiFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Route> {

	private static final int NONE = 0;
	private static final int ALL = 1;
	private static final int DEPARTMENTS = 2;
	private static final int SECRETARIES = 3;
	private static final int OTHER = 4;
	private static final String ROUTE = "ROUTE_KEY";
	private static final int LID_ROUTE = 0;

	private GoogleMap map;
	@SaveState Route route;
	private int checkedItem = -1;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("markers", checkedItem);
	//	bundleParcelable("route", outState, route);
		PocketKnife.saveInstanceState(this, outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		PocketKnife.restoreInstanceState(this, savedInstanceState);
		// 	check and requests location permissions
		if(savedInstanceState == null) {
			if(!permissionsGrated()) {
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
			}
		}

		if(savedInstanceState != null) {
			checkedItem = savedInstanceState.getInt("markers");
		//  route = (Route) unbundleParcelable("route", savedInstanceState);
		}

		getMapAsync(googleMap -> {
			map = googleMap;
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.getUiSettings().setZoomControlsEnabled(true);
			map.getUiSettings().setMapToolbarEnabled(false);
			map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(40.656, 22.804), 17, 0, 0)));

			showMarkers(checkedItem);
			if(route != null) {
				PolylineOptions op = new PolylineOptions();
				op.addAll(route.getPoints()).width(3).color(Color.RED);
				map.addPolyline(op);
				EventBus.getDefault().post(new BusEvents.ShowDirectionsEvent(route));
			}

			map.setMyLocationEnabled(permissionsGrated());
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_map, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
			case R.id.action_places:
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.select_markers)
					   .setSingleChoiceItems(R.array.markers, checkedItem, (dialog, which) -> {
						   checkedItem = which;
						   showMarkers(which);
						   dialog.dismiss();
					   });
				builder.create().show();
				return true;
			case R.id.action_directions:
				getDirections();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void getDirections() {
		if(!permissionsGrated()) {
			Toast.makeText(getActivity(), R.string.no_location_permission, Toast.LENGTH_SHORT).show();
			return;
		}

		// clear the map of previous polylines
		map.clear();

		// show previously selected markers
		showMarkers(checkedItem);

		// check GPS
		LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.no_gps)
				   .setCancelable(false)
				   .setPositiveButton(R.string.yes, (dialog, id) -> {
					   startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				   }).setNegativeButton(R.string.no, null);
			builder.create().show();
			return;
		}

		// check location
		Location loc = map.getMyLocation();
		if(loc == null) {
			Util.showToast(getActivity(), R.string.no_location);
			return;
		}

		Bundle args = new Bundle();
		args.putParcelable("loc", loc);
		getLoaderManager().initLoader(LID_ROUTE, args, this);
	}

	private void showMarkers(int markers) {
		map.clear();

		switch (markers) {
			case NONE:
				// map already cleared
				break;
			case ALL:
				showDeps();
				showSecs();
				showOther();
				break;
			case DEPARTMENTS:
				showDeps();
				break;
			case SECRETARIES:
				showSecs();
				break;
			case OTHER:
				showOther();
				break;
		}
	}

	private void showDeps() {
		String[] array = getResources().getStringArray(R.array.department_markers);

		map.addMarker(new MarkerOptions().position(new LatLng(40.65741550741718, 22.80345610687995)).title(array[0]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65650688973563, 22.80405942793713)).title(array[1]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65751537705694, 22.80493534029657)).title(array[2]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65749081824789, 22.80583047303754)).title(array[3]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65752281481566, 22.80427511529138)).title(array[4]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65813708896448, 22.80366418805402)).title(array[5]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65675536755082, 22.80281579178317)).title(array[6]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65848537730349, 22.80363607676413)).title(array[7]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65813708896448, 22.80366418805402)).title(array[8]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65599103886893, 22.80230753636699)).title(array[9]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.6560696921044, 22.80312258813659)).title(array[10]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.6554985174275, 22.80583201993191)).title(array[11]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65598458075811, 22.80405511185979)).title(array[12]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65541226397288, 22.80318339784642)).title(array[13]));
		map.addMarker(new MarkerOptions().position(new LatLng(40.65776090013525, 22.80230288751251)).title(array[14]));
	}

	private void showOther() {
		String[] array = getResources().getStringArray(R.array.other);

		BitmapDescriptor bd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65885499081485, 22.80369697794211)).title(array[0]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65923063121845, 22.80359420984222)).title(array[1]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65558223168065, 22.80401688849685)).title(array[2]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.658102668773, 22.80418124350304)).title(array[3]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65798958337004, 22.80476786580689)).title(array[3]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65787050989015, 22.80279580883259)).title(array[4]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.6576442228868, 22.80279196134932)).title(array[5]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65797762762988, 22.80230653291655)).title(array[6]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65808364843059, 22.80284371625618)).title(array[7]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65748976719078, 22.80232378610307)).title(array[8]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65722779467273, 22.80231092281759)).title(array[9]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65690736922883, 22.80231184064693)).title(array[10]));
	}

	private void showSecs() {
		String[] array = getResources().getStringArray(R.array.secretaries);

		BitmapDescriptor bd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65689715678557, 22.80371756456915)).title(array[0]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65752350783784, 22.80459222802474)).title(array[1]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65832279628731, 22.8024158895779)).title(array[2]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65831237650392, 22.80264030340302)).title(array[3]));
		map.addMarker(new MarkerOptions().icon(bd).position(new LatLng(40.65657431364081, 22.80286974475234)).title(array[4]));
	}

	private static Parcelable unbundleParcelable(String key, Bundle src) {
		Bundle b = src.getBundle(key);
		if(b != null) {
			return b.getParcelable(ROUTE);
		}
		return null;
	}

	private static void bundleParcelable(String key, Bundle dest, Parcelable parcelable) {
		Bundle b = new Bundle();
		b.putParcelable(ROUTE, parcelable);
		dest.putBundle(key, b);
	}

	private boolean permissionsGrated() {
		return Util.permissionsGranted(getActivity(),
									   Manifest.permission.ACCESS_FINE_LOCATION,
									   Manifest.permission.ACCESS_COARSE_LOCATION);
	}

	@Override
	public void onRequestPermissionsResult(int reqcode, @NonNull String[] perms, @NonNull int[] results) {
		boolean result = results[0] == PackageManager.PERMISSION_GRANTED && results[1] == PackageManager.PERMISSION_GRANTED;

		map.setMyLocationEnabled(result);
	}

	@Override
	public Loader<Route> onCreateLoader(int id, Bundle args) {
		Location loc = args.getParcelable("loc");
		return new DirectionsLoader(getActivity(), loc);
	}

	@Override
	public void onLoadFinished(Loader<Route> loader, Route result) {
		getLoaderManager().destroyLoader(LID_ROUTE);

		if(result == null) {
			Util.showToast(getActivity(), getString(R.string.directions_error));
			return;
		}

		route = result;

		PolylineOptions op = new PolylineOptions();
		op.addAll(route.getPoints()).width(3).color(Color.RED);
		map.addPolyline(op);

		EventBus.getDefault().post(new BusEvents.ShowDirectionsEvent(route));
	}

	@Override
	public void onLoaderReset(Loader<Route> loader) {
		route = null;
	}
}

