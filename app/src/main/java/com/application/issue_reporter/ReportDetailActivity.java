package com.application.issue_reporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportDetailActivity extends AppCompatActivity {

    private String key;
    private String uid;
    private TextView typetext;
    private TextView detailtext;
    private TextView addresstext;
    private TextView statustext;
    private ImageView image;
    private TextView finishtext;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        key = getIntent().getStringExtra("key");
        uid = getIntent().getStringExtra("uid");

        typetext = findViewById(R.id.typetext);
        detailtext = findViewById(R.id.detailtext);
        addresstext = findViewById(R.id.addresstext);
        statustext = findViewById(R.id.statustext);
        image = findViewById(R.id.image);
        finishtext = findViewById(R.id.finishtext);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        initData();
    }

    private void initData() {
        FirebaseDatabase.getInstance().getReference().child("Report").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot next : snapshot.getChildren()) {
                            Report value = next.getValue(Report.class);
                            if (value.getReportid().equals(key)){
                                //set value
                                typetext.setText(value.getSort());
                                detailtext.setText(value.getDetail());
                                addresstext.setText(value.getLocationname());
                                statustext.setText(value.getStatus().equals("1")?"Pending":"Finish");
                                statustext.setTextColor(value.getStatus().equals("1")?Color.GREEN
                                        :Color.RED);
                                RequestOptions options = new RequestOptions()
                                        .placeholder(new ColorDrawable(Color.parseColor("#f2f3f5")))
                                        .error(new ColorDrawable(Color.parseColor("#f2f3f5")));
                                Glide.with(ReportDetailActivity.this)
                                        .load(value.getImageurl())
                                        .apply(options)
                                        .into(image);

                                if (value.getStatus().equals("1")&& FirebaseAuth.getInstance().getUid().equals("KXY6ReCTRJdkD4JhQ54CiDogbL03")){
                                    finishtext.setVisibility(View.VISIBLE);
                                }else {
                                    finishtext.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void bac(View view) {
        finish();
    }

    public void doFinish(View view) {
        progressDialog.show();
        Task<Void> voidTask = FirebaseDatabase.getInstance().getReference().child("Report").child(uid).child(key).child("status").setValue("2");

        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                initData();
                progressDialog.dismiss();
                Toast.makeText(ReportDetailActivity.this,"update status suc", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ReportDetailActivity.this,"update status err", Toast.LENGTH_SHORT).show();
            }
        });

    }
}