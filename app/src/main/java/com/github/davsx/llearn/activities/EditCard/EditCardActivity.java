package com.github.davsx.llearn.activities.EditCard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;

public class EditCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (action.equals(Intent.ACTION_SEND)) {
            if (type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/gif")) {


                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    ImageView v = findViewById(R.id.card_image);
                    v.setImageURI(imageUri);
                    // Update UI to reflect image being shared
                }

            } else {
                Intent i = new Intent(this, ManageCardsActivity.class);
                startActivity(i);
                finish();
                return;
            }
        }
    }
}
