package me.zhangchaozhou.timepickerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import me.zhangchaozhou.timepicker.view.TimePicker;

public class MainActivity extends AppCompatActivity implements TimePicker.OnSelectedListener {


    TimePicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimePicker = (TimePicker) findViewById(R.id.pt);
        mTimePicker.setOnSelectedListener(this);
    }

    @Override
    public void onSelected(String time) {
        Log.i("time", time);
    }
}
