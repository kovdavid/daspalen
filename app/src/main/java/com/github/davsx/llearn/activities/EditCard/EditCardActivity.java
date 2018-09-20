package com.github.davsx.llearn.activities.EditCard;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.LLearnConstants;
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
    private ImageButton imageButtonFront;
    private ImageButton imageButtonBack;

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
        } else if (action.equals(Intent.ACTION_SEND) && type != null) {
            if (type.equals("image/jpeg") || type.equals("image/png") || type.equals("image/gif")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    loadDataFromSharedPrefs();
                    imagePath = cardImageService.saveTempImage(getContentResolver(), imageUri);
                }
            } else if (type.equals("text/plain")) {
                loadDataFromSharedPrefs();
                // com.google.android.app.translate
                String host = this.getReferrer().getHost();
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                Toast.makeText(this, sharedText, Toast.LENGTH_SHORT).show();
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

        imageButtonFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(EditCardActivity.this, imageButtonFront);
                popup.getMenuInflater().inflate(R.menu.edit_cards_edittext_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.translate_spanishdict:
                                translateFrontWithSpanishDict();
                                break;
                            case R.id.translate_google_translate:
                                translateFrontWithGoogleTranslate();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(EditCardActivity.this, imageButtonBack);
                popup.getMenuInflater().inflate(R.menu.edit_cards_edittext_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.translate_spanishdict:
                                translateBackWithSpanishDict();
                                break;
                            case R.id.translate_google_translate:
                                translateBackWithGoogleTranslate();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void translateFrontWithSpanishDict() {
        String frontText = editTextFront.getText().toString();
        if (frontText.equals("")) {
            Toast.makeText(this, "Front text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse("http://www.spanishdict.com/translate/" + Uri.encode(frontText));
        translateWithSpanishDict(uri);
    }

    private void translateBackWithSpanishDict() {
        String backText = editTextBack.getText().toString();
        if (backText.equals("")) {
            Toast.makeText(this, "Back text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse("http://www.spanishdict.com/traductor/" + Uri.encode(backText));
        translateWithSpanishDict(uri);
    }

    private void translateWithSpanishDict(Uri uri) {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(LLearnConstants.PKG_SPANISHDICT, 0);
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (packageInfo != null) {
            openSpanishDict(uri);
        } else {
            String appName = "SpanishDict";
            String pkgName = LLearnConstants.PKG_SPANISHDICT;

            alertInstallApp(appName, pkgName);
        }
    }

    private void translateBackWithGoogleTranslate() {
        String backText = editTextBack.getText().toString();
        if (backText.equals("")) {
            Toast.makeText(this, "Back text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateWithGoogleTranslate(backText, "es", "en");
    }

    private void translateFrontWithGoogleTranslate() {
        String frontText = editTextFront.getText().toString();
        if (frontText.equals("")) {
            Toast.makeText(this, "Front text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateWithGoogleTranslate(frontText, "en", "es");
    }

    private void translateWithGoogleTranslate(String text, String lngFrom, String lngTo) {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(LLearnConstants.PKG_GOOGLE_TRANSLATE, 0);
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (packageInfo != null) {
            openGoogleTranslate(text, lngFrom, lngTo);
        } else {
            String appName = "Google Translate";
            String pkgName = LLearnConstants.PKG_GOOGLE_TRANSLATE;

            alertInstallApp(appName, pkgName);
        }
    }

    private void openGoogleTranslate(String text, String lngFrom, String lngTo) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra("key_text_input", text);
        intent.putExtra("key_text_output", "");
        intent.putExtra("key_language_from", lngFrom);
        intent.putExtra("key_language_to", lngTo);
        intent.putExtra("key_suggest_translation", "");
        intent.putExtra("key_from_floating_window", false);
        intent.setComponent(new ComponentName(
                LLearnConstants.PKG_GOOGLE_TRANSLATE,
                "com.google.android.apps.translate.TranslateActivity"));
        startActivity(intent);
    }

    private void alertInstallApp(String appName, final String pkgName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Application "+ appName +" not installed")
                .setPositiveButton("Install", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("market://details?id=" + pkgName);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void openSpanishDict(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setPackage("com.spanishdict.spanishdict");
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        startActivity(intent);
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
        String newFront = editTextFront.getText().toString();
        String newBack = editTextBack.getText().toString();

        if (!newFront.equals(card.getFront()) || !newBack.equals(card.getBack())) {
            card.setType(CardEntity.TYPE_INCOMPLETE);
            card.setLearnScore(0);
            card.setFront(newFront);
            card.setBack(newBack);
        }
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
        imageButtonFront = findViewById(R.id.imagebutton_front_text);
        imageButtonBack = findViewById(R.id.imagebutton_back_text);
    }

}