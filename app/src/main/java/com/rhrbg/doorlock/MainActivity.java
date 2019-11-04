package com.rhrbg.doorlock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView menu1, menu2, menu3, menu4;

    String strTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu1 = (ImageView) findViewById(R.id.menu1);
        menu2 = (ImageView) findViewById(R.id.menu2);
        menu3 = (ImageView) findViewById(R.id.menu3);
        menu4 = (ImageView) findViewById(R.id.menu4);

        menu1.setOnClickListener(this);
        menu2.setOnClickListener(this);
        menu3.setOnClickListener(this);
        menu4.setOnClickListener(this);

        Intent intent = getIntent();
        strTemp = intent.getStringExtra("ID");
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.menu1:
                Intent intent1 = new Intent(MainActivity.this, ScheduleActivity.class);
                intent1.putExtra("ID", strTemp);
                startActivity(intent1);
                break;

            case R.id.menu2:
                Intent intent2 = new Intent(MainActivity.this, OutClassActivity.class);
                intent2.putExtra("ID", strTemp);
                startActivity(intent2);
                break;

            case R.id.menu3:
                Intent intent3 = new Intent(MainActivity.this, LogActivity.class);
                intent3.putExtra("ID", strTemp);
                startActivity(intent3);
                break;

            case R.id.menu4:
                Intent intent4 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent4);
                MainActivity.this.finish(); //  MainActivity Stack에서 제거


                break;
        }
    }


}
