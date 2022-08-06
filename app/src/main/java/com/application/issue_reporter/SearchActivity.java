package com.application.issue_reporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEdit;
    private RecyclerView searchRecyclerview;
    private List<Report> reportList = new ArrayList<>();
    private ReportAdapter reportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchEdit = findViewById(R.id.search_edit);
        searchRecyclerview = findViewById(R.id.search_recyclerview);
        searchRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this, new ArrayList<>());
        searchRecyclerview.setAdapter(reportAdapter);

        reportAdapter.setOnItemClickListener(new ReportAdapter.ItemClickListener() {
            @Override
            public void setOnItemClickListener(int position) {
                startActivity(new Intent(SearchActivity.this, ReportDetailActivity.class)
                        .putExtra("key", reportAdapter.getdata(position).getReportid())
                        .putExtra("uid", reportAdapter.getdata(position).getUid())
                );
            }
        });
    }

    public void bac(View view) {
        finish();
    }

    public void doFind(View view) {
        if (TextUtils.isEmpty(searchEdit.getText().toString())){
            Toast.makeText(SearchActivity.this,"please input you num",Toast.LENGTH_SHORT).show();
            return;
        }
        reportList.clear();
        FirebaseDatabase.getInstance().getReference().child("Report").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot next : snapshot.getChildren()) {
                    Report value = next.getValue(Report.class);
                    if (searchEdit.getText().toString().equals(value.getReportid())){
                        reportList.add(value);
                    }
                }

                if (reportList.size()==0){
                    Toast.makeText(SearchActivity.this,"no data",Toast.LENGTH_SHORT).show();
                }
                reportAdapter.clear();
                reportAdapter.addAll(reportList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}