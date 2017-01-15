package com.example.rishabh.lapit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GetStartedActivity extends AppCompatActivity {

    private Button mSignInBtn, mRegistrerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        mSignInBtn = (Button) findViewById(R.id.getStartedSignIn);
        mRegistrerBtn = (Button) findViewById(R.id.getStartedRegister);

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signinIntent = new Intent(GetStartedActivity.this,SignInActivity.class);
                startActivity(signinIntent);

            }
        });

        mRegistrerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(GetStartedActivity.this,RegisterActivity.class);
                startActivity(registerIntent);

            }
        });

    }
}
