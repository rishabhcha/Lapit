package com.example.rishabh.lapit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private Toolbar mSignInToolbar;
    //    compile 'com.facebook.android:facebook-android-sdk:[4,5)'


    EditText mEmailField, mPasswordField;
    Button mSigninBtn;
    LoginButton loginButton;
    CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mRef;
    private DatabaseReference userFriendRef;

    String jsondata;

    private static final String TAG = "SignInActivity";

    ProgressDialog mProgress;

    Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mSignInToolbar = (Toolbar) findViewById(R.id.signInToolbar);
        setSupportActionBar(mSignInToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mRef.keepSynced(true);
        //userFriendRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("friends");

        mainIntent = new Intent(getApplicationContext(), MainActivity.class);


        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passowrdField);
        mSigninBtn = (Button) findViewById(R.id.signinBtn);
        mProgress = new ProgressDialog(this);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);



                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                try {
                                    JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                    Log.d("--Friends--: ",rawName.toString());
                                    Log.d("--Friends2--: ", String.valueOf(response.getJSONObject()));
                                    jsondata = rawName.toString();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                ).executeAsync();




                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        mProgress.setMessage("Logging in...");
        mProgress.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        }else{
                            //String uid=task.getResult().getUser().getUid();
                            String name=task.getResult().getUser().getDisplayName();
                            String email=task.getResult().getUser().getEmail();
                            String image=task.getResult().getUser().getPhotoUrl().toString();
                            //String id = task.getResult().getUser().getProviderId();
                            //Log.d("-----Task-------",task.getResult().toString());
                            Log.d("----------id----------", Profile.getCurrentProfile().getId());
                            Log.d("----------id----------", Profile.getCurrentProfile().getFirstName());
                            Log.d("----------id----------", Profile.getCurrentProfile().getName());
                            Log.d("----------id----------", String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(300,300)));
                            Log.d("----------id----------", String.valueOf(Profile.getCurrentProfile().getLinkUri()));

                            DatabaseReference childRef = mRef.child(Profile.getCurrentProfile().getId());
                            childRef.child("name").setValue(name);
                            childRef.child("email").setValue(email);
                            childRef.child("image").setValue(image);
                            childRef.child("id").setValue(Profile.getCurrentProfile().getId());

                            JSONArray friendList;
                            try {

                                Map<String,Object> map = new HashMap<>();
                                friendList = new JSONArray(jsondata);
                                for (int l=0;l< friendList.length();l++){
                                    map.put(friendList.getJSONObject(l).getString("id"),friendList.getJSONObject(l).getString("name"));
                                }
                                childRef.child("friends").updateChildren(map);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                            mProgress.dismiss();

                            /*new GraphRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    "/me/friends",
                                    null,
                                    HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        public void onCompleted(GraphResponse response) {

                                            try {
                                                JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                                Log.d("--Friends--: ",rawName.toString());
                                                mainIntent.putExtra("jsondata",rawName.toString());

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                            ).executeAsync();
                            */


                            //Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                            //mainIntent.putExtra("uid",uid);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);


                        }

                    }
                });
    }



}
