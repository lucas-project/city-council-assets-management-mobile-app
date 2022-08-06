package com.application.issue_reporter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ReportActivity extends AppCompatActivity {

    private TextView reporttext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        reporttext = findViewById(R.id.reporttext);
        reporttext.setText(SPUtils.get(this,"ticket","").toString());
    }

    public void doFinish(View view) {
        Intent intent = new Intent(ReportActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}