package com.application.issue_reporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DistanceActivity extends AppCompatActivity {

    private DatabaseReference reportDatabaseReference;
    private RecyclerView reportRecyclerView;
    private DistanceAdapter distanceAdapter;
    private TextView locationinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        locationinfo = findViewById(R.id.locationinfo);
        locationinfo.setText(SPUtils.get(this,"address","").toString());
        reportRecyclerView = findViewById(R.id.report_recyclerView);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        distanceAdapter = new DistanceAdapter(this, new ArrayList<>());
        reportRecyclerView.setAdapter(distanceAdapter);
        reportDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Report");
        reportDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ReportDistance> reportArrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        Report value = next.getValue(Report.class);
                        double distance = getDistance(Double.parseDouble(SPUtils.get(DistanceActivity.this,"lat","0.0").toString()),
                                Double.parseDouble(SPUtils.get(DistanceActivity.this,"lon","0.0").toString()),
                                Double.parseDouble(value.getLatitude().toString()),
                                Double.parseDouble(value.getLongitude().toString())
                        );
                        if (distance <= 10d) {
                            reportArrayList.add(new ReportDistance(value,distance));
                        }
                    }
                }
                distanceAdapter.clear();
                distanceAdapter.addAll(reportArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[3];
        Location.distanceBetween(lat1 / 1E6, lon1 / 1E6, lat2 / 1E6, lon2 / 1E6, results);
        return results[0];
    }

    public void bac(View view) {
        finish();
    }

    public void doNext(View view) {
        startActivity(new Intent(DistanceActivity.this,ImageActivity.class));
    }

    public void doFinish(View view) {
        Intent intent = new Intent(DistanceActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}