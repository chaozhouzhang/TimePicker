package me.zhangchaozhou.timepicker.view;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.zhangchaozhou.timepicker.R;
import me.zhangchaozhou.timepicker.util.DateUtil;

/**
 * Created on 17/9/9 23:28
 */

public class TimePicker extends LinearLayout {


    private TextView mTvYear;
    private TextView mTvMonth;
    private TextView mTvDay;
    private TextView mTvHour;
    private TextView mTvMinute;
    private TextView mTvSecond;

    private PickerView mPvYear;
    private PickerView mPvMonth;
    private PickerView mPvDay;
    private PickerView mPvHour;
    private PickerView mPvMinute;
    private PickerView mPvSecond;


    public static final int MODE_Y = 1;
    public static final int MODE_YM = 2;
    public static final int MODE_YMD = 3;
    public static final int MODE_YMD_H = 4;
    public static final int MODE_YMD_HM = 5;
    public static final int MODE_YMD_HMS = 6;
    public static final int MODE_DEFAULT = MODE_YMD;
    private int mMode = MODE_DEFAULT;


    private final String FORMAT_Y = "yyyy";
    private final String FORMAT_YM = "yyyy-MM";
    private final String FORMAT_YMD = "yyyy-MM-dd";
    private final String FORMAT_YMD_H = "yyyy-MM-dd HH";
    private final String FORMAT_YMD_HM = "yyyy-MM-dd HH:mm";
    private final String FORMAT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";


    private final int MAX_MONTH = 12;
    private final int MIN_MONTH = 1;
    private int MAX_DAY_28 = 28;
    private int MAX_DAY_29 = 29;
    private int MAX_DAY_30 = 30;
    private int MAX_DAY_31 = 31;
    private int MIN_DAY = 1;
    private int MAX_HOUR = 23;
    private int MIN_HOUR = 0;
    private final int MAX_MINUTE = 59;
    private final int MIN_MINUTE = 0;
    private final int MAX_SECOND = 59;
    private final int MIN_SECOND = 0;

    private ArrayList<String> year, month, day, hour, minute, second;
    private int startYear, startMonth, startDay, startHour, startMinute, startSecond,
            endYear, endMonth, endDay, endHour, endMinute, endSecond;
    private boolean spanYear, spanMon, spanDay, spanHour, spanMin, spanSec;
    private Calendar selectedCalender = Calendar.getInstance();
    private final long ANIMATOR_DELAY = 200L;
    private final long CHANGE_DELAY = 90L;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();


    public void setStartCalendar(Date startDate) {
        startCalendar.setTime(startDate);
    }


    public void setEndCalendar(Date endDate) {
        endCalendar.setTime(endDate);
    }


    public TimePicker(Context context) {
        super(context);
    }

