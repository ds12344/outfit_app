package com.outfit.user;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    ImageView back;
    Button btnSubmit;
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        back = findViewById(R.id.back);
        btnSubmit = findViewById(R.id.btnSubmit);
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strOld = edtOldPassword.getText().toString();
                String strNew = edtNewPassword.getText().toString();
                String strConfirm = edtConfirmPassword.getText().toString();

                if (strOld.isEmpty() && strNew.isEmpty() && strConfirm.isEmpty()) {
                    showToast("Please fill all the fields");
                } else if (strOld.isEmpty()) {
                    showToast("Please old Password");
                } else if (strNew.isEmpty()) {
                    showToast("Please new password");
                } else if (strConfirm.isEmpty()) {
                    showToast("Please enter confirm password");
                } else if (!strNew.equals(strConfirm)) {
                    showToast("New password and confirm password does not match");
                } else {
                    changePassword(strOld, strNew);
                }
            }
        });
    }

    void showToast(String msg) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }


    private void changePassword(String oldPassword, final String newPassword) {

        final ProgressDialog mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        showToast("Password updated");
                                        finish();
                                    } else {
                                        showToast("Error password not updated");
                                    }
                                }
                            });
                        } else {
                            mDialog.dismiss();
                            showToast("Old password not matched");
                        }
                    }
                });
    }
}
