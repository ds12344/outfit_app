package com.outfit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    TextView txtForgotPassword, txtRegister;
    Button btnLogin;
    FirebaseAuth mFirebaseAuth;
    EditText edtLoginEmail, edtLoginPassword;

    String strEmail, strPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();

        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtRegister = findViewById(R.id.txtRegister);
        btnLogin = findViewById(R.id.btnLogin);
        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent2);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strEmail = edtLoginEmail.getText().toString();
                strPassword = edtLoginPassword.getText().toString();

                if (strEmail.isEmpty() && strPassword.isEmpty()) {
                    showToast("Please fill all the fields");
                } else if (strEmail.isEmpty()) {
                    showToast("Please enter email");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    showToast("Please enter valid email");
                } else if (strPassword.isEmpty()) {
                    showToast("Please enter password");
                } else {
                    loginApi();
                }
            }
        });
    }

    private void loginApi() {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {
                mDialog.dismiss();
                if(!task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Email/password is incorrect",Toast.LENGTH_SHORT).show();
                }
                else  {
                    if (task.getResult().getUser() == null) {
                        Toast.makeText(LoginActivity.this, "Email/password is incorrect",Toast.LENGTH_SHORT).show();
                    } else {

                        Intent inToHome = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(inToHome);
                        finish();
                    }
                }
            }
        });
    }

    void showToast(String msg) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }


}
