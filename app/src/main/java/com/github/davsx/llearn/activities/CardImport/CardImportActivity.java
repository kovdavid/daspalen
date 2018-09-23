package com.github.davsx.llearn.activities.CardImport;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.CardImport.CardImportService;

import javax.inject.Inject;

public class CardImportActivity extends AppCompatActivity {

    @Inject
    private CardImportService cardImportService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_import);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);
    }
}
