package com.github.davsx.llearn.gui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.davsx.llearn.R;

public class MainActivity extends BaseActivity {
    @BindView(R.id.button_word_list) Button btnWordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ManageCardsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });
    }
}
