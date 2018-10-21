package com.github.davsx.llearn.activities.KindleImport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.KindleImport.KindleImportService;

import javax.inject.Inject;

public class KindleImportActivity extends Activity {

    @Inject
    CardRepository cardRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (action.equals(Intent.ACTION_SEND_MULTIPLE) && type.equals("message/rfc822")) {
            KindleImportService.doImport(this, intent, cardRepository);
        }

        Intent i = new Intent(this, ManageCardsActivity.class);
        startActivity(i);
        finish();
    }
}
