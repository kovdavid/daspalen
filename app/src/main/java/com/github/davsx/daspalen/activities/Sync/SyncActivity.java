package com.github.davsx.daspalen.activities.Sync;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.service.Sync.SyncService;

import javax.inject.Inject;

public class SyncActivity extends AppCompatActivity {

    @Inject
    SyncService syncService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sync);

        ((DaspalenApplication) getApplication()).getApplicationComponent().inject(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("   Sync");
        actionBar.setIcon(R.mipmap.daspalen_icon);
    }

}
