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
import android.widget.RadioGroup;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    ImageView back, imgProduct;
    EditText edtName, edtDescription, edtPrice;
    RadioGroup rgGender;
    Button btnSubmit;
    Boolean isEdit = false;
    TextView txtTitle;
    String type = "men", imagePath = "", imageUrl = "", strName = "", strDescription = "", strPrice = "", productId = "";
    FirebaseAuth mFirebaseAuth;
    //Firebase storage
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog mDialog;
    HashMap data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        isEdit = getIntent().getBooleanExtra("isEdit", false);


        back = findViewById(R.id.back);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgProduct = findViewById(R.id.imgProduct);
        edtName = findViewById(R.id.edtProductName);
        edtDescription = findViewById(R.id.edtProductDescription);
        edtPrice = findViewById(R.id.edtProductPrice);
        rgGender = findViewById(R.id.rgGender);
        txtTitle = findViewById(R.id.txtTitle);

        if (isEdit) {
            txtTitle.setText("Edit Product");
            data = (HashMap) getIntent().getSerializableExtra("data");
            imageUrl = data.get("image").toString();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo)
                    .into(imgProduct);
            strName = data.get("name").toString();
            edtName.setText(strName);
            strPrice = data.get("price").toString();
            edtPrice.setText(strPrice);
            strDescription = data.get("description").toString();
            edtDescription.setText(strDescription);
            type = data.get("type").toString();
            productId = data.get("productId").toString();
            if (type.equals("men")) {
                rgGender.check(R.id.rbMen);
            } else {
                rgGender.check(R.id.rbWomen);
            }
        }

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
                strDescription = edtDescription.getText().toString();
                strPrice = edtPrice.getText().toString();

                if (isEdit) {
                    if (strName.isEmpty() && strDescription.isEmpty() && type.isEmpty() && strPrice.isEmpty()) {
                        showToast("Please fill all fields");
                    } else if (strName.isEmpty()) {
                        showToast("Please enter product name");
                    } else if (strDescription.isEmpty()) {
                        showToast("Please enter product description");
                    } else if (type.isEmpty()) {
                        showToast("Please choose product type");
                    } else if (strPrice.isEmpty()) {
                        showToast("Please enter product price");
                    } else {
                        mDialog = new ProgressDialog(AddProductActivity.this);
                        mDialog.setMessage("Please wait...");
                        mDialog.setCancelable(false);
                        mDialog.show();
                        if (imagePath.isEmpty()) {
                            editProductInfo(FirebaseAuth.getInstance().getUid(), productId);
                        } else {
                            uploadImage(FirebaseAuth.getInstance().getUid(), productId, getDrawableToBytes(imgProduct));
                        }
                    }
                } else {
                    if (imagePath.isEmpty() && strName.isEmpty() && strDescription.isEmpty() && type.isEmpty() && strPrice.isEmpty()) {
                        showToast("Please fill all fields");
                    } else if (imagePath.isEmpty()) {
                        showToast("Please add product image");
                    } else if (strName.isEmpty()) {
                        showToast("Please enter product name");
                    } else if (strDescription.isEmpty()) {
                        showToast("Please enter product description");
                    } else if (type.isEmpty()) {
                        showToast("Please choose product type");
                    } else if (strPrice.isEmpty()) {
                        showToast("Please enter product price");
                    } else {
                        mDialog = new ProgressDialog(AddProductActivity.this);
                        mDialog.setMessage("Please wait...");
                        mDialog.setCancelable(false);
                        mDialog.show();
                        productId = "product_"+Calendar.getInstance().getTimeInMillis();
                        uploadImage(FirebaseAuth.getInstance().getUid(), productId, getDrawableToBytes(imgProduct));
                    }
                }

            }
        });

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbMen) {
                    type = "men";
                } else if (checkedId == R.id.rbWomen) {
                    type = "women";
                }
            }
        });

        imgProduct.setOnClickListener(new View.OnClickListener() {
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
            // Do stuff with image's path or id. For example:
            for (Image image : images) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imagePath = image.getUri().toString();
                    Glide.with(this)
                            .load(image.getUri())
                            .into(imgProduct);
                } else {
                    imagePath = image.getPath();
                    Glide.with(this)
                            .load(image.getPath())
                            .into(imgProduct);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addProductInfo(String userId) {
        final HashMap<String,Object> map = new HashMap<>();
        map.put("name", strName);
        map.put("description", strDescription);
        map.put("type", type);
        map.put("price", strPrice);
        map.put("sellerId", userId);
        map.put("image", imageUrl);
        map.put("productId", productId);

        FirebaseDatabase.getInstance().getReference("Product")
                .child(userId)
                .child(productId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        showToast("Product Added");
                        finish();
                    }
                });
    }

    private void editProductInfo(String userId, String productId) {
        final HashMap<String,Object> map = new HashMap<>();
        map.put("name", strName);
        map.put("description", strDescription);
        map.put("type", type);
        map.put("price", strPrice);
        map.put("sellerId", userId);
        map.put("image", imageUrl);
        map.put("productId", productId);

        FirebaseDatabase.getInstance().getReference("Product")
                .child(userId)
                .child(productId)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        showToast("Product Edit successfully");
                        Intent intent = new Intent();
                        intent.putExtra("data", map);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }

    private void uploadImage(final String userId, final String name, byte[] res) {
        final StorageReference imageFolder = storageReference.child("product").child(name);
        imageFolder.putBytes(res).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Upload this url to avtar property of user
                        //First you need to add avtar property on user model
                        imageUrl = uri.toString();
                        if (!isEdit) {
                            addProductInfo(userId);
                        } else {
                            editProductInfo(userId, productId);
                        }
                    }
                });
            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        /*double progress = (100.0* taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mDialog.setMessage("Uploading " + pos + " : "+progress+"%");*/
                    }
                });
    }
}
