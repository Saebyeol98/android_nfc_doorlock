package com.rhrbg.doorlock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class OutClassActivity extends AppCompatActivity implements View.OnClickListener, OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    CalendarDay calendarDay;

    ArrayAdapter adapter_st = null;
    ArrayAdapter adapter_et = null;
    ArrayAdapter adapter_class = null;
    TextView tvDate;
    String Year, Month, Day;
    Spinner spinner1 = null;
    Spinner spinner2 = null;
    Spinner spinner3 = null;

    MaterialCalendarView calendar;
    TextView btn1;
    String Id = null, strDate = null;

    //전역 변수..................
    String m_startTime = null, m_endTime = null, m_class = null;
    int m_type = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outclass);
        calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
        tvDate = (TextView) findViewById(R.id.tvDate);
        btn1 = (TextView) findViewById(R.id.btn1);

        adapter_st = ArrayAdapter.createFromResource(OutClassActivity.this, R.array.start_time_array, R.layout.spinner_setting);
        adapter_st.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_et = ArrayAdapter.createFromResource(OutClassActivity.this, R.array.end_time_array, R.layout.spinner_setting);
        adapter_et.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter_class = ArrayAdapter.createFromResource(OutClassActivity.this, R.array.class_array, R.layout.spinner_setting);
        adapter_class.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);

        Intent intent = getIntent();
        Id = intent.getStringExtra("ID");

        calendar.setOnDateChangedListener(OutClassActivity.this);
        calendar.setOnMonthChangedListener(OutClassActivity.this);

        spinner1.setAdapter(adapter_st);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                print(view, position);
                m_startTime = spinner1.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner2.setAdapter(adapter_et);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                print(view, position);
                m_endTime = spinner2.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner3.setAdapter(adapter_class);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                print(view, position);
                m_class = spinner3.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("내용보자", Id + strDate + m_startTime + m_endTime + m_class);
                if (Id == null || strDate == null || m_startTime == null || m_endTime == null || m_class == null) {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(OutClassActivity.this);
                    alt_bld.setMessage("선택 항목중에 비어있는게 있습니다 확인해주세요").setCancelable(
                            false).setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    // Title for AlertDialog
                    alert.setTitle("A.N.D");
                    // Icon for AlertDialog
                    alert.setIcon(R.drawable.icon);
                    alert.show();
//                    Toast.makeText(getApplicationContext(), "선택 항목중에 비어있는게 있습니다 확인해주세요", Toast.LENGTH_LONG).show();
                } else {
                    OutClassServer oc = new OutClassServer(m_Handler, Id, Year, Month, Day, m_startTime, m_endTime, m_class, m_type);
                    oc.start();
                    Log.d("보낼 내용", Id + " " + strDate + " " + m_startTime + " " + m_endTime + " " + m_class + " " + m_type);
//                    Toast.makeText(getApplicationContext(), "test" + m_class + "신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
//                    Toast toast = Toast.makeText(OutClassActivity.this, sTime +" ~ "+ eTime +"\n"+strClass+ "가 신청되었습니다", Toast.LENGTH_SHORT);
//                    toast.show();
                }

            }
        });


//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                Year = String.valueOf(year);
//                Month = String.valueOf(month + 1);
//                Day = String.valueOf(dayOfMonth);
//
//                strDate = Year + "." + Month + "." + Day;
//
//                tvDate.setText("");
//                tvDate.append(Year);
//                tvDate.append("년");
//                tvDate.append(" ");
//                tvDate.append(Month);
//                tvDate.append("월");
//                tvDate.append(" ");
//                tvDate.append(Day);
//                tvDate.append("일");
//            }
//        });

    }

    public void onClick(View v) {

        switch (v.getId()) {

        }
    }

    public void print(View v, int position) {
        Spinner sp = (Spinner) findViewById(R.id.spinner1);
        String res = "";
        if (sp.getSelectedItemPosition() > 0) {
            res = (String) sp.getAdapter().getItem(sp.getSelectedItemPosition());
        }
    }

    Handler m_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OutClassServer.ServerSucces:
                    if (String.valueOf(msg.arg1).equals("1")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(OutClassActivity.this);
                        dialog.setMessage(m_class + "신청이 완료되었습니다.").setCancelable(
                                true).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = dialog.create();
                        // Title for AlertDialog
                        alert.setTitle("A.N.D");
                        // Icon for AlertDialog
                        alert.setIcon(R.drawable.icon);
                        alert.show();
//                        Toast.makeText(getApplicationContext(), m_class + "신청이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(OutClassActivity.this);
                        dialog.setMessage("서버와의 연결을 확인해주시고 다시 시도해주제요").setCancelable(
                                true).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = dialog.create();
                        // Title for AlertDialog
                        alert.setTitle("A.N.D");
                        // Icon for AlertDialog
                        alert.setIcon(R.drawable.icon);
                        alert.show();
//                        Toast.makeText(getApplicationContext(), "서버와의 연결을 확인해주시고 다시 시도해주제요", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    private String getSelectedDatesString() {
        calendarDay = calendar.getSelectedDate();
        if (calendarDay == null) {
            return "No Selection";
        }
        return FORMATTER.format(calendarDay.getYear() + calendarDay.getMonth() + calendarDay.getDay());
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

        strDate = getSelectedDatesString();
        Year = String.valueOf(calendarDay.getYear());
        Month = String.valueOf(calendarDay.getMonth() + 1);
        Day = String.valueOf(calendarDay.getDay());
        strDate = Year + "년 " + Month + "월 " + Day + "일";

        tvDate.setText(strDate);

        Toast.makeText(getApplicationContext(), Year + " " + Month + " " + Day, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    public CalendarDay getCalendarDay() {
        return calendarDay;
    }

}

