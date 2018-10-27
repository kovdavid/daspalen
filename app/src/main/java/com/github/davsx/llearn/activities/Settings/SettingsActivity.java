package com.github.davsx.llearn.activities.Settings;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.*;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.WordOfTheDay.WordOfTheDayAlarmService;
import com.github.davsx.llearn.service.WordOfTheDay.WordOfTheDaySettingsService;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {

    @Inject
    SharedPreferences sharedPreferences;

    private WordOfTheDaySettingsService wordOfTheDaySettingsService;

    private CheckBox checkBoxWordOfTheDayEnable;
    private Button buttonNotificationFrom;
    private Button buttonNotificationTo;
    private Button buttonNotificationInterval;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("   Settings");
        actionBar.setIcon(R.mipmap.ic_launcher_icon);

        wordOfTheDaySettingsService = new WordOfTheDaySettingsService(sharedPreferences);

        checkBoxWordOfTheDayEnable = findViewById(R.id.checkbox_word_of_the_day_enable);
        buttonNotificationFrom = findViewById(R.id.button_notification_from);
        buttonNotificationTo = findViewById(R.id.button_notification_to);
        buttonNotificationInterval = findViewById(R.id.button_notification_interval);

        buttonNotificationFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationFromTimePickerDialog();
            }
        });
        buttonNotificationTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationToTimePickerDialog();
            }
        });
        buttonNotificationInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationIntervalTimePickerDialog();
            }
        });
        checkBoxWordOfTheDayEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wordOfTheDaySettingsService.setNotificationEnabled(isChecked);
            }
        });

        updateViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WordOfTheDayAlarmService.setNextAlarm(this, sharedPreferences);
    }

    private void showNotificationIntervalTimePickerDialog() {
        TimePickerDialog dialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setNotificationInterval(hourOfDay, 0);
            }
        }, getNotificationIntervalHour(), getNotificationIntervalMinute(), true);
        dialog.show();
    }

    private void showNotificationToTimePickerDialog() {
        TimePickerDialog dialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setNotificationTo(hourOfDay, 0);
            }
        }, getNotificationToHour(), getNotificationToMinute(), true);
        dialog.show();
    }

    private void showNotificationFromTimePickerDialog() {
        TimePickerDialog dialog = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                setNotificationFrom(hourOfDay, minute);
            }
        }, getNotificationFromHour(), getNotificationFromMinute(), true);
        dialog.show();
    }

    private void setNotificationInterval(int hour, int minute) {
        wordOfTheDaySettingsService.setNotificationInterval(hour, minute);
        updateViews();
    }

    private void setNotificationFrom(int hour, int minute) {
        Integer notificationToHour = getNotificationToHour();
        Integer notificationToMinute = getNotificationToMinute();
        String notificationToString = String.format("%02d:%02d", notificationToHour, notificationToMinute);

        if (hour < notificationToHour || (hour == notificationToHour && minute < notificationToMinute)) {
            wordOfTheDaySettingsService.setNotificationFrom(hour, minute);
        } else {
            Toast.makeText(this, "Notification start hour must be smaller than " + notificationToString,
                    Toast.LENGTH_SHORT).show();
        }

        updateViews();
    }

    private void setNotificationTo(int hour, int minute) {
        int notificationFromHour = getNotificationFromHour();
        int notificationFromMinute = getNotificationFromMinute();
        String notificationFromString = String.format("%02d:%02d", notificationFromHour, notificationFromMinute);

        if (hour > notificationFromHour || (hour == notificationFromHour && minute > notificationFromMinute)) {
            wordOfTheDaySettingsService.setNotificationTo(hour, minute);
        } else {
            Toast.makeText(this, "Notification end hour must be bigger than " + notificationFromString,
                    Toast.LENGTH_SHORT).show();
        }

        updateViews();
    }

    private void updateViews() {
        String notificationFromString = String.format("%02d:%02d", getNotificationFromHour(),
                getNotificationFromMinute());
        buttonNotificationFrom.setText(notificationFromString);

        String notificationToString = String.format("%02d:%02d", getNotificationToHour(), getNotificationToMinute());
        buttonNotificationTo.setText(notificationToString);

        String notificationIntervalString = String.format("%02d:%02d", getNotificationIntervalHour(),
                getNotificationIntervalMinute());
        buttonNotificationInterval.setText(notificationIntervalString);

        checkBoxWordOfTheDayEnable.setChecked(wordOfTheDaySettingsService.getNotificationEnabled());
    }

    private Integer getNotificationFromHour() {
        return wordOfTheDaySettingsService.getNotificationFromHour();
    }

    private Integer getNotificationFromMinute() {
        return wordOfTheDaySettingsService.getNotificationFromMinute();
    }

    private Integer getNotificationIntervalHour() {
        return wordOfTheDaySettingsService.getNotificationIntervalHour();
    }

    private Integer getNotificationIntervalMinute() {
        return wordOfTheDaySettingsService.getNotificationIntervalMinute();
    }

    private Integer getNotificationToHour() {
        return wordOfTheDaySettingsService.getNotificationToHour();
    }

    private Integer getNotificationToMinute() {
        return wordOfTheDaySettingsService.getNotificationToMinute();
    }
}
