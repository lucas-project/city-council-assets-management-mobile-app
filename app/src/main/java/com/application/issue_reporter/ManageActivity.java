package com.application.issue_reporter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ManageActivity extends AppCompatActivity {

    private RecyclerView reportRecyclerView;
    private ReportAdapter reportAdapter;
    private DatabaseReference reportDatabaseReference;
    private TextView homeEdit;
    private View homeAdd;
    private TextView hometitle;
    List<Report> reportList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        hometitle = findViewById(R.id.hometitle);
        hometitle.setText("Manage Reports");
        reportRecyclerView = findViewById(R.id.report_recyclerView);
        homeEdit = findViewById(R.id.home_edit);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this, new ArrayList<>());
        reportRecyclerView.setAdapter(reportAdapter);


        reportAdapter.setOnItemClickListener(new ReportAdapter.ItemClickListener() {
            @Override
            public void setOnItemClickListener(int position) {
                startActivity(new Intent(ManageActivity.this, ReportDetailActivity.class)
                        .putExtra("key", reportAdapter.getdata(position).getReportid())
                        .putExtra("uid", reportAdapter.getdata(position).getUid())
                );
            }
        });
        reportDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Report");
        reportDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot nextUid : snapshot.getChildren()) {
                    for (DataSnapshot next : nextUid.getChildren()) {
                        Report value = next.getValue(Report.class);
                        reportList.add(value);
                    }
                }
                Collections.sort(reportList, new Comparator<Report>() {
                    @Override
                    public int compare(Report report, Report t1) {

                        if (report.getTime() == null)
                            report.setTime("0");
                        if (t1.getTime() == null)
                            t1.setTime("0");
                        return (int)(Long.parseLong(report.getTime()) - Long.parseLong(t1.getTime()));
                    }
                });
                reportAdapter.clear();

                if (tempSting.equalsIgnoreCase("all")){
                    reportAdapter.addAll(reportList);
                }else {
                    List<Report> reports = new ArrayList<>();
                    for (Report next : reportList) {
                        if (next.getSort().equalsIgnoreCase(tempSting)) {
                            reports.add(next);
                        }
                    }
                    reportAdapter.addAll(reports);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        homeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageActivity.this,SearchActivity.class));
            }
        });
    }

    String[] items3 = new String[]{"all", "street lighting", "waste and recycling services"
            , "street maintenance","public behavior issues", "tree maintenance", "legal parking"
            , "pest control", "graffiti"};

    private String tempSting = "all";
    public void select(View view){

        AlertDialog alertDialog3 = new AlertDialog.Builder(this)
                .setTitle("Select Category")
                .setIcon(R.mipmap.ic_launcher)
                .setItems(items3, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tempSting = items3[i];

                        if (tempSting.equalsIgnoreCase("all")){
                            reportAdapter.clear();
                            reportAdapter.addAll(reportList);
                        }else {
                            reportAdapter.clear();
                            List<Report> reports = new ArrayList<>();
                            for (Report next : reportList) {
                                if (next.getSort().equalsIgnoreCase(tempSting)) {
                                    reports.add(next);
                                }
                            }
                            reportAdapter.addAll(reports);
                        }

                    }
                })
                .create();
        alertDialog3.show();

    }

    public void doNew(View view) {
        startActivity(new Intent(ManageActivity.this,SelectionActivity.class));
    }

    public void doLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ManageActivity.this,LoginActivity.class));
        finish();
    }
}