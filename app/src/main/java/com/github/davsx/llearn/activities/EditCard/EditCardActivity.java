package com.github.davsx.llearn.activities.EditCard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.FileService.CardImageService;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;
import com.github.davsx.llearn.service.Speaker.SpeakerService;

import javax.inject.Inject;
import java.util.Locale;

public class EditCardActivity extends AppCompatActivity {

    private static final String TAG = "EditCardActivity";

    @Inject
    CardRepository cardRepository;
    @Inject
    CardImageService cardImageService;
    @Inject
    SpeakerService speakerService;

    private ImageView imageView;
    private EditText editTextFront;
    private EditText editTextBack;
    private ImageView buttonTTS;
    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonDelete;
    private TextView textViewCardScore;

    private CardEntity card;
    private Long cardId;
    private Integer cardPosition;
    private String imagePath;
    private String frontText = "";
    private String backText = "";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);
        sharedPreferences = getPreferences(MODE_PRIVATE);

        speakerService.setLanguage(new Locale("es", "ES"));

        setUpViews();
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (action == null) {
            cardId = intent.getLongExtra("ID_CARD", 0L);
            if (cardId > 0L) {
                card = cardRepository.getCardWithId(cardId);

                cardPosition = intent.getIntExtra("CARD_POSITION", 0);
                frontText = card.getFront();
                backText = card.getBack();
                imagePath = cardImageService.getCardImagePath(cardId);
            } else {
                Log.e(TAG, "Invalid ID_CARD in Intent extra");
                openManageCardsActivity(ManageCardsService.RESULT_CARD_NOT_CHANGED);
            }
        } else if (action.equals(Intent.ACTION_SEND)) {
            if (type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/gif")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    loadDataFromSharedPrefs();
                    imagePath = cardImageService.saveTempImage(getContentResolver(), imageUri);
                }
            } else {
                Log.e(TAG, "Invalid ACTION_SEND type " + type);
                openManageCardsActivity(ManageCardsService.RESULT_CARD_NOT_CHANGED);
            }
        }
    }

    private void openManageCardsActivity(int result) {
        Intent i = new Intent(this, ManageCardsActivity.class);
        i.putExtra("ID_CARD", cardId);
        i.putExtra("CARD_POSITION", cardPosition);
        i.putExtra("RESULT", result);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        editTextFront.setText(frontText);
        editTextBack.setText(backText);
        if (imagePath != null) {
            Bitmap bmImg = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bmImg);
        }
        textViewCardScore.setText(Integer.toString(card.getLearnScore()));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePath == null) {
                    searchImageFromWeb();
                } else {
                    showImageEditDialog();
                }

            }
        });

        buttonTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakerService.speak(editTextBack.getText().toString());
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCard();
                openManageCardsActivity(ManageCardsService.RESULT_CARD_CHANGED);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openManageCardsActivity(ManageCardsService.RESULT_CARD_NOT_CHANGED);
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });
    }

    private void showImageEditDialog() {
        String[] items = {"Delete image", "Find image on the web"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCardActivity.this)
                .setTitle("Choose action")
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                showImageDeleteConfirmDialog();
                                break;
                            case 1:
                                searchImageFromWeb();
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void showImageDeleteConfirmDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cardImageService.removeCardImages(cardId);
                        imagePath = null;
                        imageView.setImageResource(android.R.drawable.ic_delete);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void searchImageFromWeb() {
        putDataToSharedPrefs();
        String url = "https://www.google.com/search?tbm=isch&q=" + Uri.encode(editTextFront.getText().toString());
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void saveCard() {
        card.setFront(editTextFront.getText().toString());
        card.setBack(editTextBack.getText().toString());
        if (imagePath != null) {
            cardImageService.removeCardImages(cardId);
            cardImageService.setCardImageFromTemp(cardId);
        }
        cardRepository.save(card);
    }

    private void showDeleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCard();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void deleteCard() {
        cardRepository.deleteCard(card);
        openManageCardsActivity(ManageCardsService.RESULT_CARD_DELETED);
    }

    private void putDataToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("CARD_ID", cardId);
        editor.putInt("CARD_POSITION", cardPosition);
        editor.putString("IMAGE_PATH", imagePath);
        editor.putString("FRONT_TEXT", editTextFront.getText().toString());
        editor.putString("BACK_TEXT", editTextBack.getText().toString());

        editor.commit();
    }

    private void loadDataFromSharedPrefs() {
        cardId = sharedPreferences.getLong("CARD_ID", 0L);
        cardPosition = sharedPreferences.getInt("CARD_POSITION", 0);
        imagePath = sharedPreferences.getString("IMAGE_PATH", "");
        frontText = sharedPreferences.getString("FRONT_TEXT", "");
        backText = sharedPreferences.getString("BACK_TEXT", "");

        card = cardRepository.getCardWithId(cardId);
    }

    private void setUpViews() {
        imageView = findViewById(R.id.card_image);
        editTextFront = findViewById(R.id.edittext_front);
        editTextBack = findViewById(R.id.edittext_back);
        buttonTTS = findViewById(R.id.button_tts);
        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);
        buttonDelete = findViewById(R.id.button_delete);
        textViewCardScore = findViewById(R.id.textview_card_score);
    }

}
