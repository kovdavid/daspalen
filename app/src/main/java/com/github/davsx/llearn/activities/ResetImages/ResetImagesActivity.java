package com.github.davsx.llearn.activities.ResetImages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.persistence.repository.CardRepositoryOld;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Inject;

public class ResetImagesActivity extends AppCompatActivity {

    @Inject
    OkHttpClient okHttpClient;
    @Inject
    CardRepositoryOld cardRepository;

    private Button buttonResetImages;
    private ProgressBar progressBar;
    private TextView textViewInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_import);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);
    }

}
