package com.recsysclient.maps;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptionsCreator;
import com.recsysclient.R;
import com.recsysclient.RecommendationListActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.recsysclient.entity.PoI;
import com.recsysclient.maps.MapsContextMonitor;
import com.recsysclient.maps.businesslogic.BusinessLogic;
import com.recsysclient.service.ContextMonitorService;

/**
 * This shows how to change the camera position for the map.
 */
public class MapsActivity extends android.support.v4.app.FragmentActivity {

	/**
	 * The amount by which to scroll the camera. Note that this amount is in raw pixels, not dp
	 * (density-independent pixels).
	 */
	private static final int SCROLL_BY_PX = 100;

	private CameraPosition currentPosition;

	private GoogleMap mMap;
	private Map<Long,Marker> markers;
	private BroadcastReceiver broadcastReceiver;
	private float bearing = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);
		markers = new HashMap<Long,Marker>(0);
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				//if cambio posizione
				Log.d("MapAct", "received");
				Bundle extras = intent.getExtras();
				if (intent.getAction().equals(BusinessLogic.POSITION_UPDATE)) {
					bearing=extras.getFloat("bearing");
					

				}
				else if(intent.getAction().equals(BusinessLogic.MARKERS_UPDATE)){
					List<PoI> toAddMarkers= (List<PoI>) extras.get("newPoIs");
					List<PoI> toRemoveMarkers= (List<PoI>) extras.get("oldPoIs");
					addMarkers(toAddMarkers);
					removeMarkers(toRemoveMarkers);
				}
				setUpMap();


			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(BusinessLogic.POSITION_UPDATE);
		filter.addAction(BusinessLogic.MARKERS_UPDATE);
		registerReceiver(broadcastReceiver, filter);
		Log.d("Maps", "create");
		setUpMapIfNeeded();
	}

	protected void removeMarkers(List<PoI> toRemoveMarkers) {
		for( PoI p : toRemoveMarkers ){
			markers.get(p.get_idEvento()).remove();
		}
	}

	protected void addMarkers(List<PoI> toAddMarkers) {

		for( PoI p : toAddMarkers ){
			BitmapDescriptor icon=BitmapDescriptorFactory.fromFile("res/drawable/"+p.getCategoria()+"_icon.png");
			if( icon==null)
				icon=BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
			
			Marker marker = mMap.addMarker(new MarkerOptions()
			.position(new LatLng( p.getLat(), p.getLng()))
			.title(p.get_nomeEvento())
			.snippet(p.get_descrizione())
			.icon(BitmapDescriptorFactory.fromFile("")));
			markers.put(p.get_idEvento(), marker);
		}


	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("Maps", "resume");

		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		Log.d("MapAct", "SetIfNeeded");
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		// We will provide our own zoom controls.
		Log.d("MapAct", "Set");

		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.moveCamera( CameraUpdateFactory.newCameraPosition(getCameraPosition()));
	}

	/**
	 * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
	 * all entry points that call methods on the Google Maps API.
	 */
	private boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * Called when a marker is clicked
	 */
	public void onLocationChanged(Location location) {
		//da implementare
		if (!checkReady()) {
			return;
		}

		changeCamera(CameraUpdateFactory.newCameraPosition(currentPosition));
	}

	/**
	 * Called when the Animate To Sydney button is clicked.
	 */
	/*public void onGoToSydney(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.newCameraPosition(SYDNEY), new CancelableCallback() {
            @Override
            public void onFinish() {
                Toast.makeText(getBaseContext(), "Animation to Sydney complete", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getBaseContext(), "Animation to Sydney canceled", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }*/

	/**
	 * Called when the stop button is clicked.
	 */
	public void onStopAnimation(View view) {
		if (!checkReady()) {
			return;
		}

		mMap.stopAnimation();
	}

	/**
	 * Called when the zoom in button (the one with the +) is clicked.
	 */
	public void onZoomIn(View view) {
		if (!checkReady()) {
			return;
		}

		changeCamera(CameraUpdateFactory.zoomIn());
	}

	/**
	 * Called when the zoom out button (the one with the -) is clicked.
	 */
	public void onZoomOut(View view) {
		if (!checkReady()) {
			return;
		}

		changeCamera(CameraUpdateFactory.zoomOut());
	}

	/**
	 * Called when the left arrow button is clicked.  This causes the camera to move to the left
	 */
	public void onScrollLeft(View view) {
		if (!checkReady()) {
			return;
		}

		changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0));
	}

	/**
	 * Called when the right arrow button is clicked.  This causes the camera to move to the right.
	 */
	public void onScrollRight(View view) {
		if (!checkReady()) {
			return;
		}

		changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0));
	}

	/**
	 * Called when the up arrow button is clicked.  The causes the camera to move up.
	 */
	public void onScrollUp(View view) {
		if (!checkReady()) {
			return;
		}

		changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX));
	}

	/**
	 * Called when the down arrow button is clicked.  This causes the camera to move down.
	 */
	public void onScrollDown(View view) {
		if (!checkReady()) {
			return;
		}

		changeCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX));
	}

	private void changeCamera(CameraUpdate update) {
		changeCamera(update, null);
	}

	/**
	 * Change the camera position by moving or animating the camera depending on the state of the
	 * animate toggle button.
	 */
	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		/*Da reimplementare
		 * 
		 * boolean animated = ((CompoundButton) findViewById(R.id.animate)).isChecked();
        if (animated) {
            mMap.animateCamera(update, callback);
        } else {
            mMap.moveCamera(update);
        }*/
	}

	public CameraPosition getCameraPosition(){
		LatLng coords= new LatLng(40.643136,17.303009);
		Log.d("MapAct", "Set bearing to "+bearing);

		return new CameraPosition.Builder().target(coords)
				.zoom(12f)
				.bearing(bearing)
				.tilt(89)
				.build();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);

	}
}
