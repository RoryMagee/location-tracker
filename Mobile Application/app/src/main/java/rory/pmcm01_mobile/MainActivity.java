package rory.pmcm01_mobile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private final int CODE_PERMISSIONS = 3;
    String token, id;

    private ArrayList<Event> eventArr = new ArrayList<Event>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Permissions required for IndoorAtlas API
        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions( this, neededPermissions, CODE_PERMISSIONS );
        setContentView(R.layout.activity_main);

        //Retrieving the users id and auth token from the login activity
        id = getIntent().getStringExtra("id");
        token = getIntent().getStringExtra("token");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getUserEvents(token, id);
        mAdapter = new MyAdapter(eventArr, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Load the createEvent activity when the floating action button is clicked
                Intent createEventIntent = new Intent(MainActivity.this, createEventActivity.class);
                createEventIntent.putExtra("token", token);
                createEventIntent.putExtra("id", id);
                MainActivity.this.startActivity(createEventIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Creating the location service to run in the background
        Intent intent = new Intent(this, locationService.class);
        intent.putExtra("token", token);
        intent.putExtra("id", id);
        startService(intent);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    public void stopLocationService(View view) {
        Intent intent = new Intent(this, locationService.class);
        stopService(intent);
    }

    public void getUserEvents(String token, String id) {
        String url = "http://locationtracker.75tjcbkspv.eu-west-1.elasticbeanstalk.com/events/" + id;
        final String _token = token;
        final String __id = id;
        Log.d("URL", url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("RESPONSE", "ON RESPONSE");
                        Log.d("JSON ARRAY", response.toString());
                        int x = 0;
                        try {
                            SimpleDateFormat format = new SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US
                            );
                            //JSONArray json = new JSONArray(response);
                            while(x < response.length()) {
                            //for(int x = 0; x < response.length(); x++) {
                                JSONObject jsonObject = response.getJSONObject(x);
                                Log.d("ARRAY", "ADDING OBJECT");

                                String name = jsonObject.getString("name");
                                String locationName = jsonObject.getJSONObject("location").getString("name");
                                String locationId = jsonObject.getJSONObject("location").getString("_id");
                                Location eventLocation = new Location(locationId, locationName);
                                String createdBy = jsonObject.getJSONObject("createdBy").getString("firstName") + " " + jsonObject.getJSONObject("createdBy").getString("lastName");
                                //ONLY GET ATTENDED FOR CURRENT USER
                                JSONArray invitedUsers = jsonObject.getJSONArray("invitedUsers");
                                ArrayList<User> invitedUsersList = new ArrayList<User>();
                                ArrayList<Boolean> userAttendedList = new ArrayList<Boolean>();
                                for(int i = 0; i < invitedUsers.length();i++) {
                                    JSONObject userInfo = invitedUsers.getJSONObject(i);
                                    String IU_Id = userInfo.getJSONObject("name").getString("_id");
                                    String firstName = userInfo.getJSONObject("name").getString("firstName");
                                    String lastName = userInfo.getJSONObject("name").getString("lastName");
                                    User u = new User(IU_Id, firstName, lastName);
                                    Boolean attended = userInfo.getBoolean("attended");
                                    invitedUsersList.add(u);
                                    userAttendedList.add(attended);
                                }
                                Boolean completed = jsonObject.getBoolean("completed");
                                Date startDate;
                                Date endDate;
                                try {
                                    startDate = format.parse(jsonObject.getString("startDate"));
                                    endDate = format.parse(jsonObject.getString("endDate"));
                                } catch(ParseException pe) {
                                    throw new IllegalArgumentException(pe);
                                }
                                eventArr.add(new Event(name, eventLocation, createdBy, completed, startDate, endDate, invitedUsersList, userAttendedList));
                                x++;
                            }
                            mAdapter.notifyDataSetChanged();
                            int eventArrSize = eventArr.size();
                            Log.d("Arraysize", Integer.toString(eventArrSize));
                            for(int y = 0; y < eventArrSize; y++) {
                                eventArr.get(y).toString();
                            }
                        } catch(JSONException e) {
                            Log.e("PMCM01", "Json exception", e);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle error
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String,String>();
                headers.put("Authorization", "Bearer " + _token);
                headers.put("Content-Type", "application/json");
                Log.d("header", "headers added");
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Handle if any of the permissions are denied, in grantResults
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TextView console = (TextView) findViewById(R.id.status);
        //console.setText("RESUMED");
        //console.append("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TextView console = (TextView) findViewById(R.id.status);
        //console.setText("PAUSED");
    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int item_id = item.getItemId();

        if (item_id == R.id.all_events) {
            Intent intent = new Intent(this, allEventsActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("token", token);
            startActivity(intent);
        } else if (item_id == R.id.add_location) {
            Intent intent = new Intent(this, locationActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("id", id);
            MainActivity.this.startActivity(intent);
        } else if (item_id == R.id.nav_slideshow) {

        } else if (item_id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
