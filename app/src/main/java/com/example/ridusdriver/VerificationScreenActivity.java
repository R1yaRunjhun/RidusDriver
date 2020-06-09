package com.example.ridusdriver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerificationScreenActivity extends AppCompatActivity {
    private TextView textViewPhone;
    private EditText editTextCode;
    private Button verifyNext;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String verificationId,phoneNo, fullName;
    private String userID;
    private DatabaseReference mDriverDatabase;
    private String mName;
    private String mPhone;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth=FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editTextCode);
        textViewPhone = findViewById(R.id.textViewPhone);
        progressBar = findViewById(R.id.progressBar);
        verifyNext= findViewById(R.id.nextBtn);
        phoneNo = getIntent().getStringExtra("phone");
        textViewPhone.setText(phoneNo);
        fullName= getIntent().getStringExtra("name");


        sendVerificationCode(phoneNo);

        verifyNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();
                if(code.isEmpty() || code.length()<6){
                    editTextCode.setError("Enter code");
                    editTextCode.requestFocus();
                    return;
                } verifyCode(code);
            }
        });


    }
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(credential);
    }


    private void saveUserInformation() {
        mName = fullName;
        mPhone = textViewPhone.getText().toString();


        Map userInfo = new HashMap();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        mDriverDatabase.updateChildren(userInfo);

    }



    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            mAuth = FirebaseAuth.getInstance();
                            userID = mAuth.getCurrentUser().getUid();
                            mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);

                            saveUserInformation();

                            Intent intent =new Intent(VerificationScreenActivity.this,MapsActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(VerificationScreenActivity.this,"On verification success",Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(VerificationScreenActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        progressBar.setVisibility((View.VISIBLE));
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, VerificationScreenActivity.this ,mCallBack);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s,@NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId =s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code!=null){
                editTextCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerificationScreenActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();


        }
    };
}

