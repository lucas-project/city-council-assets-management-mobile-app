package com.application.issue_reporter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private final String[] PERMISSIONS_LOCATION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};

    private final static int REQUEST_LOCATION = 100;
    private final static int REQUEST_CAMERA = 101;
    private final static int CHOOSE_REQUEST = 188;
    private EditText editEmail;
    private EditText editPwd;
    private FirebaseAuth instance;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.edit_email);
        editPwd = findViewById(R.id.edit_pwd);

        initViewDrawable(R.mipmap.info_email, editEmail);
        initViewDrawable(R.mipmap.info_pwd, editPwd);

        instance = FirebaseAuth.getInstance();

        if (instance.getCurrentUser() != null&&instance.getCurrentUser().getEmail().equals("admin@gmail.com")) {
            startActivity(new Intent(LoginActivity.this, ManageActivity.class));
            finish();
        }else if (instance.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        enableMyLocation();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
    }

    private void initViewDrawable(int id, EditText view) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(id);
        drawable.setBounds(0, 0, 60, 60);
        view.setCompoundDrawablePadding(10);
        view.setCompoundDrawables(drawable, null, null, null);
    }

    public void bac(View view) {
        finish();
    }

    public void doLogin(View view) {
        if (TextUtils.isEmpty(editEmail.getText().toString())
                || TextUtils.isEmpty(editPwd.getText().toString())
        ) {
            Toast.makeText(LoginActivity.this, "empty data err!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();

        instance.signInWithEmailAndPassword(editEmail.getText().toString(), editPwd.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            if (editEmail.getText().toString().equals("admin@gmail.com")){
                                //todo  admin
                                startActivity(new Intent(LoginActivity.this, ManageActivity.class));
                                Toast.makeText(LoginActivity.this, "Login suc.",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                Toast.makeText(LoginActivity.this, "Login suc.",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void enableMyLocation() {
        PermissionUtils.onRequestMultiplePermissionsResult(this, PERMISSIONS_LOCATION, new PermissionUtils.OnPermissionListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(String... permission) {
                showNoticeDialog(REQUEST_LOCATION);
            }

            @Override
            public void alwaysDenied(String... permission) {
                PermissionUtils.goToAppSetting(LoginActivity.this, "Permission");
            }


        });
    }

    private void showNoticeDialog(final int type) {
        final String temp = "Permission notice";
        String tips = null;
        if (type == REQUEST_LOCATION) {
            tips = String.format(temp, "Location");
        } else if (type == CHOOSE_REQUEST) {
            tips = String.format(temp, "EXTERNAL STORAGE");
        } else if (type == REQUEST_CAMERA) {
            tips = String.format(temp, "CAMERA");
        }
        if (!TextUtils.isEmpty(tips)) {
            new android.app.AlertDialog.Builder(LoginActivity.this)
                    .setTitle(PermissionUtils.TITLE)
                    .setMessage(tips)
                    .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (type == REQUEST_LOCATION) {
                                PermissionUtils.requestMultiplePermissions(LoginActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION);
                            } else if (type == CHOOSE_REQUEST) {
                                PermissionUtils.requestPermission(LoginActivity.this, PermissionUtils.PERMISSION_SD, CHOOSE_REQUEST);
                            } else if (type == REQUEST_CAMERA) {
                                PermissionUtils.requestPermission(LoginActivity.this, PermissionUtils.PERMISSION_SD, REQUEST_CAMERA);
                            }

                        }
                    }).setNegativeButton("cancel", null)
                    .show();
        }
    }

    public void doReg(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}