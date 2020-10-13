package com.outfit.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileActivity extends AppCompatActivity {

    ImageView back;
    Button btnSubmit;
    String userEmail,image, firstName, lastName, imagePath = "" ;
    CircleImageView imgUser;
    EditText edtFirstName, edtLastName;
    FirebaseAuth mFirebaseAuth;
    //Firebase storage
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        back = findViewById(R.id.back);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgUser = findViewById(R.id.imgUser);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);

        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        image = getIntent().getStringExtra("image");

        edtFirstName.setText(firstName);
        edtLastName.setText(lastName);
        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.logo)
                .into(imgUser);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = edtFirstName.getText().toString();
                lastName = edtLastName.getText().toString();

                if (firstName.isEmpty() && lastName.isEmpty()) {
                    showToast("Please fill all the details");
                } else if (firstName.isEmpty()) {
                    showToast("Please enter first name");
                } else if (lastName.isEmpty()) {
                    showToast("Please enter last name");
                } else {
                    mDialog = new ProgressDialog(EditProfileActivity.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.setCancelable(false);
                    mDialog.show();
                    if (imagePath.isEmpty()) {
                        updateUserInfo(FirebaseAuth.getInstance().getUid(), "");
                    } else {
                        uploadImage(FirebaseAuth.getInstance().getUid(), "user_"+ FirebaseAuth.getInstance().getUid(), getDrawableToBytes(imgUser));
                    }
                }
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditProfileActivity.this)
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

    private byte[] getDrawableToBytes(ImageView imageView) {
        Drawable d = imageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    private void updateUserInfo(String userId, String path) {
        final HashMap<String,Object> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        if (!path.isEmpty())
            map.put("image", path);

        FirebaseDatabase.getInstance().getReference("User")
                .child(userId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        showToast("Profile Updated");
                        finish();
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

    void showToast(String msg) {
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }
}
