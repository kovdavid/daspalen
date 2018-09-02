package com.github.davsx.llearn.gui.activities.KindleImport;

import android.content.Intent;
import android.os.Bundle;
import com.github.davsx.llearn.data.KindleImport.KindleImportReceiver;
import com.github.davsx.llearn.gui.activities.BaseActivity;
import com.github.davsx.llearn.gui.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

public class KindleImportActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CardRepository cardRepository = CardRepository.getInstance(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type.equals("message/rfc822")) {
            new KindleImportReceiver(this, intent, cardRepository).doImport();
        }

        Intent i = new Intent(this, ManageCardsActivity.class);
        startActivity(i);
    }

}
