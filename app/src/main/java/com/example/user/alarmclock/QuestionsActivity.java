package com.example.user.alarmclock;

import android.app.ActivityManager;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

import com.example.user.alarmclock.model.Alarm;

public class QuestionsActivity extends AppCompatActivity {

    ImageButton btn;
    private Realm realm;
    Random r = new Random();
    TextView question;
    EditText answer;
    int numA,numB,numC,numD;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Log.i("QuestionActivity","Inside Question Activity");

        realm = Realm.getDefaultInstance();

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(this, notification);
        mp.start();

        btn = findViewById(R.id.idCheck);
        question = findViewById(R.id.idQuestion);
        answer = findViewById(R.id.idAnswer);

        numA = r.nextInt(10);
        numB = r.nextInt(10);
        numC = r.nextInt(10);
        numD = r.nextInt(10);

        String str = numA + "+" + numB + "-" + numC + "+" + numD;
        question.setText(str);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int ans = Integer.parseInt(answer.getText().toString());
                int actualAns = numA + numB - numC + numD;
                if (answer.getText().toString().length() == 0)
                    Toast.makeText(getApplicationContext(), "Type an Answer!", Toast.LENGTH_SHORT).show();

                else if(Integer.parseInt(answer.getText().toString()) == actualAns) {
                    //deleting time from the database
                    String setTime = getIntent().getStringExtra("Alarm time");
                    realm.executeTransaction(realm -> {
                        RealmResults<Alarm> results = realm.where(Alarm.class).equalTo("alarmTime", setTime).findAll();
                        int req = results.first().getRequestCode();
                        Log.i("deleting alarm","alarm "+ req +" deleted");
                        results.deleteAllFromRealm();
                    });
                    Toast.makeText(QuestionsActivity.this, "Correct answer!", Toast.LENGTH_LONG).show();
                    mp.stop();
                    Intent startAwakeActivity = new Intent(QuestionsActivity.this,MainActivity.class);
                    //To navigate back to the main activity.
                    TaskStackBuilder.create(getApplicationContext()).addNextIntentWithParentStack(startAwakeActivity).startActivities();
                    //Intent intent = new Intent();
                    //setResult(RESULT_OK,intent);
                    finish();
                }
                else
                    Toast.makeText(QuestionsActivity.this,"Wrong answer! Try Again!",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public void onBackPressed() { }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(getApplicationContext(), "You can't Escape!", Toast.LENGTH_SHORT).show();
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
       // activityManager.moveTaskToFront(getTaskId(), 0);
        Log.i("Inside", "onpause");

    }
}
