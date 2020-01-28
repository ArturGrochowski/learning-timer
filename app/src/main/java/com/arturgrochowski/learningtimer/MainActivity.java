package com.arturgrochowski.learningtimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button startResetButton;
    private TextView textView;
    private TextView timeLeftTextView;
    private TextView readWriteTextView;
    private CountDownTimer learnTimer;
    private CountDownTimer brakeTimer;
    private CountDownTimer readTimer;
    private CountDownTimer writeTimer;
    private int learningTime;
    private int readingAndWritingTime = 250 * 1000;
    private MediaPlayer soundForBrake;
    private MediaPlayer soundToLearn;
    private MediaPlayer soundForReading;
    private MediaPlayer soundForWriting;
    private boolean timerRunning = false;
    private boolean readWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignTextView();
        assignButton();
        createLearnTimer();
        createBrakeTimer();
        createReadingTimer();
        createWritingTimer();
        setActionListener();
        timeToString(learningTime);
        textView.setText(R.string.waiting);
        soundForBrake = MediaPlayer.create(this, R.raw.win_alert_1);
        soundToLearn = MediaPlayer.create(this, R.raw.win_alert_2);
        soundForReading = MediaPlayer.create(this, R.raw.win_alert_3);
        soundForWriting = MediaPlayer.create(this, R.raw.win_alert_4);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem itemSwitch = menu.findItem(R.id.mySwitch);
        itemSwitch.setActionView(R.layout.use_switch);
        SwitchCompat sw = menu.findItem(R.id.mySwitch).getActionView().findViewById(R.id.action_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                readWrite = isChecked;
            }
        });

        return true;
    }


    private void assignTextView() {
        textView = findViewById(R.id.textView);
        timeLeftTextView = findViewById(R.id.timeLeftTextView);
        readWriteTextView = findViewById(R.id.readWriteTextView);
    }


    private void assignButton() {
        startResetButton = findViewById(R.id.start);
    }


    private void createLearnTimer() {
        learningTime = 25*60*1000;
        learnTimer = new CountDownTimer(learningTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeToString(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                startBrakeTimer();
            }
        };
    }


    private void startBrakeTimer() {
        learnTimer.cancel();
        brakeTimer.start();
        textView.setText(R.string.brake);
        soundForBrake.start();
        stopReadWriteTimers();
    }


    private void stopReadWriteTimers() {
        readTimer.cancel();
        writeTimer.cancel();
        readWriteTextView.setText(null);
    }


    private void createBrakeTimer() {
        int brakeTime = 5*60*1000;
        brakeTimer = new CountDownTimer(brakeTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeToString(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                startLearnTimer();
            }
        };
    }


    private void startLearnTimer() {
        brakeTimer.cancel();
        learnTimer.start();
        textView.setText(R.string.learn);
        soundToLearn.start();
        if(readWrite){
            startReadTimer();
        }
    }


    private void createReadingTimer() {
        readTimer = new CountDownTimer(readingAndWritingTime, readingAndWritingTime) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                startWriteTimer();
            }
        };
    }


    private void createWritingTimer() {
        writeTimer = new CountDownTimer(readingAndWritingTime, readingAndWritingTime) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startReadTimer();

            }
        };
    }


    private void startWriteTimer() {
        soundForWriting.start();
        readTimer.cancel();
        writeTimer.start();
        readWriteTextView.setText(R.string.write);
        readWriteTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
    }


    private void startReadTimer() {
        soundForReading.start();
        writeTimer.cancel();
        readTimer.start();
        readWriteTextView.setText(R.string.read);
        readWriteTextView.setTextColor(getResources().getColor(R.color.colorGreen));
    }


    private void setActionListener() {
        startResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    resetTimer();
                } else {
                    startTimer();
                }
            }
        });
    }


    private void resetTimer() {
        timerRunning = false;
        learnTimer.cancel();
        brakeTimer.cancel();
        stopReadWriteTimers();
        timeToString(learningTime);
        textView.setText(R.string.waiting);
        startResetButton.setText(R.string.start);
        startResetButton.setBackgroundResource(R.color.colorGreen);

    }


    private void startTimer() {
        timerRunning = true;
        learnTimer.start();
        textView.setText(R.string.learn);
        startResetButton.setText(R.string.reset);
        startResetButton.setBackgroundResource(R.color.colorPrimary);
        if(readWrite){
            readWriteTextView.setText(R.string.read);
            readWriteTextView.setTextColor(getResources().getColor(R.color.colorGreen));
            readTimer.start();
        }
    }


    private void timeToString(long millisUntilFinished){
        int minutes = (int) millisUntilFinished / 60000;
        int seconds = (int) millisUntilFinished % 60000 / 1000;
        String timeLeftText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timeLeftTextView.setText(timeLeftText);
    }
}
