package rory.pmcm01_mobile;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event implements Parcelable {

    Context mContext;
    String name, createdBy;
    Location location;
    Boolean completed;
    Date startDate, endDate;
    ArrayList<User> invitedUsers;
    ArrayList<Boolean> usersAttended;

    public Event(String name, Location location, String createdBy, Boolean completed, Date startDate, Date endDate, ArrayList<User> invitedUsers, ArrayList<Boolean> usersAttended) {
        this.name = name;
        this.location = location;
        this.createdBy = createdBy;
        this.completed = completed;
        this.startDate = startDate;
        this.endDate = endDate;
        if(invitedUsers.size() != 0) {
            this.invitedUsers = invitedUsers;
        } else {
            this.invitedUsers = null;
        }
        if(usersAttended.size() != 0) {
            this.usersAttended = usersAttended;
        } else {
            usersAttended = null;
        }
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(this.startDate);
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(this.endDate);
    }


    protected Event(Parcel in) {
        name = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        createdBy = in.readString();
        completed = in.readByte() != 0;
        startDate = new Date(in.readLong());
        endDate = new Date(in.readLong());
        invitedUsers = in.readArrayList(User.class.getClassLoader());
        usersAttended = in.readArrayList(Boolean.class.getClassLoader());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Boolean getCompleted() { return completed; }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ArrayList<User> getInvitedUsers() {
        return invitedUsers;
    }

    public void setName(String x) {
        this.name = x;
    }

    public void setLocation(Location x) {
        this.location = x;
    }

    public void setCreatedBy(String x) {
        this.createdBy = x;
    }

    public void setCompleted(Boolean x) {
        this.completed = x;
    }


    public void setStartDate(Date x) {
        this.startDate = x;
    }

    public void setEndDate(Date x) {
        this.endDate = x;
    }

    public void setInvitedUsers(ArrayList<User> x) {
        this.invitedUsers = x;
    }

    public void printEvent() {
        Log.d("EVENT", this.name);
        Log.d("EVENT", this.location.toString());
        Log.d("EVENT", this.startDate.toString());
        Log.d("EVENT", this.endDate.toString());
        Log.d("EVENT", this.completed.toString());
        Log.d("EVENT", this.invitedUsers.toString());
        Log.d("EVENT", this.usersAttended.toString());

    }

    /*public void pushEvent(String token) throws JSONException {
        final String url = "http://locationtracker.75tjcbkspv.eu-west-1.elasticbeanstalk.com/events";
        final String name = this.getName();
        final Location location = this.getLocation();
        final Date startDate = this.getStartDate();
        final Date endDate = this.getEndDate();
        final String createdBy = this.getCreatedBy();
        final String _token = token;
        final ArrayList<HashMap<User, Boolean>> invitedUsers = this.getInvitedUsers();
        final JSONArray invitedUsersJson = new JSONArray();
        for(int x = 0; x < invitedUsers.size(); x++) {
            JSONObject temp = new JSONObject();
            temp.put("name", invitedUsers.get(x));
            invitedUsersJson.put(temp);
        }
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            Log.d("PMCM01", response.toString());
                        } catch (JSONException e) {
                            Log.e("PMCM01", "JSON exception", e);
                        }


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
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("name", name);
                params.put("location", location.getId());
                params.put("startDate", startDate.toString());
                params.put("endDate", endDate.toString());
                params.put("createdBy", createdBy);
                params.put("invitedUsers", invitedUsersJson.toString());
                Log.d("PARAMS", params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String,String>();
                headers.put("Authorization", "Bearer " +_token);
                headers.put("Content-Type", "application/json");
                Log.d("header", "headers added");
                return headers;
            }
        };
        requestQueue.add(postRequest);
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeParcelable(location, i);
        parcel.writeString(createdBy);
        parcel.writeByte((byte) (completed ? 1 : 0));
        parcel.writeLong(startDate.getTime());
        parcel.writeLong(endDate.getTime());
        parcel.writeList(invitedUsers);
        parcel.writeList(usersAttended);
    }
}

