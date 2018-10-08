package rory.pmcm01_mobile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class locationActivity extends AppCompatActivity {
    locationService mService;
    String token, id;
    boolean mBound = false;
    int numOfPoints = 0;
    Double latitude, longitude, altitude, steadyLatitude, steadyLongitude, steadyAltitude;
    ArrayList<Double> locationPoints = new ArrayList<Double>();
    ArrayList<Double> longitudeArray = new ArrayList<Double>();
    ArrayList<Double> latitudeArray = new ArrayList<Double>();
    EditText et_locationName;
    ListView longitudeList;
    ArrayAdapter<Double> longitudeAdapter;
    ListView latitudeList;
    ArrayAdapter<Double> latitudeAdapter;
    ArrayAdapter<Double> locationPointsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        token = getIntent().getStringExtra("token");
        id = getIntent().getStringExtra("id");
        Button btn_addPoint = (Button) findViewById(R.id.addPointButton);
        final Button btn_createLocation = (Button) findViewById(R.id.createLocation);
        et_locationName = (EditText) findViewById(R.id.locationName);
        longitudeList = (ListView) findViewById(R.id.longitudeList);
        longitudeAdapter = new ArrayAdapter<Double>(this, R.layout.location_list_view, longitudeArray);
        longitudeList.setAdapter(longitudeAdapter);
        longitudeList.setItemsCanFocus(false);
        latitudeList = (ListView) findViewById(R.id.latitudeList);
        latitudeAdapter = new ArrayAdapter<Double>(this, R.layout.location_list_view, latitudeArray);
        latitudeList.setAdapter(latitudeAdapter);
        locationPointsAdapter = new ArrayAdapter<Double>(this, R.layout.location_point_list, locationPoints);
        btn_createLocation.setClickable(false);

        btn_createLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://locationtracker.75tjcbkspv.eu-west-1.elasticbeanstalk.com/locations";
                String locationName = et_locationName.getText().toString();
                Toast.makeText(locationActivity.this, locationName, Toast.LENGTH_SHORT).show();
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                Log.d("RESPONSE", response.toString());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("RESPONSE", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("name", et_locationName.getText().toString());
                        params.put("P1Lat", Double.toString(locationPoints.get(0)));
                        params.put("P1Long", Double.toString(locationPoints.get(1)));
                        params.put("P2Lat", Double.toString(locationPoints.get(2)));
                        params.put("P2Long", Double.toString(locationPoints.get(3)));
                        params.put("P3Lat", Double.toString(locationPoints.get(4)));
                        params.put("P3Long", Double.toString(locationPoints.get(5)));
                        params.put("P4Lat", Double.toString(locationPoints.get(6)));
                        params.put("P4Long", Double.toString(locationPoints.get(7 )));
                        Log.d("PARAMS", params.toString());
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String,String>();
                        headers.put("Authorization", "Bearer " + token);
                        //headers.put("Content-Type", "application/json");
                        Log.d("header", "headers added");
                        return headers;
                    }


                };

                // Add the request to the RequestQueue.
                requestQueue.add(stringRequest);
                Intent intent = new Intent(locationActivity.this, MainActivity.class);
                intent.putExtra("token", token);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        btn_addPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numOfPoints < 4) {
                    numOfPoints++;
                    assignCoordinates();
                } else {
                    Toast.makeText(locationActivity.this, "Too many points added", Toast.LENGTH_SHORT).show();
                    Log.d("POINTS", locationPoints.toString());
                    btn_createLocation.getBackground().setAlpha(255);
                    btn_createLocation.setClickable(true);
                }
            }
        });
    }

    public void assignCoordinates() {
        latitude = mService.getLatitude();
        longitude = mService.getLongitude();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                assignTestCoordinates();
            }
        }, 2000);
    }

    public void assignTestCoordinates() {
        steadyLatitude = mService.getLatitude();
        steadyLongitude = mService.getLongitude();
        Log.d("LONGITUDE", longitude.toString());
        Log.d("STEADY_LONGITUDE", steadyLongitude.toString());
        Log.d("LATITUDE", latitude.toString());
        Log.d("STEADY_LATITUDE", steadyLatitude.toString());
        if((latitude.equals(steadyLatitude)) && (longitude.equals(steadyLongitude))) {
            addPoint(latitude, longitude);
        } else {
            Toast.makeText(locationActivity.this, "Waiting for accurate reading", Toast.LENGTH_SHORT).show();
            assignCoordinates();
        }
    }

    public void addPoint(Double latitude, Double longitude) {
        locationPoints.add(latitude);
        locationPoints.add(longitude);
        longitudeArray.add(longitude);
        latitudeArray.add(latitude);
        latitudeAdapter.notifyDataSetChanged();
        longitudeAdapter.notifyDataSetChanged();
        Toast.makeText(locationActivity.this, "POINT ADDED", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, locationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService.LocalBinder binder = (locationService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };
}
