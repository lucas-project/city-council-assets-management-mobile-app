package com.application.issue_reporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView reportRecyclerView;
    private ReportAdapter reportAdapter;
    private DatabaseReference reportDatabaseReference;
    private TextView homeEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        reportRecyclerView = findViewById(R.id.report_recyclerView);
        homeEdit = findViewById(R.id.home_edit);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this, new ArrayList<>());
        reportRecyclerView.setAdapter(reportAdapter);


        reportAdapter.setOnItemClickListener(new ReportAdapter.ItemClickListener() {
            @Override
            public void setOnItemClickListener(int position) {
                startActivity(new Intent(HomeActivity.this, ReportDetailActivity.class)
                        .putExtra("key", reportAdapter.getdata(position).getReportid())
                        .putExtra("uid", reportAdapter.getdata(position).getUid())
                );
            }
        });
        reportDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Report")
                .child(FirebaseAuth.getInstance().getUid());
        reportDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                List<Report> reportList = new ArrayList<>();
                while (iterator.hasNext()){
                    DataSnapshot next = iterator.next();
                    Report value = next.getValue(Report.class);
                    reportList.add(value);
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
                reportAdapter.addAll(reportList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        homeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,SearchActivity.class));
            }
        });
    }


    public void doNew(View view) {
        startActivity(new Intent(HomeActivity.this,SelectionActivity.class));
    }

    public void doLogout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        finish();
    }
}