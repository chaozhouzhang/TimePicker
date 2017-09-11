package me.zhangchaozhou.timepickerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;

import me.zhangchaozhou.timepicker.util.DateUtil;
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
        try {
            Date date = DateUtil.parse(time, TimePicker.FORMAT_YMD_HMS);
            Date now = new Date();
            if (date.after(now)) {
                Log.i("time", DateUtil.getDateInterval(date, now));
            } else {
                Log.i("time", DateUtil.getDateInterval(now, date));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
