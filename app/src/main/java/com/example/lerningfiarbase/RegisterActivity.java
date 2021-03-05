package com.example.lerningfiarbase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    EditText phoneNum,code;
    Button next;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker codePicker;

    ArrayList<ModelUser> mArrayList;

    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;

    Boolean verificationInProgress = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mArrayList = new ArrayList<>();

        phoneNum = findViewById(R.id.phone);
        code = findViewById(R.id.codeEnter);
        progressBar = findViewById(R.id.progressBar);
        next= findViewById(R.id.nextBtn);
        state = findViewById(R.id.state);
        codePicker = findViewById(R.id.ccp);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!verificationInProgress){
                    if (!phoneNum.getText().toString().isEmpty() && phoneNum.getText().toString().length()==10){
                        String phoneNumber = "+"+codePicker.getSelectedCountryCode()+phoneNum.getText().toString();
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP...");
                        state.setVisibility(View.VISIBLE);
                        Log.d("TAG", "onClick: "+phoneNumber);

                        requestOTP(phoneNumber);

                    }else {
                        phoneNum.setError("phone number is not valid");
                    }
                }else {
                    String userOTP = code.getText().toString();

                    if (!userOTP.isEmpty() && userOTP.length() == 6){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, userOTP);
                        verifyAuth(credential);

                    }else{
                        code.setError("not valid OTP");
                    }
                }
            }
        });
    }


    private void verifyAuth(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()){
                 checkUser();
              }else{
                  Toast.makeText(RegisterActivity.this,"Authentication is Failed",Toast.LENGTH_SHORT).show();
              }
            }
        });
    }


    private void checkUser() {
        DocumentReference docRef = firestore.collection("users").document(auth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Log.d("ddfsfbc", "onSuccess: exists");
                }else{
                    startActivity(new Intent(getApplicationContext(), AddDetailActivity.class));
                    Log.d("ddfsfbc", "onSuccess: not exists");
                }
                finish();
            }
        });
    }

    private void requestOTP(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                code.setVisibility(View.VISIBLE);
                verificationId = s;
                token = forceResendingToken;
                next.setText("Verify");
               // next.setEnabled(false);
                verificationInProgress = true;


            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(RegisterActivity.this,"OTP is expired ",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                verifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(RegisterActivity.this,"Can not create Account " + e.getMessage(),Toast.LENGTH_SHORT).show();

                Log.d("fail", "onVerificationFailed: "+e.getMessage());

            }
        });
    }
}