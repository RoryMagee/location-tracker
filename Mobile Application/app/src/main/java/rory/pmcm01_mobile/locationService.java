package rory.pmcm01_mobile;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;

/**
 * Created by Rory on 02/04/2018.
 */

public class locationService extends Service {

    private IALocationManager mLocationManager;
    private double longitude, latitude, altitude;
    private String token;
    private String id;
    private Runnable mRunnable;
    private Handler handler;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        locationService getService() {
            return locationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d("SERVICE", "SERVICE CREATED");
        mLocationManager = IALocationManager.create(this);
        handler = new Handler();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                    postLocation();
                }
        };
        handler.postDelayed(mRunnable, 1000*15);

    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getAltitude() {
        return this.altitude;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SERVICE", "SERVICE DESTROYED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERVICE", "SERVICE STARTED");
        token = (String) intent.getExtras().get("token");
        id = (String) intent.getExtras().get("id");
        mLocationManager.requestLocationUpdates(IALocationRequest.create(), mIALocationListener);
        return Service.START_NOT_STICKY;
    }

    public void postLocation() {
        String url = "http://locationtracker.75tjcbkspv.eu-west-1.elasticbeanstalk.com/users/" + id;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url,
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
                params.put("longitude", Double.toString(longitude));
                params.put("latitude", Double.toString(latitude));
                params.put("altitude", Double.toString(altitude));
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
        handler.postDelayed(mRunnable, 1000*15);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IALocationListener mIALocationListener = new IALocationListener() {
        @Override
        public void onLocationChanged(IALocation location) {
            //Log.d("SERVICE", "LOCATION CHANGED");
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            altitude = location.getAltitude();

            //Log.d("LONGITUDE", Double.toString(longitude));
            //Log.d("LATITUDE", Double.toString(latitude));
            //Log.d("ALTITUDE", Double.toString(altitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //TextView console = (TextView) findViewById(R.id.status);
            //Log.d("SERVICE", "STATUS CHANGED");
            switch (status) {
                case IALocationManager.STATUS_CALIBRATION_CHANGED:
                    String quality = "unknown";
                    switch (extras.getInt("quality")) {
                        case IALocationManager.CALIBRATION_POOR:
                            quality = "Poor";
                            break;
                        case IALocationManager.CALIBRATION_GOOD:
                            quality = "Good";
                            break;
                        case IALocationManager.CALIBRATION_EXCELLENT:
                            quality = "Excellent";
                            break;
                    }
                    break;
                case IALocationManager.STATUS_AVAILABLE:
                    break;
                case IALocationManager.STATUS_LIMITED:
                    break;
                case IALocationManager.STATUS_OUT_OF_SERVICE:
                    break;
                case IALocationManager.STATUS_TEMPORARILY_UNAVAILABLE:
            }
        }
    };

    private IARegion.Listener mRegionListener = new IARegion.Listener() {
        IARegion mCurrentFloorPlan = null;

        @Override
        public void onExitRegion(IARegion region) {
            Log.d("SERVICE", "EXIT REGION");
        }

        @Override
        public void onEnterRegion(IARegion region) {
            Log.d("SERVICE", "ENTER REGION");
        }
    };
}
