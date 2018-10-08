package rory.pmcm01_mobile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Rory on 17/04/2018.
 */

public class eventActivity extends AppCompatActivity {
    private static final String TAG = "Event Activity";
    ListView userListView;
    ArrayAdapter<User> userAdapter;
    ListView userAttendedView;
    ArrayAdapter<Boolean> attendedAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Log.d(TAG, "onCreate: started");
        getIncomingIntent();
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIntent: Checking for intent");
        /*if(getIntent().hasExtra("eventName") && getIntent().hasExtra("eventLocation") && getIntent().hasExtra("eventCreatedBy") && getIntent().hasExtra("eventCompleted")
                && getIntent().hasExtra("eventStartDate") && getIntent().hasExtra("eventEndDate") && getIntent().hasExtra("eventInvitedUsers")) {*/
                    String eventName = getIntent().getStringExtra("eventName");
                    String eventLocation = getIntent().getStringExtra("eventLocation");
                    String eventCreatedBy = getIntent().getStringExtra("eventCreatedBy");
                    Boolean eventCompleted = getIntent().getExtras().getBoolean("eventCompleted");
                    String startDate = getIntent().getStringExtra("eventStartDate");
                    String endDate = getIntent().getStringExtra("eventEndDate");
                    ArrayList<User> invitedUsers = getIntent().getParcelableArrayListExtra("invitedUsers");
                    ArrayList<Boolean> usersAttended = (ArrayList<Boolean>) getIntent().getSerializableExtra("usersAttended");
                    Log.d(TAG, eventName);
                    setUI(eventName, eventLocation, eventCreatedBy, eventCompleted, startDate, endDate, invitedUsers, usersAttended);
        //}

    }

    private void setUI(String eventName, String eventLocation, String eventCreatedBy, Boolean eventCompleted, String startDate, String endDate, ArrayList<User> invitedUsers, ArrayList<Boolean> usersAttended) {
        TextView eventNameLabel = (TextView) findViewById(R.id.event_name);
        TextView eventLocationLabel = (TextView) findViewById(R.id.eventLocation);
        TextView eventCreatedByLabel = (TextView) findViewById(R.id.createdBy);
        TextView startDateLabel = (TextView) findViewById(R.id.startDate);
        TextView endDateLabel = (TextView) findViewById(R.id.endDate);
        TextView eventCompletedLabel = (TextView) findViewById(R.id.completed);
        userListView = (ListView) findViewById(R.id.invitedUsers);
        userAdapter = new ArrayAdapter<User>(this, R.layout.event_user_list, invitedUsers);
        userListView.setAdapter(userAdapter);
        userListView.setItemsCanFocus(false);
        userAttendedView = (ListView) findViewById(R.id.userAttended);
        attendedAdapter = new ArrayAdapter<Boolean>(this, R.layout.event_user_list, usersAttended);
        userAttendedView.setAdapter(attendedAdapter);
        userAttendedView.setItemsCanFocus(false);

        eventNameLabel.setText(eventName);
        eventLocationLabel.setText(eventLocation);
        eventCreatedByLabel.setText(eventCreatedBy);
        eventCompletedLabel.setText(eventCompleted.toString());
        startDateLabel.setText(startDate);
        endDateLabel.setText(endDate);



    }
}
