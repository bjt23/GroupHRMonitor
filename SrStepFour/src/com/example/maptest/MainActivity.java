package com.example.maptest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Profile;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mainservercode.Pcontain;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	
	//bluetooth object instantiation
	private BluetoothSocket mmSocket;
	private BluetoothDevice mmDevice=null;
	private InputStream mmInputStream;
	private InputStreamReader mmReader;
	private BufferedReader mmBuffer;
	
	//UI element object instantiation
	private TextView textField;
	private TextView aprompt;
	private Button qbutton;
	private Button cbutton;
	private Button mviewbutton;
	private Button backbutton;
	private Button lviewbutton;
	private Spinner aspin;
	private ListView lview;
	private MapFragment mapView;
	private GoogleMap gmap;
	private ArrayAdapter<Integer> ageadapter;
	private ArrayAdapter<String> liadapt;
	
	//internet object instantiation
	private Socket webclient;
	private PrintWriter printwriter;
	private ObjectInputStream inlist;
	private String message;
	private long hrate;
	private double hrmax;
	private int age=13;
	private String uname;
	private String strsta;
	private boolean emergency=false;
	private Object inob;
	
	//group object instantiation
	private ArrayList<Pcontain> phonelist;
	private ArrayList<String> litems=new ArrayList<String>();
	private ArrayList<Marker> mitems=new ArrayList<Marker>();
	
	
	//GPS object instantiation
	private Location slocation;
	private double mylat;
	private double mylon;
	//GPS autozoom target
	private float gzoom=16;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Get Android owner name
		final String[] selfphone=new String[]{Phone.DISPLAY_NAME};
		Cursor cursor=this.getContentResolver().query(Profile.CONTENT_URI, selfphone, null, null, null);
		cursor.moveToFirst();
		uname=cursor.getString(0);
		System.out.println("Owner name: "+uname);
    	
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// UI references
		textField = (TextView) findViewById(R.id.textView1);
		aprompt = (TextView) findViewById(R.id.textView2);
		qbutton = (Button) findViewById(R.id.button1);
		cbutton = (Button) findViewById(R.id.button2);
		mviewbutton = (Button) findViewById(R.id.button3);
		backbutton = (Button) findViewById(R.id.button5);
		lviewbutton = (Button) findViewById(R.id.button4);
		lview = (ListView) findViewById(R.id.listv1);
		mapView = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		aspin = (Spinner) findViewById(R.id.spinner1);
		gmap = mapView.getMap();
		
		//Initialize main screen
		mapView.getView().setVisibility(View.INVISIBLE);
		backbutton.setVisibility(View.INVISIBLE);
		lview.setVisibility(View.INVISIBLE);
		gmap.setMyLocationEnabled(true);
		
		//Initialize age spinner
		Integer[] agearray=new Integer[87];
		//creates age array from 13-100
		for(int i=0;i<87;i++){
			agearray[i]=i+13;
		}
		ageadapter=new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, agearray);
		aspin.setAdapter(ageadapter);
		
		// map view button event listener
		mviewbutton.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View v) {
				//change visibility
				cbutton.setVisibility(View.INVISIBLE);
				qbutton.setVisibility(View.INVISIBLE);
				mviewbutton.setVisibility(View.INVISIBLE);
				lviewbutton.setVisibility(View.INVISIBLE);
				aprompt.setVisibility(View.INVISIBLE);
				aspin.setVisibility(View.INVISIBLE);
				mapView.getView().setVisibility(View.VISIBLE);
				backbutton.setVisibility(View.VISIBLE);
				
				//center map at location
				gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mylat, mylon), gzoom));
			}
		});
		
		// universal back button event listener
		backbutton.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View v) {
				mapView.getView().setVisibility(View.INVISIBLE);
				backbutton.setVisibility(View.INVISIBLE);
				lview.setVisibility(View.INVISIBLE);
				cbutton.setVisibility(View.VISIBLE);
				qbutton.setVisibility(View.VISIBLE);
				mviewbutton.setVisibility(View.VISIBLE);
				lviewbutton.setVisibility(View.VISIBLE);
				aprompt.setVisibility(View.VISIBLE);
				aspin.setVisibility(View.VISIBLE);
			}
		});
		
		// list view button event listener
		lviewbutton.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View v) {
				//change visibility
				cbutton.setVisibility(View.INVISIBLE);
				qbutton.setVisibility(View.INVISIBLE);
				mviewbutton.setVisibility(View.INVISIBLE);
				lviewbutton.setVisibility(View.INVISIBLE);
				aprompt.setVisibility(View.INVISIBLE);
				aspin.setVisibility(View.INVISIBLE);
				lview.setVisibility(View.VISIBLE);
				backbutton.setVisibility(View.VISIBLE);
			}
		});

		// quit button event listener
		qbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println("-------------APP ENDS--------------");
				finish();
				System.exit(0);
			}
		});
		
		// connect button event listener
		cbutton.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View v) {
				PiConnect pconn = new PiConnect();
				pconn.execute();
				cbutton.setEnabled(false);
				mviewbutton.setEnabled(true);
				lviewbutton.setEnabled(true);
			}
		});
		
		//age spinner listener
		aspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				age=(Integer)parent.getItemAtPosition(pos);
				System.out.println("age: "+age);
			}
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		//GPS Listener
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      mylat=location.getLatitude();
		      mylon=location.getLongitude();
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		};
		  
		//initialize UI elements
		textField.setText("No Data Yet");
		mviewbutton.setEnabled(false);
		lviewbutton.setEnabled(false);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		//bluetooth enabling stuff
		if(!mBluetoothAdapter.isEnabled())
		{
		   Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		   startActivityForResult(enableBluetooth, 0);
		}
		
		//checks for bluetooth device
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals("carlthepi")) //Note, you will need to change this to match the name of your device
                {
                	System.out.println("paired with pi");
                	Log.e("Raspberry Pi",device.getName());
                    mmDevice = device;
                    break;
                }
                else
                {
                	System.out.println("can't find the pi!");
                }
            }
        }
        
        //initialize GPS location
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        slocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        System.out.println("grabbing location");
        mylat=slocation.getLatitude();
        mylon=slocation.getLongitude();
        System.out.println("got location");
        
        //initialize list view
        liadapt=new ArrayAdapter<String>(MainActivity.this,R.layout.listlayout,litems);
        System.out.println("Setting adapter");
        lview.setAdapter(liadapt);
        litems.add("Not Connected to Main Server");
        liadapt.notifyDataSetChanged();
        
        //initialize map view
        mitems.add(gmap.addMarker(new MarkerOptions().position(new LatLng(mylat,mylon)).title("you")));
        
        System.out.println("app initialized");
	}
        
        
	
	//method for connection to pi
	public class PiConnect extends AsyncTask<Void, Void, Void> {
		 
		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Void... params) {


			while(true){
	        	
	        	try {
	        		//bluetooth connection
	            	//pi needs an insecure socket for some reason
	            	UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
	    			mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
	        		System.out.println("blue socket created");
	            	if (!mmSocket.isConnected()){
	            		mmSocket.connect();
	            		System.out.println("blue socket connected");
	            	}
	            	
	            	//get message
		        	mmInputStream=mmSocket.getInputStream();
		        	mmReader=new InputStreamReader(mmInputStream);
		        	mmBuffer=new BufferedReader(mmReader);
		        	//reads in heartrate and rounds
		        	hrate=Math.round(Double.parseDouble(mmBuffer.readLine()));

		        	//calculate status
		        	hrmax=(191.5-0.007*age*age);
		        	System.out.println("HRMax: "+hrmax);
		        	if(hrate>hrmax*1.1) {
		        	//if(true) {
		        		strsta="Probable Tachycardia!";
		        		if(!emergency){
		        			emergency=true;
		        			
		        			//Send message
		        			Intent message=new Intent(Intent.ACTION_SEND);
		        			message.setType("message/rfc822");
		        			message.putExtra(Intent.EXTRA_EMAIL, new String[]{"bjt23@pitt.edu"});
		        			message.putExtra(Intent.EXTRA_SUBJECT, "POSSIBLE TACHYCARDIA");
		        			message.putExtra(Intent.EXTRA_TEXT, "THIS IS AN AUTOMATED DISTRESS MESSAGE" +
		        					"\n\nTHE SUBJECT AT COORDINATES\nLATITUDE: "+mylat+"\nLONGITUDE: "+
		        					mylon+"\nMAY BE EXPERIENCING TACHYCARDIA!\n\nTHEIR CURRENT STATUS IS" +
		        					"\nAGE: "+age+"\nHEART RATE: "+hrate+"\n\nPLEASE RESPOND DIRECTLY TO " +
		        					"THE GIVEN COORDINATES AS APPROPRIATE\nDO NOT RESPOND TO THIS MESSAGE" +
		        					" - THIS MESSAGE IS AUTOMATED");
		        			try{
		        				startActivity(Intent.createChooser(message, "Send distress"));
		        				System.out.println("Distress Sent");
		        			}
		        			catch(android.content.ActivityNotFoundException me){
		        				me.printStackTrace();
		        			}
		        		}
		        	}
		        	else if(hrate>hrmax*0.8) {
		        		strsta="Anaerobic Activity";
		        	}
		        	else if(hrate>hrmax*0.6) {
		        		strsta="Aerobic Activity";
		        	}
		        	else if(hrate>40) {
		        		strsta="Resting";
		        	}
		        	else if(hrate>30) {
		        		strsta="Probable Bradycardia!";
		        		if(!emergency){
		        			emergency=true;
		        			
		        			//Send message
		        			Intent emess=new Intent(Intent.ACTION_SEND);
		        			emess.setType("message/rfc822");
		        			emess.putExtra(Intent.EXTRA_EMAIL, new String[]{"bjt23@pitt.edu"});
		        			emess.putExtra(Intent.EXTRA_SUBJECT, "POSSIBLE BRADYCARDIA");
		        			emess.putExtra(Intent.EXTRA_TEXT, "THIS IS AN AUTOMATED DISTRESS MESSAGE" +
		        					"\n\nTHE SUBJECT AT COORDINATES\nLATITUDE: "+mylat+"\nLONGITUDE: "+
		        					mylon+"\nMAY BE EXPERIENCING BRADYCARDIA!\n\nTHEIR CURRENT STATUS IS" +
		        					"\nAGE: "+age+"\nHEART RATE: "+hrate+"\n\nPLEASE RESPOND DIRECTLY TO " +
		        					"THE GIVEN COORDINATES AS APPROPRIATE\nDO NOT RESPOND TO THIS MESSAGE" +
		        					" - THIS MESSAGE IS AUTOMATED");
		        			try{
		        				startActivity(Intent.createChooser(emess, "Send distress"));
		        				System.out.println("Distress Sent");
		        			}
		        			catch(android.content.ActivityNotFoundException me){
		        				me.printStackTrace();
		        			}
		        		}
		        	}
		        	else{
		        		strsta="Bad Connection";
		        	}
		        	
		        	message="Heartrate: "+hrate+"\nStatus: "+strsta;
		        	System.out.println("message: "+message);
		        	
		        	//final to pass to UI thread
		        	final String sMess=message;
		        	
		        	//sends message to main java server
					webclient = new Socket("172.20.10.2", 4444); // connect to the server
					printwriter = new PrintWriter(webclient.getOutputStream(), true);
					printwriter.write(uname+"\n"+hrate+"\n"+strsta+"\n"+mylat+"\n"+mylon); // write the message to output stream
					System.out.println("message sent to java server");

					//close output connection and prepare input connection
					printwriter.flush();
					printwriter.close();
					webclient = new Socket("172.20.10.2", 4444);
					
					//gets phonelist from server
					inlist=new ObjectInputStream(webclient.getInputStream());
					try{
						inob=inlist.readObject();
						phonelist=(ArrayList<Pcontain>) inob;
						System.out.println("got list");
					}
					catch(ClassNotFoundException ce)
					{
						ce.printStackTrace();
					}
					
		        	//updates UI text
		        	runOnUiThread(new Runnable(){
		        		public void run(){
		        			//Personal heart rate + status display update
		        			textField.setText(sMess);
		        			System.out.println("updating list view");
		        			
		        			//Updates list view and map view
		        			for(int i=0;i<litems.size();i++){
								litems.set(i,"Name: "+phonelist.get(i).aname
										+"\nHeart Rate:"+phonelist.get(i).arate+
										"\nStatus: "+phonelist.get(i).astat);
								mitems.get(i).setPosition(new LatLng(phonelist.get(i).alat,phonelist.get(i).alon));
		        			}
							for(int i=litems.size();i<phonelist.size();i++){
								litems.add("Name: "+phonelist.get(i).aname
										+"\nHeart Rate:"+phonelist.get(i).arate+
										"\nStatus: "+phonelist.get(i).astat);
								mitems.add(gmap.addMarker(new MarkerOptions().position(
										new LatLng(phonelist.get(i).alat,phonelist.get(i).alon)).title(
												phonelist.get(i).aname)));
							}
							liadapt.notifyDataSetChanged();
		        		}
		        	});
					
		        	//close all connections
					inlist.close();
					webclient.close();
		        	mmSocket.close();
		        	
				} catch (IOException e) {
					System.out.println("ERROR: IOException.");
					e.printStackTrace();
				}
	        }
			//return null;
		}
	}
 
}
