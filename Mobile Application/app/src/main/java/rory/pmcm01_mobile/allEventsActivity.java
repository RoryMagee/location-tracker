package rory.pmcm01_mobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

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

public class allEventsActivity extends AppCompatActivity {

    String token, id;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Event> eventArr = new ArrayList<Event>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        id = getIntent().getStringExtra("id");
        token = getIntent().getStringExtra("token");
        Log.d("ID", id);

        getUserEvents(token, id);

        mRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new MyAdapter(eventArr, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerViewAdapter);




        /*Button btn = (Button) findViewById(R.id.testButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });*/
    }




    public void getUserEvents(String token, String id) {
        String url = "http://locationtracker.75tjcbkspv.eu-west-1.elasticbeanstalk.com/events/all/" + id;
        final String _token = token;
        final String _id = id;
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
                            recyclerViewAdapter.notifyDataSetChanged();
                            int eventArrSize = eventArr.size();
                            Log.d("Arraysize", Integer.toString(eventArrSize));
                            for(int y = 0; y < eventArrSize; y++) {
                                Log.d("Array", eventArr.get(y).getName().toString());
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
                headers.put("Authorization", "Bearer " +_token);
                headers.put("Content-Type", "application/json");
                Log.d("header", "headers added");
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }
}
