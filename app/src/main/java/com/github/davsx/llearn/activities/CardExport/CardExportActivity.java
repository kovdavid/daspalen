package com.github.davsx.llearn.activities.CardExport;

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
import android.widget.TextView;
import android.widget.Toast;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.CardExport.CardExportService;
import com.github.lzyzsd.circleprogress.CircleProgress;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.OutputStream;

public class CardExportActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    CardExportService cardExportService;

    private CircleProgress progressBar;
    private TextView textViewInfo;
    private Button buttonExport;

    private ExportStatus exportStatus;

    private AsyncTask<OutputStream, Pair<Integer, String>, Boolean> exportTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_export);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        progressBar = findViewById(R.id.progress_bar);
        textViewInfo = findViewById(R.id.textview_info);
        buttonExport = findViewById(R.id.button_export);
        buttonExport.setOnClickListener(this);

        exportStatus = ExportStatus.EXPORT_NOT_STARTED;

        updateViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (exportTask != null) {
            exportTask.cancel(true);
        }
    }

    private void updateViews() {
        if (exportStatus.equals(ExportStatus.EXPORT_NOT_STARTED)) {
            textViewInfo.setText("Export cards");
            buttonExport.setText("Export cards");
        } else if (exportStatus.equals(ExportStatus.EXPORT_RUNNING)) {
            buttonExport.setText("Cancel export");
        } else if (exportStatus.equals(ExportStatus.EXPORT_CANCELLED)) {
            textViewInfo.setText("Export cancelled");
            buttonExport.setText("Export cards");
        } else if (exportStatus.equals(ExportStatus.EXPORT_FINISHED)) {
            textViewInfo.setText("Export finished");
            buttonExport.setText("Export cards");
        }
    }

    @Override
    public void onClick(View v) {
        if (exportStatus.equals(ExportStatus.EXPORT_RUNNING)) {
            if (exportTask != null) {
                exportTask.cancel(true);
            }
            exportStatus = ExportStatus.EXPORT_CANCELLED;
            updateViews();
        } else {
            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("application/zip");
            i.putExtra(Intent.EXTRA_TITLE, cardExportService.getDefaultFileName());
            startActivityForResult(i, LLearnConstants.REQUEST_CODE_CREATE_DOCUMENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == LLearnConstants.REQUEST_CODE_CREATE_DOCUMENT) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        doExport(uri);
                    }
                }
            }
        }
    }

    private void doExport(Uri uri) {
        OutputStream outputStream;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not create file", Toast.LENGTH_SHORT).show();
            exportStatus = ExportStatus.EXPORT_CANCELLED;
            updateViews();
            return;
        }

        exportTask = new ExportTask().execute(outputStream);
    }

    private enum ExportStatus {
        EXPORT_NOT_STARTED,
        EXPORT_RUNNING,
        EXPORT_CANCELLED,
        EXPORT_FINISHED
    }

    private class ExportTask extends AsyncTask<OutputStream, Pair<Integer, String>, Boolean> {
        @Override
        protected Boolean doInBackground(OutputStream... outputStreams) {
            cardExportService.startExport(outputStreams[0]);

            boolean run;
            do {
                run = cardExportService.doNextChunk();
                Pair<Integer, String> p = new Pair<>(cardExportService.getCurrentProgress(),
                        cardExportService.getStatus());
                publishProgress(p);
            } while (run);

            return cardExportService.getFinished();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            exportStatus = ExportStatus.EXPORT_RUNNING;
            progressBar.setMax(100);

            updateViews();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            exportStatus = result ? ExportStatus.EXPORT_FINISHED : ExportStatus.EXPORT_CANCELLED;
            updateViews();
        }

        @Override
        protected void onProgressUpdate(Pair<Integer, String>... values) {
            super.onProgressUpdate(values);

            if (values != null && values[0] != null && values[0].first != null) {
                progressBar.setProgress(values[0].first);
                textViewInfo.setText(values[0].second);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cardExportService.cancelExport();
        }
    }
}
