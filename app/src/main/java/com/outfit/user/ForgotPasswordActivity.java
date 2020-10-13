package com.outfit.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.outfit.user.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button btnSubmit;
    ImageView back;
    EditText edtLogin;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnSubmit = findViewById(R.id.btnSubmit);
        edtLogin = findViewById(R.id.edtLoginEmail);
        back = findViewById(R.id.back);

        firebaseAuth = FirebaseAuth.getInstance();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtLogin.getText().toString().isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(edtLogin.getText().toString()).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                } else {
                    forgot(edtLogin.getText().toString());
                }
            }
        });
    }

    private void forgot(String email) {
        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this,"Password send to your email", Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
