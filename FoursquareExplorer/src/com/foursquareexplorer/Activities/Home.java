package com.foursquareexplorer.Activities;

import java.util.ArrayList;
import APIs.CheckInAPI;
import APIs.VenuesAPI;
import Handlers.DatabaseHandler;
import Helpers.InternetMethods;
import Listeners.OnCheckCompleted;
import Listeners.OnTaskCompleted;
import Services.GPSTracker;
import VenueObjects.Venue;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.foursquareexplorer.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Home extends FragmentActivity implements OnTaskCompleted, LocationListener, OnCheckCompleted 
{
	VenuesAPI callVenues;
	CheckInAPI callCheckIn;
	DatabaseHandler dbHandler = new DatabaseHandler(this);
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions optionsRectangle = new DisplayImageOptions.Builder()
    .showImageForEmptyUri(R.drawable.ic_launcher)
    .showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
    .cacheOnDisc(true).considerExifParams(true).build();
	
	ArrayList<Venue> allVenuesList;
	private GoogleMap mMap;
	Location myLocation;
	ArrayList<Marker> markers = new ArrayList<Marker>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        setUpMapIfNeeded();
        
        showVenuesOnMap();
    }

	@Override
	public void onTaskCompleted() 
	{
		try
		{
			allVenuesList = callVenues.get();
			
			showVenues();
			
			dbHandler.deleteAll();
			dbHandler.addVenueList(allVenuesList);
		}
		catch(Exception e)
		{
			Log.d("Exception", e.getMessage());
		}
	}
	
	private void setUpMapIfNeeded() 
    {
        if (mMap != null) 
        {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap == null) 
        {
            return;
        }
        // Initialize map options. For example:
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

	@Override
	public void onLocationChanged(Location arg0) 
	{
		myLocation = arg0;
		String lat = String.valueOf(myLocation.getLatitude());
		String lng = String.valueOf(myLocation.getLongitude());
		
		callVenues = new VenuesAPI(this, new ProgressDialog(this));
        callVenues.execute(lat,lng);
	}

	@Override
	public void onProviderDisabled(String arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) 
	{
		// TODO Auto-generated method stub
		
	}
	
	public void setInfowindow()
	{
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() 
		{
			
			@Override
			public View getInfoWindow(Marker arg0) 
			{
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public View getInfoContents(Marker arg0) 
			{				
				LayoutInflater inf=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				ViewGroup gr=(ViewGroup) findViewById(R.id.infoWindow);
				View v=inf.inflate(R.layout.info, gr, false);
				
				TextView nameOfVenue = (TextView) v.findViewById(R.id.textView_info);
				int index=getMarkerIndex(arg0);
				nameOfVenue.setText(allVenuesList.get(index).getName());
				
				return v;
				
			}
		});
		
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker arg0) 
			{
				int index=getMarkerIndex(arg0);
				
				callCheckIn = new CheckInAPI(Home.this, new ProgressDialog(Home.this));
				callCheckIn.execute(allVenuesList.get(index).getID());
				
			}
		});
	}
	
	public int getMarkerIndex(Marker arg0)
	{
		int index=0;
		for(int counter=0;counter<markers.size();counter++)
		{
			if(arg0.equals(markers.get(counter)))
			{
				index = counter;
				break;
			}
		}
		return index;
	}

	@Override
	public void onCheckCompleted() 
	{
		try
		{
			String retBack = callCheckIn.get();
			if(retBack.equals("200"))
			{
				Toast.makeText(this, "CheckIn completed", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(this, "Try again later", Toast.LENGTH_LONG).show();
			}
		}
		catch(Exception e)
		{
			Log.e("Exception", e.getMessage());
		}
		catch(Error e)
		{
			Log.e("Exception", e.getMessage());
		}
	}
	
	public void showVenuesOnMap()
	{
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean checkInternet = InternetMethods.checkInternetConnection(this);
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if(checkInternet && isGPSEnabled && isNetworkEnabled)
		{
			GPSTracker gps = new GPSTracker(this);
	        double latitude = gps.getLatitude();
	        double longitude = gps.getLongitude();
	        
	        Log.d("Latitude", String.valueOf(latitude));
	        Log.d("Long", String.valueOf(longitude));
	        
	        callVenues = new VenuesAPI(this, new ProgressDialog(this));
	        callVenues.execute(String.valueOf(latitude),String.valueOf(longitude));
		}
		else
		{
			Toast.makeText(this,"Check your Internet and GPS connections" , Toast.LENGTH_LONG).show();
			showCachedVenues();
		}
	}
	
	public void showCachedVenues()
	{
		allVenuesList = dbHandler.getAllVenues();
		showVenues();
	}
	
	public void showVenues()
	{
		markers = new ArrayList<Marker>();
		
		for(int counter1=0 ; counter1<allVenuesList.size() ; counter1++ )
		{
			try
			{
				Venue oneVenue = allVenuesList.get(counter1);
				Log.i("Venue : "+String.valueOf(counter1), oneVenue.getLocation().getLat()+" , "+oneVenue.getLocation().getLng());
				LatLng location = new LatLng(Double.parseDouble(oneVenue.getLocation().getLat()),Double.parseDouble(oneVenue.getLocation().getLng()));
				MarkerOptions mop=new MarkerOptions();
				mop.position(location);
				mop.title(oneVenue.getName());
				mop.snippet(oneVenue.getLocation().getAddress());
				Log.i("PhotoPath", "bos 3aleh");
				Log.i("PhotoPath", oneVenue.getPhoto().getPhotoURL());
				Marker MakMarker=mMap.addMarker(mop);
				markers.add(MakMarker);
				try
				{
					new ShowVenuesClass(oneVenue, MakMarker).execute();
				}
				catch(Exception e)
				{
					Log.e("Exception", e.getMessage());
					MakMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
				}
				
			}
			catch(Exception e)
			{
				Log.e("Exception", e.getMessage());
				continue;
			}
			catch (Error e) 
			{
				Log.e("Exception", e.getMessage());
				continue;
			}
		}
		
		
		
		if(allVenuesList.size()>0)
		{
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(allVenuesList.get(0).getLocation().getLat()), Double.parseDouble(allVenuesList.get(0).getLocation().getLng())),9), null);
		}
		setInfowindow();
	}
	
	public class ShowVenuesClass extends AsyncTask<Void, Void, Bitmap>
	{

		Venue selectedVenue;
		Marker selectedMarker;
		public ShowVenuesClass(Venue oneVenue , Marker oneMarker)
		{
			selectedVenue = oneVenue;
			selectedMarker = oneMarker;
		}
		
		@Override
		protected Bitmap doInBackground(Void... params) 
		{
			Bitmap bmp = imageLoader.loadImageSync(selectedVenue.getPhoto().getPhotoURL(),optionsRectangle);
			
			return bmp;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) 
		{
			selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(result));
			super.onPostExecute(result);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add("Refresh");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if(item.toString().equals("Refresh"))
		{
			setUpMapIfNeeded();
	        
	        showVenuesOnMap();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onBackPressed() 
    {
		try
		{
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
		}
		catch (Exception e)
	    {
			Log.d("Exception", e.getMessage());
	    }
		catch (Error e)
		{
			Log.d("Exception", e.getMessage());
		}
    }
	
}
