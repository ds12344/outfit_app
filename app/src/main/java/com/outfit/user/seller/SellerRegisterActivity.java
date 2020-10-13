package com.outfit.user.seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.outfit.user.HomeActivity;
import com.outfit.user.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SellerRegisterActivity extends AppCompatActivity {

    ImageView back, imgShowroom, imgIdProof;
    Button btnSubmit;
    EditText edtShowroomName, edtShowroomAddress, edtPhone, edtGst, edtShippingPrice;

    FirebaseAuth mFirebaseAuth;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog mDialog;

    String showroomImagePath = "", strShowroomName, strAddress, strPhone, strGst, strPrice, idProofImagePath = "";
    boolean isIdProof = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        back = findViewById(R.id.back);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgShowroom = findViewById(R.id.imgShowroom);
        imgIdProof = findViewById(R.id.imgIdProof);
        edtShowroomName = findViewById(R.id.edtShowroomName);
        edtShowroomAddress = findViewById(R.id.edtShowroomAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtGst = findViewById(R.id.edtGst);
        edtShippingPrice = findViewById(R.id.edtShippingPrice);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=new Intent(SellerRegisterActivity.this, HomeActivity.class);
              startActivity(intent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strShowroomName = edtShowroomName.getText().toString();
                strAddress = edtShowroomAddress.getText().toString();
                strPhone = edtPhone.getText().toString();
                strGst = edtGst.getText().toString();
                strPrice = edtShippingPrice.getText().toString();

                if (showroomImagePath.isEmpty() && strShowroomName.isEmpty() && strAddress.isEmpty() &&
                        strPhone.isEmpty() && strGst.isEmpty() && strPrice.isEmpty() && idProofImagePath.isEmpty()) {
                    showToast("Please fill all fields");
                } else if (showroomImagePath.isEmpty()) {
                    showToast("Please add showroom image");
                } else if (strShowroomName.isEmpty()) {
                    showToast("Please enter showroom name");
                } else if (strAddress.isEmpty()) {
                    showToast("Please enter address");
                } else if (strPhone.isEmpty()) {
                    showToast("Please enter phone");
                } else if (strGst.isEmpty()) {
                    showToast("Please enter GST");
                } else if (strPrice.isEmpty()) {
                    showToast("Please enter shipping price");
                } else if (idProofImagePath.isEmpty()) {
                    showToast("Please add ID proof");
                } else {
                    register();
                }
            }
        });

        imgShowroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryIntent(101);
            }
        });

        imgIdProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryIntent(102);
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

    void register() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        uploadImage("showroom_"+FirebaseAuth.getInstance().getUid(), getDrawableToBytes(imgShowroom), "showroom");
    }

    boolean isIdUpload = false;
    String showroomImageUrl = "";
    String idProofUrl = "";
    private void uploadImage(final String name, byte[] res, String folderName) {
        final StorageReference imageFolder = storageReference.child(folderName).child(name);
        imageFolder.putBytes(res).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        if (!isIdUpload) {
                            showroomImageUrl = uri.toString();
                            isIdUpload = true;
                            uploadImage("IDProof_"+FirebaseAuth.getInstance().getUid(), getDrawableToBytes(imgIdProof), "ID_Proof");
                        } else {
                            idProofUrl = uri.toString();
                            updateUserInfo();
                        }

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

    private void updateUserInfo() {
        final HashMap<String,Object> map = new HashMap<>();
        map.put("showroomName", strShowroomName);
        map.put("address", strAddress);
        map.put("phone", strPhone);
        map.put("showroomImage", showroomImageUrl);
        map.put("idProof", idProofUrl);
        map.put("gst", strGst);
        map.put("shippingPrice", strPrice);
        map.put("isSeller", true);
        map.put("status", false);
        map.put("sellerId", FirebaseAuth.getInstance().getUid());

        FirebaseDatabase.getInstance().getReference("Seller")
                .child(FirebaseAuth.getInstance().getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        updateSellerStatus();


                    }
                });
    }

    private void updateSellerStatus() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("isSeller", true);
        FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        Intent intent3 = new Intent(SellerRegisterActivity.this, HomeSellerActivity.class);
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
        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 101)) {
            ArrayList<Image> images  = ImagePicker.getImages(data);

            for (Image image : images) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    showroomImagePath = image.getUri().toString();
                    Glide.with(this)
                            .load(image.getUri())
                            .into(imgShowroom);
                } else {
                    showroomImagePath = image.getPath();
                    Glide.with(this)
                            .load(image.getPath())
                            .into(imgShowroom);
                }
            }
        } else if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 102)) {
            ArrayList<Image> images  = ImagePicker.getImages(data);

            for (Image image : images) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    idProofImagePath = image.getUri().toString();
                    Glide.with(this)
                            .load(image.getUri())
                            .into(imgIdProof);
                } else {
                    idProofImagePath = image.getPath();
                    Glide.with(this)
                            .load(image.getPath())
                            .into(imgIdProof);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