    public TimePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.time_picker, this);
        mTvYear = (TextView) view.findViewById(R.id.tv_year);
        mTvMonth = (TextView) view.findViewById(R.id.tv_month);
        mTvDay = (TextView) view.findViewById(R.id.tv_day);
        mTvHour = (TextView) view.findViewById(R.id.tv_hour);
        mTvMinute = (TextView) view.findViewById(R.id.tv_minute);
        mTvSecond = (TextView) view.findViewById(R.id.tv_second);


        mPvYear = (PickerView) view.findViewById(R.id.pv_year);
        mPvMonth = (PickerView) view.findViewById(R.id.pv_month);
        mPvDay = (PickerView) view.findViewById(R.id.pv_day);
        mPvHour = (PickerView) view.findViewById(R.id.pv_hour);
        mPvMinute = (PickerView) view.findViewById(R.id.pv_minute);
        mPvSecond = (PickerView) view.findViewById(R.id.pv_second);


        try {
            parseAttributes(attrs);
        } catch (ParseException e) {
            Toast.makeText(context, "Start time and end time must be correct format.", Toast.LENGTH_LONG).show();
            return;
        }


        if (startCalendar.getTime().getTime() >= endCalendar.getTime().getTime()) {
            Toast.makeText(context, "Start time must be greater than end time.", Toast.LENGTH_LONG).show();
            return;
        }

        initParameter();
        initTimer();
        addListener();


    }

    public TimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    private void parseAttributes(AttributeSet attributeSet) throws ParseException {
        if (attributeSet != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.TimePicker);
            try {
                mMode = typedArray.getInteger(R.styleable.TimePicker_time_mode, MODE_DEFAULT);
                String start = typedArray.getString(R.styleable.TimePicker_start);
                String end = typedArray.getString(R.styleable.TimePicker_end);
                String year = typedArray.getString(R.styleable.TimePicker_year);
                String month = typedArray.getString(R.styleable.TimePicker_month);
                String day = typedArray.getString(R.styleable.TimePicker_day);
                String hour = typedArray.getString(R.styleable.TimePicker_hour);
                String minute = typedArray.getString(R.styleable.TimePicker_minute);
                String second = typedArray.getString(R.styleable.TimePicker_second);
                boolean isLoop = typedArray.getBoolean(R.styleable.TimePicker_loop, true);
                if (!TextUtils.isEmpty(year))
                    mTvYear.setText(year);
                if (!TextUtils.isEmpty(month))
                    mTvMonth.setText(month);
                if (!TextUtils.isEmpty(day))
                    mTvDay.setText(day);
                if (!TextUtils.isEmpty(hour))
                    mTvHour.setText(hour);
                if (!TextUtils.isEmpty(minute))
                    mTvMinute.setText(minute);
                if (!TextUtils.isEmpty(second))
                    mTvSecond.setText(second);
                setMode(mMode);
                setCalendar(start, end);
                setLoop(isLoop);

            } finally {
                typedArray.recycle();
            }
        }
    }

    private void setCalendar(String start, String end) throws ParseException {
        switch (mMode) {
            case MODE_Y:
                setStartCalendar(DateUtil.parse(start, FORMAT_Y));
                setEndCalendar(DateUtil.parse(end, FORMAT_Y));
                break;

            case MODE_YM:
                setStartCalendar(DateUtil.parse(start, FORMAT_YM));
                setEndCalendar(DateUtil.parse(end, FORMAT_YM));
                break;
            case MODE_YMD:
                setStartCalendar(DateUtil.parse(start, FORMAT_YMD));
                setEndCalendar(DateUtil.parse(end, FORMAT_YMD));
                break;
            case MODE_YMD_H:
                setStartCalendar(DateUtil.parse(start, FORMAT_YMD_H));
                setEndCalendar(DateUtil.parse(end, FORMAT_YMD_H));
                break;
            case MODE_YMD_HM:
                setStartCalendar(DateUtil.parse(start, FORMAT_YMD_HM));
                setEndCalendar(DateUtil.parse(end, FORMAT_YMD_HM));
                break;

            case MODE_YMD_HMS:
                setStartCalendar(DateUtil.parse(start, FORMAT_YMD_HMS));
                setEndCalendar(DateUtil.parse(end, FORMAT_YMD_HMS));
                break;
        }
    }


    private void initParameter() {
        startYear = startCalendar.get(Calendar.YEAR);
        startMonth = startCalendar.get(Calendar.MONTH) + 1;
        startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        startMinute = startCalendar.get(Calendar.MINUTE);
        startSecond = startCalendar.get(Calendar.SECOND);

        endYear = endCalendar.get(Calendar.YEAR);
        endMonth = endCalendar.get(Calendar.MONTH) + 1;
        endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
        endMinute = endCalendar.get(Calendar.MINUTE);
        endSecond = endCalendar.get(Calendar.SECOND);

        spanYear = startYear != endYear;
        spanMon = (spanYear) || (startMonth != endMonth);
        spanDay = (spanYear) || (spanMon) || (startDay != endDay);
        spanHour = (spanYear) || (spanMon) || (spanDay) || (startHour != endHour);
        spanMin = (spanYear) || (spanMon) || (spanDay) || (spanHour) || (startMinute != endMinute);
        spanSec = (spanYear) || (spanMon) || (spanDay) || (spanHour) || (spanMin) || (startSecond != endSecond);

        selectedCalender.setTime(startCalendar.getTime());
    }

    private void initTimer() {
        initArrayList();

        if (spanYear) {
            for (int i = startYear; i <= endYear; i++) {
                year.add(String.valueOf(i));
            }
        } else {
            year.add(formatTimeUnit(startYear));
        }

        if (spanMon) {
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                month.add(formatTimeUnit(i));
            }
        } else {
            month.add(formatTimeUnit(startMonth));
        }
        if (spanDay) {
            int maxDay = MAX_DAY_31;
            switch (startMonth) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    maxDay = MAX_DAY_31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    maxDay = MAX_DAY_30;
                    break;
                case 2:
                    if ((startYear % 4 == 0) && (startYear % 100 != 0) || (startYear % 400 == 0)) {
                        maxDay = MAX_DAY_29;
                    } else {
                        maxDay = MAX_DAY_28;
                    }
                    break;
            }
            for (int i = startDay; i <= maxDay; i++) {
                day.add(formatTimeUnit(i));
            }
        } else {
            day.add(formatTimeUnit(startDay));
        }
        if (spanHour) {
            for (int i = startHour; i <= MAX_HOUR; i++) {
                hour.add(formatTimeUnit(i));
            }
        } else {
            hour.add(formatTimeUnit(startHour));
        }
        if (spanMin) {
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
        } else {
            minute.add(formatTimeUnit(startMinute));
        }
        if (spanSec) {
            for (int i = startSecond; i <= MAX_SECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        } else {
            second.add(formatTimeUnit(startSecond));
        }

        loadComponent();

    }


    /**
     * 格式化数字
     *
     * @param unit
     * @return
     */
    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void initArrayList() {
        if (year == null) year = new ArrayList<>();
        if (month == null) month = new ArrayList<>();
        if (day == null) day = new ArrayList<>();
        if (hour == null) hour = new ArrayList<>();
        if (minute == null) minute = new ArrayList<>();
        if (second == null) second = new ArrayList<>();
        year.clear();
        month.clear();
        day.clear();
        hour.clear();
        minute.clear();
        second.clear();
    }


    private void addListener() {
        mPvYear.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.YEAR, Integer.parseInt(text));
                monthChange();


            }
        });
        mPvMonth.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, 1);
                selectedCalender.set(Calendar.MONTH, Integer.parseInt(text) - 1);
                dayChange();


            }
        });
        mPvDay.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(text));
                hourChange();

            }
        });
        mPvHour.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(text));
                minuteChange();


            }
        });
        mPvMinute.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.MINUTE, Integer.parseInt(text));

                secondChange();

            }
        });

        mPvSecond.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.SECOND, Integer.parseInt(text));
            }
        });

    }


    private void loadComponent() {
        mPvYear.setData(year);
        mPvMonth.setData(month);
        mPvDay.setData(day);
        mPvHour.setData(hour);
        mPvMinute.setData(minute);
        mPvSecond.setData(second);


        /**
         * 初始化选中状态
         */
        mPvYear.setSelected(0);
        mPvMonth.setSelected(0);
        mPvDay.setSelected(0);
        mPvHour.setSelected(0);
        mPvMinute.setSelected(0);
        mPvSecond.setSelected(0);

        executeScroll();
    }

    private void executeScroll() {
        mPvYear.setCanScroll(year.size() > 1);
        mPvMonth.setCanScroll(month.size() > 1);
        mPvDay.setCanScroll(day.size() > 1);
        mPvHour.setCanScroll(hour.size() > 1);
        mPvMinute.setCanScroll(minute.size() > 1);
        mPvSecond.setCanScroll(second.size() > 1);
    }

    private void monthChange() {

        month.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        if (selectedYear == startYear) {
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                month.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear) {
            for (int i = MIN_MONTH; i <= endMonth; i++) {
                month.add(formatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= MAX_MONTH; i++) {
                month.add(formatTimeUnit(i));
            }
        }
        selectedCalender.set(Calendar.MONTH, Integer.parseInt(month.get(0)) - 1);
        mPvMonth.setData(month);
        mPvMonth.setSelected(0);
        executeAnimator(ANIMATOR_DELAY, mPvMonth);

        mPvMonth.postDelayed(new Runnable() {
            @Override
            public void run() {
                dayChange();
            }
        }, CHANGE_DELAY);

    }

    private void dayChange() {

        day.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        if (selectedYear == startYear && selectedMonth == startMonth) {
            for (int i = startDay; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth) {
            for (int i = MIN_DAY; i <= endDay; i++) {
                day.add(formatTimeUnit(i));
            }
        } else {
            for (int i = 1; i <= selectedCalender.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                day.add(formatTimeUnit(i));
            }
        }
        selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(0)));
        mPvDay.setData(day);
        mPvDay.setSelected(0);
        executeAnimator(ANIMATOR_DELAY, mPvDay);

        mPvDay.postDelayed(new Runnable() {
            @Override
            public void run() {
                hourChange();
            }
        }, CHANGE_DELAY);
    }

    private void hourChange() {
        hour.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);

        if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay) {
            for (int i = startHour; i <= MAX_HOUR; i++) {
                hour.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay) {
            for (int i = MIN_HOUR; i <= endHour; i++) {
                hour.add(formatTimeUnit(i));
            }
        } else {

            for (int i = MIN_HOUR; i <= MAX_HOUR; i++) {
                hour.add(formatTimeUnit(i));
            }

        }
        selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.get(0)));
        mPvHour.setData(hour);
        mPvHour.setSelected(0);
        executeAnimator(ANIMATOR_DELAY, mPvHour);
        mPvHour.postDelayed(new Runnable() {
            @Override
            public void run() {
                minuteChange();
            }
        }, CHANGE_DELAY);

    }

    private void minuteChange() {
        minute.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
        int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);

        if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour) {
            for (int i = startMinute; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour) {
            for (int i = MIN_MINUTE; i <= endMinute; i++) {
                minute.add(formatTimeUnit(i));
            }
        } else {
            for (int i = MIN_MINUTE; i <= MAX_MINUTE; i++) {
                minute.add(formatTimeUnit(i));
            }
        }
        selectedCalender.set(Calendar.MINUTE, Integer.parseInt(minute.get(0)));
        mPvMinute.setData(minute);
        mPvMinute.setSelected(0);
        executeAnimator(ANIMATOR_DELAY, mPvMinute);

        mPvMinute.postDelayed(new Runnable() {
            @Override
            public void run() {
                secondChange();
            }
        }, CHANGE_DELAY);


    }

    private void secondChange() {
        second.clear();
        int selectedYear = selectedCalender.get(Calendar.YEAR);
        int selectedMonth = selectedCalender.get(Calendar.MONTH) + 1;
        int selectedDay = selectedCalender.get(Calendar.DAY_OF_MONTH);
        int selectedHour = selectedCalender.get(Calendar.HOUR_OF_DAY);
        int selectedMinute = selectedCalender.get(Calendar.MINUTE);
        if (selectedYear == startYear && selectedMonth == startMonth && selectedDay == startDay && selectedHour == startHour && selectedMinute == startMinute) {
            for (int i = startSecond; i <= MAX_SECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        } else if (selectedYear == endYear && selectedMonth == endMonth && selectedDay == endDay && selectedHour == endHour && selectedMinute == endMinute) {
            for (int i = MIN_SECOND; i <= endSecond; i++) {
                second.add(formatTimeUnit(i));
            }
        } else {
            for (int i = MIN_SECOND; i <= MAX_SECOND; i++) {
                second.add(formatTimeUnit(i));
            }
        }
        selectedCalender.set(Calendar.SECOND, Integer.parseInt(second.get(0)));
        mPvSecond.setData(second);
        mPvSecond.setSelected(0);
        executeAnimator(ANIMATOR_DELAY, mPvSecond);

        executeScroll();


    }

    private void executeAnimator(long ANIMATOR_DELAY, View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f,
                0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(ANIMATOR_DELAY).start();
    }


    public void setMode(int mode) {
        switch (mode) {
            case MODE_Y:
                mPvMonth.setVisibility(GONE);
                mTvMonth.setVisibility(GONE);

                mPvDay.setVisibility(GONE);
                mTvDay.setVisibility(GONE);

                mPvHour.setVisibility(View.GONE);
                mTvHour.setVisibility(View.GONE);

                mTvMinute.setVisibility(View.GONE);
                mPvMinute.setVisibility(View.GONE);

                mPvSecond.setVisibility(View.GONE);
                mTvSecond.setVisibility(View.GONE);
                break;

            case MODE_YM:
                mPvDay.setVisibility(GONE);
                mTvDay.setVisibility(GONE);

                mPvHour.setVisibility(View.GONE);
                mTvHour.setVisibility(View.GONE);

                mTvMinute.setVisibility(View.GONE);
                mPvMinute.setVisibility(View.GONE);

                mPvSecond.setVisibility(View.GONE);
                mTvSecond.setVisibility(View.GONE);
                break;
            case MODE_YMD:
                mPvHour.setVisibility(View.GONE);
                mTvHour.setVisibility(View.GONE);

                mTvMinute.setVisibility(View.GONE);
                mPvMinute.setVisibility(View.GONE);

                mPvSecond.setVisibility(View.GONE);
                mTvSecond.setVisibility(View.GONE);
                break;


            case MODE_YMD_H:
                mTvMinute.setVisibility(View.GONE);
                mPvMinute.setVisibility(View.GONE);

                mPvSecond.setVisibility(View.GONE);
                mTvSecond.setVisibility(View.GONE);
                break;
            case MODE_YMD_HM:
                mPvSecond.setVisibility(View.GONE);
                mTvSecond.setVisibility(View.GONE);
                break;

            case MODE_YMD_HMS:
                break;

        }
    }

    public void setLoop(boolean isLoop) {
        this.mPvYear.setIsLoop(isLoop);
        this.mPvMonth.setIsLoop(isLoop);
        this.mPvDay.setIsLoop(isLoop);
        this.mPvHour.setIsLoop(isLoop);
        this.mPvMinute.setIsLoop(isLoop);
        this.mPvSecond.setIsLoop(isLoop);
    }
}