package com.example.ridusdriver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText name, phoneNumber;
    Button next_btn;
    private String phone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        name = findViewById(R.id.name);
        phoneNumber = findViewById(R.id.phoneNo);
        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                switch(v.getId()){
                    case R.id.next_btn:
                        if(validationNumber()) {
                            Intent intent =new Intent(LoginActivity.this, VerificationScreenActivity.class);
                            intent.putExtra("phone","+91"+phoneNumber.getText().toString());
                            intent.putExtra("name",name.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),
                                    "Phone Number required", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
    }

    private boolean validationNumber() {
        phone = phoneNumber.getText().toString().trim();
        String fName = name.getText().toString();
        if (phone.isEmpty() && fName.isEmpty()) {
            phoneNumber.setError("Phone number is required");
            phoneNumber.requestFocus();
            return false;
        }

        if (phone.length() < 10) {
            phoneNumber.setError("Please enter a valid phone");
            phoneNumber.requestFocus();
            return false;
        }
        return true;

    }



}

