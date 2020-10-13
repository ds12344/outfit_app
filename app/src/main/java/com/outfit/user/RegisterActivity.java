package com.outfit.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    FirebaseAuth mFirebaseAuth;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String strFirstName, strLastName, strEmail, strPassword, strConfirmPassword, imagePath = "";
    ProgressDialog mDialog;

    CircleImageView imgUser;

    EditText edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        btnRegister = findViewById(R.id.btnRegister);
        imgUser = findViewById(R.id.imgUser);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(RegisterActivity.this)
                        .setFolderMode(true)
                        .setFolderTitle("Album")
                        .setDirectoryName("Image Picker")
                        .setMultipleMode(false)
                        .setShowNumberIndicator(true)
                        .setMaxSize(1)
                        .setRequestCode(100)
                        .start();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strFirstName = edtFirstName.getText().toString();
                strLastName = edtLastName.getText().toString();
                strEmail = edtEmail.getText().toString();
                strPassword = edtPassword.getText().toString();
                strConfirmPassword = edtConfirmPassword.getText().toString();

                if (strFirstName.equals("") && strLastName.equals("") && strEmail.equals("") && strPassword.equals("") && strConfirmPassword.equals("") && imagePath.equals("")) {
                    showToast("Please fill all details");
                } else if (strFirstName.equals("")) {
                    showToast("Please enter first name");
                } else if (strLastName.equals("")) {
                    showToast("Please enter last name");
                } else if (strEmail.equals("")) {
                    showToast("Please enter email");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                    showToast("Please enter valid email");
                } else if (strPassword.equals("")) {
                    showToast("Please enter password");
                } else if (strConfirmPassword.equals("")) {
                    showToast("Please enter confirm password");
                } else if (!strPassword.equals(strConfirmPassword)) {
                    showToast("Password does not match");
                } else if (imagePath.equals("")) {
                    showToast("Please choose image");
                } else {
                    register();
                }

            }
        });
    }

    void register() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()) {
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "SignUp Unsuccessful, Please try Again",Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadImage(task.getResult().getUser().getUid(), "user_"+task.getResult().getUser().getUid(), getDrawableToBytes(imgUser));

                }
            }
        });
    }

    private void uploadImage(final String userId, final String name, byte[] res) {
        final StorageReference imageFolder = storageReference.child("user").child(name);
        imageFolder.putBytes(res).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        updateUserInfo(userId, uri.toString());
                    }
                });
            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                    }
                });
    }

    private void updateUserInfo(String userId, String path) {
        final HashMap<String,Object> map = new HashMap<>();
        map.put("firstName", strFirstName);
        map.put("lastName", strLastName);
        map.put("email", strEmail);
        map.put("image", path);
        map.put("userId", userId);
        map.put("isSeller", false);

        FirebaseDatabase.getInstance().getReference("User")
                .child(userId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        Intent intent3 = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        finish();
                    }
                });
    }

    private byte[] getDrawableToBytes(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    void showToast(String msg) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 100)) {
            ArrayList<Image> images  = ImagePicker.getImages(data);

            for (Image image : images) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imagePath = image.getUri().toString();
                    Glide.with(this)
                            .load(image.getUri())
                            .into(imgUser);
                } else {
                    imagePath = image.getPath();
                    Glide.with(this)
                            .load(image.getPath())
                            .into(imgUser);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
