package com.application.issue_reporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editFrist;
    private EditText editLast;
    private EditText editPwd;
    private EditText editConfirm;
    private FirebaseAuth instance;
    private DatabaseReference userDatabaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = findViewById(R.id.edit_email);
        editFrist = findViewById(R.id.edit_frist);
        editLast = findViewById(R.id.edit_last);
        editPwd = findViewById(R.id.edit_pwd);
        editConfirm = findViewById(R.id.edit_confirm);

        initViewDrawable(R.mipmap.info_email,editEmail);
        initViewDrawable(R.mipmap.info_user,editFrist);
        initViewDrawable(R.mipmap.info_user,editLast);
        initViewDrawable(R.mipmap.info_pwd,editPwd);
        initViewDrawable(R.mipmap.info_pwd,editConfirm);

        instance = FirebaseAuth.getInstance();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
    }

    private void initViewDrawable(int id, EditText view) {
        Drawable drawable = getResources().getDrawable(id);
        drawable .setBounds(0, 0, 60, 60);
        view.setCompoundDrawablePadding(10);
        view .setCompoundDrawables(drawable , null, null, null);
    }

    public void bac(View view) {
        finish();
    }

    public void doReg(View view) {
        if (TextUtils.isEmpty(editEmail.getText().toString())
                ||TextUtils.isEmpty(editFrist.getText().toString())
                ||TextUtils.isEmpty(editLast.getText().toString())
                ||TextUtils.isEmpty(editPwd.getText().toString())
                ||TextUtils.isEmpty(editConfirm.getText().toString())
        ){
            Toast.makeText(RegisterActivity.this,"empty data err!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!editPwd.getText().toString().equals(editConfirm.getText().toString())){
            Toast.makeText(RegisterActivity.this,"Inconsistent passwords!",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        instance.createUserWithEmailAndPassword(editEmail.getText().toString(), editPwd.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = instance.getCurrentUser();
                            DatabaseReference push = userDatabaseReference.push();
                            User tempUser = new User(editFrist.getText().toString(),editLast.getText().toString(),user.getUid());
                            push.setValue(tempUser);
                            Toast.makeText(RegisterActivity.this,"reg suc!",Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}