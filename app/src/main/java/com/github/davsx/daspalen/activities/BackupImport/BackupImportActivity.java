package com.github.davsx.daspalen.activities.BackupImport;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.service.BackupImport.BackupImportService;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BackupImportActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    BackupImportService backupImportService;

    private Button buttonImport;
    private ProgressBar progressBar;
    private TextView textViewInfo;

    private ImportStatus importStatus;

    private AsyncTask<InputStream, String, Boolean> importTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_import);

        ((DaspalenApplication) getApplication()).getApplicationComponent().inject(this);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        textViewInfo = findViewById(R.id.textview_info);
        buttonImport = findViewById(R.id.button_import);
        buttonImport.setOnClickListener(this);

        importStatus = ImportStatus.IMPORT_NOT_STARTED;

        updateViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (importTask != null) {
            importTask.cancel(true);
            importStatus = ImportStatus.IMPORT_CANCELLED;
        }
    }

    private void updateViews() {
        buttonImport.setText("Import backup");
        if (importStatus.equals(ImportStatus.IMPORT_NOT_STARTED)) {
            textViewInfo.setText("Import backup");
            progressBar.setVisibility(View.INVISIBLE);
        } else if (importStatus.equals(ImportStatus.IMPORT_RUNNING)) {
            buttonImport.setText("Cancel import");
            progressBar.setVisibility(View.VISIBLE);
        } else if (importStatus.equals(ImportStatus.IMPORT_CANCELLED)) {
            textViewInfo.setText("Import cancelled");
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            textViewInfo.setText("Import finished");
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DaspalenConstants.REQUEST_CODE_OPEN_DOCUMENT) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        doImport(uri);
                    }
                }
            }
        }
    }

    private void doImport(Uri uri) {
        InputStream inputStream;
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not open file", Toast.LENGTH_SHORT).show();
            return;
        }

        importTask = new ImportTask().execute(inputStream);
    }

    @Override
    public void onClick(View v) {
        if (importStatus.equals(ImportStatus.IMPORT_RUNNING)) {
            if (importTask != null) {
                importTask.cancel(true);
            }
            importStatus = ImportStatus.IMPORT_CANCELLED;
            updateViews();
        } else {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("application/zip");
            startActivityForResult(i, DaspalenConstants.REQUEST_CODE_OPEN_DOCUMENT);
        }
    }

    private enum ImportStatus {
        IMPORT_NOT_STARTED,
        IMPORT_RUNNING,
        IMPORT_CANCELLED,
        IMPORT_FINISHED
    }

    private class ImportTask extends AsyncTask<InputStream, String, Boolean> {
        @Override
        protected Boolean doInBackground(InputStream... inputStreams) {
            backupImportService.startImport(inputStreams[0]);

            boolean run;
            do {
                run = backupImportService.doNextChunk();
                publishProgress(backupImportService.getStatus());
            } while (run);

            return backupImportService.getFinished();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            importStatus = ImportStatus.IMPORT_RUNNING;
            progressBar.setVisibility(View.VISIBLE);

            updateViews();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            importStatus = result ? ImportStatus.IMPORT_FINISHED : ImportStatus.IMPORT_CANCELLED;
            updateViews();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            textViewInfo.setText(values[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            backupImportService.cancelImport();
        }
    }
}
