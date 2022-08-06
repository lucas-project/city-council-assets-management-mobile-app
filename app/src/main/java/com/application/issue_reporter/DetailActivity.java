package com.application.issue_reporter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    private EditText editDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        editDetail = findViewById(R.id.edit_detail);
    }

    public void doNext(View view) {
        if (TextUtils.isEmpty(editDetail.getText().toString())){
            Toast.makeText(DetailActivity.this,"empty data!",Toast.LENGTH_SHORT).show();
            return;
        }
        SPUtils.put(this,"detail",editDetail.getText().toString());
        startActivity(new Intent(DetailActivity.this, MapActivity.class)
                .putExtra("sort",getIntent().getStringExtra("sort"))
        );

    }

    public void bac(View view) {
        finish();
    }
}