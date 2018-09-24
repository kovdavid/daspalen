package com.github.davsx.llearn.activities.CardImport;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.CardImport.CardImportService;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class CardImportActivity extends AppCompatActivity {

    @Inject
    CardImportService cardImportService;

    private Button btnImport;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_import);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        btnImport = findViewById(R.id.button_import);
        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("application/zip");
                startActivityForResult(i, LLearnConstants.REQUEST_CODE_OPEN_DOCUMENT);
            }
        });
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

        new ImportTask().execute(inputStream);
    }

    private class ImportTask extends AsyncTask<InputStream, Void, Void> {
        @Override
        protected Void doInBackground(InputStream... inputStreams) {

            Map<String, byte[]> zipContent = new HashMap<>();

            ZipInputStream zipInputStream = new ZipInputStream(inputStreams[0]);
            ZipEntry zipEntry;
            int len;
            byte[] buffer = new byte[8096];

            try {
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

                    while ((len = zipInputStream.read(buffer)) > 0) {
                        byteBuffer.write(buffer, 0, len);
                    }

                    zipContent.put(zipEntry.getName(), byteBuffer.toByteArray());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
