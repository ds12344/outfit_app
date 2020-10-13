package com.outfit.user.seller;

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
import com.outfit.user.R;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class EditSellerProfileActivity extends AppCompatActivity {

    ImageView back, imgShowroom;
    Button btnSubmit;
    EditText edtName, edtAddress, edtPhone, edtGst, edtPrice;
    String imagePath = "", strName, strAddress, strPhone, strGst, strPrice, imageUrl;

    FirebaseAuth mFirebaseAuth;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_seller_profile);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        back = findViewById(R.id.back);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgShowroom = findViewById(R.id.imgShowroom);
        edtName = findViewById(R.id.edtShowroomName);
        edtAddress = findViewById(R.id.edtShowroomAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtGst = findViewById(R.id.edtGst);
        edtPrice = findViewById(R.id.edtShippingPrice);

        imageUrl = getIntent().getStringExtra("showroomImage");
        strName = getIntent().getStringExtra("name");
        strAddress = getIntent().getStringExtra("address");
        strPhone = getIntent().getStringExtra("phone");
        strGst = getIntent().getStringExtra("gst");
        strPrice = getIntent().getStringExtra("shippingPrice");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .into(imgShowroom);
        edtName.setText(strName);
        edtAddress.setText(strAddress);
        edtPhone.setText(strPhone);
        edtGst.setText(strGst);
        edtPrice.setText(strPrice);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = edtName.getText().toString();
                strAddress = edtAddress.getText().toString();
                strPhone = edtPhone.getText().toString();
                strGst = edtGst.getText().toString();
                strPrice = edtPrice.getText().toString();

                if (strName.isEmpty() && strAddress.isEmpty() && strPhone.isEmpty() && strGst.isEmpty() && strPrice.isEmpty()) {
                    showToast("Please fill all fields");
                } else if (strName.isEmpty()) {
                    showToast("Please enter showroom name");
                } else if (strAddress.isEmpty()) {
                    showToast("Please enter showroom address");
                } else if (strPhone.isEmpty()) {
                    showToast("Please enter phone");
                } else if (strGst.isEmpty()) {
                    showToast("Please enter GST/HST");
                } else if (strPrice.isEmpty()) {
                    showToast("Please enter shipping price");
                } else {
                    mDialog = new ProgressDialog(EditSellerProfileActivity.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.setCancelable(false);
                    mDialog.show();
                    if (imagePath.isEmpty()) {
                        updateUserInfo(FirebaseAuth.getInstance().getUid(), "");
                    } else {
                        uploadImage(FirebaseAuth.getInstance().getUid(), "showroom_"+ FirebaseAuth.getInstance().getUid(), getDrawableToBytes(imgShowroom));
                    }
                }
            }
        });

        imgShowroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryIntent(101);
            }
        });
    }

    private void openGalleryIntent(int requestCode) {
        ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image Picker")
                .setMultipleMode(false)
                .setShowNumberIndicator(true)
                .setMaxSize(1)
                .setRequestCode(requestCode)
                .start();
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
        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 101)) {
            ArrayList<Image> images  = ImagePicker.getImages(data);

            for (Image image : images) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imagePath = image.getUri().toString();
                    Glide.with(this)
                            .load(image.getUri())
                            .into(imgShowroom);
                } else {
                    imagePath = image.getPath();
                    Glide.with(this)
                            .load(image.getPath())
                            .into(imgShowroom);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUserInfo(String userId, String path) {
        final HashMap<String,Object> map = new HashMap<>();
        map.put("showroomName", strName);
        map.put("address", strAddress);
        map.put("phone", strPhone);
        map.put("gst", strGst);
        map.put("shippingPrice", strPrice);
        if (!path.isEmpty())
            map.put("showroomImage", path);

        FirebaseDatabase.getInstance().getReference("Seller")
                .child(userId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        showToast("Showroom Updated");
                        finish();
                    }
                });
    }

    private void uploadImage(final String userId, final String name, byte[] res) {
        final StorageReference imageFolder = storageReference.child("showroom").child(name);
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
}
