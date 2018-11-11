package com.github.davsx.daspalen.activities.BackupCreate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.service.BackupCreate.BackupCreateService;
import com.github.lzyzsd.circleprogress.CircleProgress;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.OutputStream;

public class BackupCreateActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    BackupCreateService backupCreateService;

    private CircleProgress progressBar;
    private TextView textViewInfo;
    private Button buttonCreate;

    private BackupStatus backupStatus;

    private AsyncTask<OutputStream, AsyncProgress, Boolean> backupTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_create);

        ((DaspalenApplication) getApplication()).getApplicationComponent().inject(this);

        progressBar = findViewById(R.id.progress_bar);
        textViewInfo = findViewById(R.id.textview_info);
        buttonCreate = findViewById(R.id.button_export);
        buttonCreate.setOnClickListener(this);

        backupStatus = BackupStatus.BACKUP_NOT_STARTED;

        updateViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (backupTask != null) {
            backupTask.cancel(true);
        }
    }

    private void updateViews() {
        if (backupStatus.equals(BackupStatus.BACKUP_NOT_STARTED)) {
            textViewInfo.setText("Create backup");
            buttonCreate.setText("Create backup");
        } else if (backupStatus.equals(BackupStatus.BACKUP_RUNNING)) {
            buttonCreate.setText("Cancel backup");
        } else if (backupStatus.equals(BackupStatus.BACKUP_CANCELLED)) {
            textViewInfo.setText("Backup cancelled");
            buttonCreate.setText("Create backup");
        } else if (backupStatus.equals(BackupStatus.BACKUP_CREATED)) {
            textViewInfo.setText("Backup created");
            buttonCreate.setText("Create backup");
        }
    }

    @Override
    public void onClick(View v) {
        if (backupStatus.equals(BackupStatus.BACKUP_RUNNING)) {
            if (backupTask != null) {
                backupTask.cancel(true);
            }
            backupStatus = BackupStatus.BACKUP_CANCELLED;
            updateViews();
        } else {
            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("application/zip");
            i.putExtra(Intent.EXTRA_TITLE, backupCreateService.getDefaultFileName());
            startActivityForResult(i, DaspalenConstants.REQUEST_CODE_CREATE_DOCUMENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DaspalenConstants.REQUEST_CODE_CREATE_DOCUMENT) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        createBackup(uri);
                    }
                }
            }
        }
    }

    private void createBackup(Uri uri) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not create file", Toast.LENGTH_SHORT).show();
            backupStatus = BackupStatus.BACKUP_CANCELLED;
            updateViews();
            return;
        }

        backupTask = new BackupCreateTask().execute(outputStream);
    }

    private enum BackupStatus {
        BACKUP_NOT_STARTED,
        BACKUP_RUNNING,
        BACKUP_CANCELLED,
        BACKUP_CREATED
    }

    private class AsyncProgress {
        Integer progress;
        String status;

        AsyncProgress(Integer progress, String status) {
            this.progress = progress;
            this.status = status;
        }
    }

    private class BackupCreateTask extends AsyncTask<OutputStream, AsyncProgress, Boolean> {
        @Override
        protected Boolean doInBackground(OutputStream... outputStreams) {
            backupCreateService.startBackup(outputStreams[0]);

            boolean run;
            do {
                run = backupCreateService.doNextChunk();
                publishProgress(new AsyncProgress(backupCreateService.getCurrentProgress(),
                        backupCreateService.getStatus()));
            } while (run);

            return backupCreateService.getFinished();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            backupStatus = BackupStatus.BACKUP_RUNNING;
            progressBar.setMax(100);

            updateViews();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            backupStatus = result ? BackupStatus.BACKUP_CREATED : BackupStatus.BACKUP_CANCELLED;
            updateViews();
        }

        @Override
        protected void onProgressUpdate(AsyncProgress... values) {
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0].progress);
            textViewInfo.setText(values[0].status);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            backupCreateService.cancelBackup();
        }
    }
}
