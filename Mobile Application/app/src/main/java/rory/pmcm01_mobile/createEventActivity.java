package rory.pmcm01_mobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateFormat;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class createEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener  {

    Context mContext;
    ListView userListView;
    ArrayAdapter<Location> locationAdapter;
    Spinner locationSpinner;
    ArrayAdapter<User> userAdapter;
    EditText eventName;
    NumberPicker hoursPicker;
    NumberPicker minutesPicker;
    Calendar startDateCalendar;
    Calendar endDateCalendar;
    Calendar cal;
    ArrayList<User> userList;
    ArrayList<Location> locationList;
    JSONArray invitedUsersJson = new JSONArray();

    int day, month, year, hour, minute;
    int durationHours, durationMinutes;

    TextView startDateLabel;
    TextView endDateLabel;
    Button startDateButton;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    String name, createdBy, startDateJson, endDateJson;
    Location location;
    Date startDate;
    Date endDate;
    ArrayList<User> invitedUsers;

    String token, id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        token = getIntent().getStringExtra("token");
        id = getIntent().getStringExtra("id");

        createdBy = getIntent().getStringExtra("id");

        startDateLabel = (TextView) findViewById(R.id.startDate_text);
        endDateLabel = (TextView) findViewById(R.id.endDate_text);
        startDateButton = (Button) findViewById(R.id.startDate_button);
        hoursPicker = (NumberPicker) findViewById(R.id.numberPicker_hours);
        minutesPicker = (NumberPicker) findViewById(R.id.numberPicker_minutes);

        hoursPicker.setMaxValue(23);
        hoursPicker.setMinValue(0);
        hoursPicker.setWrapSelectorWheel(false);

        minutesPicker.setMaxValue(59);
        minutesPicker.setMinValue(0);
        minutesPicker.setWrapSelectorWheel(false);

        invitedUsers = new ArrayList<User>();

        initializseUI();

        final Button createEventButton = (Button) findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Work out the end date
                if(eventName.equals("") || location == null || startDate == null || invitedUsers.size() < 1) {
                    return;
                }
                durationHours = hoursPicker.getValue();
                durationMinutes = minutesPicker.getValue();
                endDateCalendar = cal;
                endDateCalendar.add(Calendar.HOUR_OF_DAY, durationHours);
                endDateCalendar.add(Calendar.MINUTE, durationMinutes);
                endDate = endDateCalendar.getTime();
                endDateJson = sdf.format(endDate);

                name = eventName.getText().toString();

                invitedUsersJson = new JSONArray();
                for(int x = 0; x<invitedUsers.size(); x++) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("name", invitedUsers.get(x).getId());
                    } catch (JSONException e) {
                        Log.d("JSON EXCEPTION", e.toString());
                    }
                    invitedUsersJson.put(obj);
                }

                //location = (Location) locationSpinner.getSelectedItem();
                Log.d("LOCATION", location.toString());

                final String url = "http://locationtracker.75tjcbkspv.eu-west-1.elasticbeanstalk.com/events";

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                Map params = new HashMap();
                params.put("name", name);
                params.put("location", location.getId());
                params.put("startDate", startDateJson);
                params.put("endDate", endDateJson);
                params.put("createdBy", createdBy);
                params.put("invitedUsers", invitedUsersJson);

                JSONObject parameters = new JSONObject(params);
                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, parameters,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("RESPONSE", response.toString());
                                //Intent intent = new Intent(createEventActivity.this, MainActivity.class);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("token", token);
                                intent.putExtra("id", id);
                                createEventActivity.this.startActivity(intent);
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", error.toString());
                            }
                        }
                ) {
                    /*@Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("name", name);
                        params.put("location", location.getId());
                        params.put("startDate", startDateJson);
                        params.put("endDate", endDateJson);
                        params.put("createdBy", createdBy);
                        Log.d("PARAMS", params.toString());
                        return params;
                    }*/
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String,String>();
                        headers.put("Authorization", "Bearer " + token);
                        //headers.put("Content-Type", "application/json");
                        Log.d("header", "headers added");
                        return headers;
                    }
                };
                requestQueue.add(postRequest);
            }
        });




        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDateCalendar = Calendar.getInstance();

                DatePickerDialog startDateDialog = new DatePickerDialog(createEventActivity.this, createEventActivity.this, startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.DAY_OF_YEAR));
                startDateDialog.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        year = i;
        month = i1 + 1;
        day = i2;

        Calendar startTimeCalendar = Calendar.getInstance();
        TimePickerDialog startTimeDialog = new TimePickerDialog(this, this, startTimeCalendar.get(Calendar.HOUR_OF_DAY),
                startTimeCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        startTimeDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hour = i;
        minute = i1;

        //Date tempDate = new GregorianCalendar(year, month, day, hour, minute).getTime();
        cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        startDate = cal.getTime();
        startDateJson = sdf.format(startDate);
        startDateLabel.setText(startDate.toString());
    }

    public void initializseUI() {
        userList = getUserList();
        locationList = getLocationList();

        eventName = (EditText)findViewById(R.id.event_name);

        locationSpinner = (Spinner) findViewById(R.id.location_spinner);
        locationAdapter = new ArrayAdapter<Location>(this, android.R.layout.simple_spinner_item, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                location = (Location)adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        userListView = (ListView)findViewById(R.id.userList);
        userAdapter = new ArrayAdapter<User>(this, R.layout.invited_user_list, userList);
        userListView.setAdapter(userAdapter);
        userListView.setItemsCanFocus(false);
        userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) adapterView.getItemAtPosition(i);
                invitedUsers.add(user);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public ArrayList<Location> getLocationList() {
        String url = "http://locationtracker.75tjcbkspv.eu-west-1.elasticbeanstalk.com/locations";
        final ArrayList<Location> locationList = new ArrayList<Location>();
        final String _token = getIntent().getStringExtra("token");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("RESPONSE", "ON RESPONSE");
                        Log.d("JSON ARRAY", response.toString());
                        int x = 0;
                        try {

                            //JSONArray json = new JSONArray(response);
                            while (x < response.length()) {
                                //for(int x = 0; x < response.length(); x++) {
                                JSONObject jsonObject = response.getJSONObject(x);
                                Log.d("ARRAY", "ADDING OBJECT");

                                String id = jsonObject.getString("_id");
                                String name = jsonObject.getString("name");
                                locationList.add(new Location(id, name));
                                x++;
                            }
                            int locationArrSize = locationList.size();
                            locationAdapter.notifyDataSetChanged();
                            Log.d("Arraysize", Integer.toString(locationArrSize));
                        } catch (JSONException e) {
                            Log.e("PMCM01", "Json exception", e);
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LOCATION", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                Log.d("header", "headers added");
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(jsonArrayRequest);
        return locationList;
    }

    public ArrayList<User> getUserList() {
        String url = "http://locationtracker.75tjcbkspv.eu-west-1.elasticbeanstalk.com/users";
        final ArrayList<User> userList = new ArrayList<User>();
        final String _token = getIntent().getStringExtra("token");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("RESPONSE", "ON RESPONSE");
                        Log.d("JSON ARRAY", response.toString());
                        int x = 0;
                        try {

                            //JSONArray json = new JSONArray(response);
                            while (x < response.length()) {
                                //for(int x = 0; x < response.length(); x++) {
                                JSONObject jsonObject = response.getJSONObject(x);
                                Log.d("ARRAY", "ADDING OBJECT");

                                String firstName = jsonObject.getString("firstName");
                                String lastName = jsonObject.getString("lastName");
                                String id = jsonObject.getString("_id");
                                userList.add(new User(id, firstName, lastName));
                                x++;
                            }
                            int eventArrSize = userList.size();
                            Log.d("Arraysize", Integer.toString(eventArrSize));
                        } catch (JSONException e) {
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
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                Log.d("header", "headers added");
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        requestQueue.add(jsonArrayRequest);
        return userList;
    }



}
