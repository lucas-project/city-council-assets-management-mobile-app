package com.application.issue_reporter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SelectionActivity extends AppCompatActivity {

    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        radioGroup = findViewById(R.id.rg);
        radioGroup.check(R.id.rd1);
    }

    public void bac(View view) {
        finish();
    }

    public void doNext(View view) {
        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(checkedRadioButtonId);
        String s = radioButton.getText().toString();
        SPUtils.put(this,"sort",s);
        startActivity(new Intent(SelectionActivity.this,DetailActivity.class));
    }
}