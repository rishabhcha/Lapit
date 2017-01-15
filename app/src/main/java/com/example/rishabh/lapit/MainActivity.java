package com.example.rishabh.lapit;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;

    private static final String TAG = "MainActivity";
    private Toolbar mainActivityToolbar;

    private DatabaseReference mRef;
    private DatabaseReference userFriendRef;

    private TextView mProfileName;
    private TextView mProfileEmail;
    // To hold Facebook profile picture
    private ImageView mProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .init();

        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .init();

        setContentView(R.layout.activity_main);

        mainActivityToolbar = (Toolbar) findViewById(R.id.MainActivityToolbar);
        setSupportActionBar(mainActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser()==null){
                    Intent loginIntent = new Intent(MainActivity.this, GetStartedActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }

            }
        };

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mRef.keepSynced(true);
        userFriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        userFriendRef.keepSynced(true);

        mProfileName = (TextView) findViewById(R.id.userDisplayName);
        mProfileEmail = (TextView) findViewById(R.id.userEmail);
        mProfilePicture = (ImageView) findViewById(R.id.userProfilePic);

        if (mAuth.getCurrentUser()!=null){

            mRef.child(Profile.getCurrentProfile().getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name = (String) dataSnapshot.child("name").getValue();
                    String email = (String) dataSnapshot.child("email").getValue();
                    String image = (String) dataSnapshot.child("image").getValue();

                    Picasso.with(MainActivity.this).load(Profile.getCurrentProfile().getProfilePictureUri(200,250)).into(mProfilePicture);
                    mProfileName.setText(name);
                    mProfileEmail.setText(email);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //Friend list part
            /*Intent intent = getIntent();
            String jsondata = intent.getStringExtra("jsondata");
            userFriendRef = FirebaseDatabase.getInstance().getReference().child("Users").child(intent.getStringExtra("uid")).child("friends");
            userFriendRef.keepSynced(true);

            JSONArray friendList;
            ArrayList<String> friends = new ArrayList<String>();
            try {

                Map<String,Object> map = new HashMap<>();
                friendList = new JSONArray(jsondata);
                for (int l=0;l< friendList.length();l++){
                    map.put(friendList.getJSONObject(l).getString("id"),friendList.getJSONObject(l).getString("name"));
                }
                userFriendRef.updateChildren(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            */

            //addFriendToDatabase(SignInActivity.jsondata);

            final ListView listView = (ListView) findViewById(R.id.listView);

            final FirebaseListAdapter<String> firebaseListAdapter = new FirebaseListAdapter<String>(
                    MainActivity.this,
                    String.class,
                    android.R.layout.simple_list_item_1,
                    mRef.child(Profile.getCurrentProfile().getId()).child("friends")) {
                @Override
                protected void populateView(android.view.View v, String model, final int position) {

                    TextView textView = (TextView)v.findViewById(android.R.id.text1);
                    textView.setText(model);

                    v.setOnClickListener(new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(android.view.View view) {
                            String key = getRef(position).getKey();
                            //Toast.makeText(MainActivity.this, key, Toast.LENGTH_LONG).show();
                            Intent showUserIntent = new Intent(MainActivity.this,ShowUserActivity.class);
                            showUserIntent.putExtra("user_key",key);
                            startActivity(showUserIntent);
                        }
                    });

                }
            };

            listView.setAdapter(firebaseListAdapter);
            //

            /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, android.view.View view, int i, long l) {

                    long nameee = adapterView.getItemIdAtPosition(i);
                    Toast.makeText(MainActivity.this, String.valueOf(nameee), Toast.LENGTH_LONG).show();
                    return false;
                }
            });*/

        }else {

            Intent loginIntent = new Intent(MainActivity.this, GetStartedActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginIntent);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListner);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_logout){
            logout();
        }
        if (item.getItemId()==R.id.action_chat){
            startActivity(new Intent(MainActivity.this,ChatActivity.class));
        }
        if (item.getItemId()==R.id.action_notification){
            startActivity(new Intent(MainActivity.this,NotificationActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    private void logout() {

        //mAuth.signOut();
        FirebaseAuth.getInstance().signOut();

    }

    private void addFriendToDatabase(String s) {

        JSONArray friendList;
        try {

            Map<String,Object> map = new HashMap<>();
            friendList = new JSONArray(s);
            for (int l=0;l< friendList.length();l++){
                map.put(friendList.getJSONObject(l).getString("id"),friendList.getJSONObject(l).getString("name"));
            }
            userFriendRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);

            // The following can be used to open an Activity of your choice.
            // Replace - getApplicationContext() - with any Android Context.
             Intent intent = new Intent(MainActivity.this, ChatActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(intent);


        }
    }

    class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            JSONObject data = notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }
        }
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}