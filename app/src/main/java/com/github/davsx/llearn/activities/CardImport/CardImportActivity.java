package com.github.davsx.llearn.activities.CardImport;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.CardImport.CardImportService;
import com.github.lzyzsd.circleprogress.CircleProgress;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CardImportActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    CardImportService cardImportService;

    private Button buttonImport;
    private ProgressBar progressBar;
    private TextView textViewInfo;

    private ImportStatus importStatus;

    private AsyncTask<InputStream, String, Boolean> importTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_import);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

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
        buttonImport.setText("Import cards");
        if (importStatus.equals(ImportStatus.IMPORT_NOT_STARTED)) {
            textViewInfo.setText("Import cards");
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
            if (requestCode == LLearnConstants.REQUEST_CODE_OPEN_DOCUMENT) {
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
            startActivityForResult(i, LLearnConstants.REQUEST_CODE_OPEN_DOCUMENT);
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
            cardImportService.startImport(inputStreams[0]);

            boolean run;
            do {
                run = cardImportService.doNextChunk();
                publishProgress(cardImportService.getStatus());
            } while (run);

            return cardImportService.getFinished();
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
            cardImportService.cancelImport();
        }
    }
}
