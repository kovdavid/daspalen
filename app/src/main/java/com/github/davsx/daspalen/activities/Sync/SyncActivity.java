package com.github.davsx.daspalen.activities.Sync;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.service.Sync.SyncService;
import com.github.lzyzsd.circleprogress.CircleProgress;

import javax.inject.Inject;

public class SyncActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    SyncService syncService;

    private TextView textViewInfo;
    private Button buttonSync;
    private CircleProgress circleProgress;

    private Boolean syncRunning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sync);

        ((DaspalenApplication) getApplication()).getApplicationComponent().inject(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("   Sync");
        actionBar.setIcon(R.mipmap.daspalen_icon);

        textViewInfo = findViewById(R.id.textview_info);
        buttonSync = findViewById(R.id.button_sync);
        circleProgress = findViewById(R.id.progress_bar);
        circleProgress.setMax(100);
        circleProgress.setProgress(0);

        buttonSync.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (syncRunning) {
            textViewInfo.setText("Sync data");
            buttonSync.setText("Sync data");
            syncRunning = false;
        } else {
            textViewInfo.setText("Starting sync");
            buttonSync.setText("Cancel sync");
            if (syncService.startSync()) {
                syncRunning = true;
            } else {
                Toast.makeText(this, "Sync start failed. Check sync settings!", Toast.LENGTH_SHORT).show();
                syncRunning = false;
            }
        }
    }
}
