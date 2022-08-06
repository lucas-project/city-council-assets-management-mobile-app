package com.application.issue_reporter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private StorageReference storageRef;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private FirebaseAuth instance;
    private FirebaseStorage storage;
    private ImageView addimg;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        reference = FirebaseDatabase.getInstance().getReference();
        instance = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance("gs://info-2b3c0.appspot.com");
        storageRef = storage.getReference();

        addimg = findViewById(R.id.addimg);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
    }

    public void bac(View view) {
        finish();
    }

    public void doNext(View view) {
        if (uriList.size() ==0){
            Toast.makeText(ImageActivity.this,"You must take a photo of the issue!",Toast.LENGTH_SHORT).show();
            return;
        }

    doUpload();
    }


    private void doUpload() {
        progressDialog.show();
        Uri file = uriList.get(0);
        storageReference = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = storageReference.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                progressDialog.dismiss();
                Toast.makeText(ImageActivity.this, "upload err.",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.child("images/" + file.getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri tempUri) {
                        progressDialog.dismiss();
                        String string = tempUri.toString();

                        DatabaseReference push = reference.child("Report").child(instance.getUid()).push();
                        Report report = new Report(instance.getUid(), push.getKey()
                                , SPUtils.get(ImageActivity.this, "sort", "").toString()
                                , SPUtils.get(ImageActivity.this, "address", "").toString()
                                , SPUtils.get(ImageActivity.this, "lat", "").toString()
                                , SPUtils.get(ImageActivity.this, "lon", "").toString()
                                , SPUtils.get(ImageActivity.this, "detail", "").toString()
                                , string, push.getKey(), "1",System.currentTimeMillis()+""

                        );
                        push.setValue(report);
                        progressDialog.dismiss();
                        SPUtils.put(ImageActivity.this,"ticket",push.getKey());
                        startActivity(new Intent(ImageActivity.this,ReportActivity.class));
                        Toast.makeText(ImageActivity.this, "report suc.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        progressDialog.dismiss();
                        Toast.makeText(ImageActivity.this, "report err.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private static final String FILE_PROVIDER_AUTHORITY = "com.application.issue_reporter.fileprovider";
    private Uri imageUri;
    public void doCamera(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File imageFile = createImageFile();
            if (imageFile != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(this, FILE_PROVIDER_AUTHORITY, imageFile);
                } else {
                    imageUri = Uri.fromFile(imageFile);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, 2);
        }

    }

    private File createImageFile() {
        String format = new SimpleDateFormat("yyyy-MM-DD").format(new Date());
        String fileName = "JDEG_" + format + "_";
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(fileName, ".jpg", file);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    List<Uri> uriList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            uriList.clear();
            if (data.getClipData() != null) {
                data.getClipData().getItemAt(0).getUri();
                uriList.add(data.getClipData().getItemAt(0).getUri());
            } else if (data.getData() != null) {
                uriList.add(data.getData());
            }

            addimg.setImageURI(uriList.get(0));
        } else if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
            uriList.clear();

           /* Bundle bundle = data.getExtras();
            Bitmap photo = (Bitmap) bundle.get("data");
            addimg.setImageBitmap(photo);
            uriList.add(data.getData());*/
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                addimg.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            uriList.add(imageUri);

        }
    }

    public void addImg(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
}