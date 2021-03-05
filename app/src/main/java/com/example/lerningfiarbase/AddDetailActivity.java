package com.example.lerningfiarbase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDetailActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    EditText name,age,phone;
    Button save;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_detail);

        name = findViewById(R.id.et_name);
        age = findViewById(R.id.et_age);
        phone = findViewById(R.id.et_phone);
        save = findViewById(R.id.btn_save);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference docref = firestore.collection("users").document(userID);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().isEmpty() && !age.getText().toString().isEmpty() && !phone.getText().toString().isEmpty() && phone.getText().toString().length() == 10){
                   String userName = name.getText().toString();
                   String userAge = age.getText().toString();
                    String userPhoneNumber = phone.getText().toString();

                    Map<String , Object> user = new HashMap<>();
                    user.put("name",userName);
                    user.put("age",userAge);
                    user.put("phone",userPhoneNumber);

                    docref.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                          if (task.isSuccessful()) {
                              startActivity(new Intent(getApplicationContext(), MainActivity.class));
                              finish();
                          }else {
                              Toast.makeText(AddDetailActivity.this,"Data is not inserted",Toast.LENGTH_SHORT).show();
                          }
                        }
                    });
                }else
                {
                    Toast.makeText(AddDetailActivity.this,"insert detail",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}