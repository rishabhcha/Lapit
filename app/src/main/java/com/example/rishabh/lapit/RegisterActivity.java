package com.example.rishabh.lapit;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class RegisterActivity extends AppCompatActivity {

    ImageButton mProfilePicSelector;
    EditText mProfileNameField, mProfileEmailField, mProfilePasswordField;
    Button mRegisterBtn;

    private Toolbar mRegisterToolbar;

    private static final int GALLERY_REQUEST = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegisterToolbar = (Toolbar) findViewById(R.id.registerToolbar);
        setSupportActionBar(mRegisterToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProfilePicSelector = (ImageButton) findViewById(R.id.profilePicSelector);
        mProfileNameField = (EditText) findViewById(R.id.profileNameField);
        mProfileEmailField = (EditText) findViewById(R.id.passowrdField);
        mProfilePasswordField = (EditText) findViewById(R.id.passowrdField);
        mRegisterBtn = (Button) findViewById(R.id.signinBtn);

        mProfilePicSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("*/image");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){

            Uri uri = data.getData();
            mProfilePicSelector.setImageURI(uri);

        }

    }
}
