package com.example.user.alarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.example.user.alarmclock.model.Alarm;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btn;
    private Realm realm;
    private RealmResults<Alarm> results;
    private Alarm alarm;
    static PendingIntent pendingIntent;
    static AlarmManager alarmManager;
    private int requestCode;
    private String timeset;
    private int PRIMARY_KEY;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<ListItem> listItems;
    Calendar calSet;
    private int RQS_1;
    private int hour, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting realm object to querry
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm -> results = realm.where(Alarm.class).findAll());

        // data to populate the RecyclerView with
        //alarms = new ArrayList<>();

        recyclerView = findViewById(R.id.idRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //listItems = new ArrayList<>();

        adapter = new MyAdapter(this,results,realm);
        recyclerView.setAdapter(adapter);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        btn = findViewById(R.id.idAdd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting the calender instance
                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                min = c.get(Calendar.MINUTE);
                //TimePickerDialog pops up to pick the time for the new alarm
                final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int min) {
                        timeset = String.format("%02d", hour) + ":" + String.format("%02d", min);
                        setAlarm(hour, min, timeset);
                    }
                };
                TimePickerDialog alarm_timepicker = new TimePickerDialog(MainActivity.this, listener, hour, min, true);
                alarm_timepicker.show();
            }
        });

        adapter.setOnClickTrashIconListener(setTime -> {
            realm.executeTransaction(realm -> {
                RealmResults<Alarm> results = realm.where(Alarm.class).equalTo("alarmTime", setTime).findAll();
                requestCode = results.first().getRequestCode();
                results.deleteAllFromRealm();
            });
            Log.i("Alarm deleted", setTime);
            Log.i("P.I. deleted", String.valueOf(requestCode));
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, 0);
            mPendingIntent.cancel();
            alarmManager.cancel(mPendingIntent);
        });
    }

    //creates pending Intent and fires the alarm at specified time
    @SuppressLint("NewApi")
    private void setAlarm(int hour,int min,String setTime) {
        /*AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this,QuestionsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),12553,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeInMillis,pendingIntent);
        Log.i("Alarm Manager","Intent");*/

        //create pending intent
        Intent alarmIntent = new Intent(getBaseContext(), QuestionsActivity.class);
        alarmIntent.putExtra("Alarm time", setTime);
        pendingIntent = PendingIntent.getActivity(getBaseContext(), requestCode, alarmIntent, 0);

        //Computing Primary key and Request code
        PRIMARY_KEY = Integer.parseInt(String.format("%02d", hour)+String.format("%02d", min));
        RQS_1 = PRIMARY_KEY;

        //adding alarm to the database
        realm.executeTransaction((Realm realm) -> {
            alarm = realm.createObject(Alarm.class, PRIMARY_KEY);
            alarm.setAlarmTime(setTime);
            alarm.setRequestCode(RQS_1);
        });
        adapter.updateAdapter(alarm);
        Log.i("new alarm","alarm "+ PRIMARY_KEY + " added");

        //set up alarmManager to fire the alarm
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calNow = Calendar.getInstance();

        calSet = (Calendar) calNow.clone();
        calSet.set(Calendar.HOUR_OF_DAY, hour);
        calSet.set(Calendar.MINUTE, min);
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);
        if (calSet.compareTo(calNow) <= 0)
            calSet.add(Calendar.DATE, 1);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);
        Log.i("PendingIntent","created");
        //Toast.makeText(this, "Alarm set to the time specified", Toast.LENGTH_SHORT).show();


    }
    /*protected void onActivityResult(int reqCode,int respCode,Intent msg){
        if(reqCode == 12553){
            if(respCode==RESULT_OK){
                Toast.makeText(this,"Correct!", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
