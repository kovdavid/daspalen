package com.github.davsx.llearn.activities.KindleImport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.service.KindleImport.KindleImportService;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import javax.inject.Inject;

public class KindleImportActivity extends Activity {

    private static final String TAG = "KindleImportActivity";

    @Inject
    CardRepository cardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type.equals("message/rfc822")) {
            new KindleImportService(this, intent, cardRepository).doImport();
        }

        Intent i = new Intent(this, ManageCardsActivity.class);
        startActivity(i);
    }

}
