package com.outfit.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText regFirstName,regLastName,regEmail,regPassword,regConfirmPassword;
    FirebaseAuth mfirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);
        regEmail=findViewById(R.id.edtEmail);
        regPassword=findViewById(R.id.edtPasword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String email=regEmail.getText().toString();
                 String password=regPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    regEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    regPassword.setError("Password is required");
                    return;
                }
                if(password.length()<6){
                    regPassword.setError("Password must be >=6 character");
                    return;
                }
                 mfirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                             Toast.makeText(RegisterActivity.this,"User Creates successfully.",Toast.LENGTH_SHORT).show();

                             Intent intent= new Intent(RegisterActivity.this,HomeActivity.class);
                             startActivity(intent);
                         }else{
                             Toast.makeText(RegisterActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     }
                 });

            }
        });
    }


}
