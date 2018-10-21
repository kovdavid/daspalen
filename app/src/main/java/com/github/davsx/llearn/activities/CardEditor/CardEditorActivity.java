package com.github.davsx.llearn.activities.CardEditor;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;
import com.github.davsx.llearn.service.Speaker.SpeakerService;

import javax.inject.Inject;
import java.util.Locale;

public class CardEditorActivity extends AppCompatActivity {

    private static final String TAG = "CardEditorActivity";

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
    private ImageView buttonSwap;

    private CardEntity card = null;
    private Long cardId = 0L;
    private Integer cardPosition = 0;
    private String imagePath = null;
    private String frontText = "";
    private String backText = "";
    private boolean translateFront = true;

    private String receivedTranslation = "";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_editor);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);
        sharedPreferences = getPreferences(MODE_PRIVATE);

        setUpViews();
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadDataFromSharedPrefs();
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        speakerService.setLanguage(new Locale("spa", "ESP"));

        editTextFront.setText(frontText);
        editTextBack.setText(backText);

        if (receivedTranslation != null && receivedTranslation.length() > 0) {
            showTranslationDialog();
        }

        // We do this twice, because imagePath can be set to tempImage
        if (imagePath == null && cardId != null) {
            imagePath = cardImageService.getCardImagePath(cardId);
        }
        if (imagePath != null) {
            Bitmap bmImg = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bmImg);
        }

        int learnScore = card != null ? card.getLearnScore() : 0;
        textViewCardScore.setText(Integer.toString(learnScore));

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
                onSaveCard();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openManageCardsActivity(ManageCardsService.RESULT_CARD_NOT_CHANGED);
            }
        });

        if (cardId > 0L && card != null) {
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();
                }
            });
        } else {
            buttonDelete.setOnClickListener(null);
            buttonDelete.setEnabled(false);
        }

        imageButtonFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(CardEditorActivity.this, imageButtonFront);
                popup.getMenuInflater().inflate(R.menu.card_editor_edittext_menu, popup.getMenu());
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
                PopupMenu popup = new PopupMenu(CardEditorActivity.this, imageButtonBack);
                popup.getMenuInflater().inflate(R.menu.card_editor_edittext_menu, popup.getMenu());
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

        buttonSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frontText = editTextFront.getText().toString();
                String backText = editTextBack.getText().toString();

                editTextFront.setText(backText);
                editTextBack.setText(frontText);
            }
        });
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        Log.d(TAG, "handleIntent action[" + action + "] type[" + type + "]");

        if (action == null) {
            cardId = intent.getLongExtra("ID_CARD", 0L);
            if (cardId > 0L) {
                card = cardRepository.getCardWithId(cardId);

                cardPosition = intent.getIntExtra("CARD_POSITION", 0);
                frontText = card.getFront();
                backText = card.getBack();
                imagePath = cardImageService.getCardImagePath(cardId);
            } else {
                cardPosition = 0;
                frontText = "";
                backText = "";
                imagePath = "";
            }
        } else if (action.equals(Intent.ACTION_SEND) && type != null) {
            switch (type) {
                case "image/jpeg":
                case "image/png":
                case "image/gif":
                    Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (imageUri != null) {
                        imagePath = cardImageService.saveTempImage(getContentResolver(), imageUri);
                    }
                    break;
                case "text/plain":
                    String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                    if (text != null) {
                        receivedTranslation = text;
                    }
                    break;
                default:
                    Log.e(TAG, "Invalid ACTION_SEND type " + type);
                    openManageCardsActivity(ManageCardsService.RESULT_CARD_NOT_CHANGED);
                    break;
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

    private void showTranslationDialog() {
        final EditText targetEditText;
        if (translateFront) {
            targetEditText = editTextBack;
        } else {
            targetEditText = editTextFront;
        }

        String targetText = targetEditText.getText().toString();
        final String translation = receivedTranslation;
        receivedTranslation = "";

        if (targetText.length() == 0) {
            targetEditText.setText(translation);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String dialogMessage =
                String.format("Current<br /><b>%s</b><br /><br />Received<br /><b>%s</b>", targetText, translation);
        builder.setTitle("Use received translation?")
                .setMessage(Html.fromHtml(dialogMessage))
                .setPositiveButton("Replace", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        targetEditText.setText(translation);
                    }
                })
                .setNeutralButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Append", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        targetEditText.getText().append(translation);
                    }
                })
                .show();
    }

    private void translateFrontWithSpanishDict() {
        String frontText = editTextFront.getText().toString();
        if (frontText.equals("")) {
            Toast.makeText(this, "Front text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateFront = true;

        Uri uri = Uri.parse("http://www.spanishdict.com/translate/" + Uri.encode(frontText));
        translateWithSpanishDict(uri);
    }

    private void translateBackWithSpanishDict() {
        String backText = editTextBack.getText().toString();
        if (backText.equals("")) {
            Toast.makeText(this, "Back text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateFront = false;

        Uri uri = Uri.parse("http://www.spanishdict.com/traductor/" + Uri.encode(backText));
        translateWithSpanishDict(uri);
    }

    private void translateWithSpanishDict(Uri uri) {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(LLearnConstants.PKG_SPANISHDICT, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (packageInfo != null) {
            putDataToSharedPrefs();
            openSpanishDict(uri);
        } else {
            String appName = "SpanishDict";
            String pkgName = LLearnConstants.PKG_SPANISHDICT;
            alertInstallApp(appName, pkgName);
        }
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

    private void translateBackWithGoogleTranslate() {
        String backText = editTextBack.getText().toString();
        if (backText.equals("")) {
            Toast.makeText(this, "Back text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateFront = false;
        translateWithGoogleTranslate(backText, "es", "en");
    }

    private void translateFrontWithGoogleTranslate() {
        String frontText = editTextFront.getText().toString();
        if (frontText.equals("")) {
            Toast.makeText(this, "Front text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateFront = true;
        translateWithGoogleTranslate(frontText, "en", "es");
    }

    private void translateWithGoogleTranslate(String text, String lngFrom, String lngTo) {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(LLearnConstants.PKG_GOOGLE_TRANSLATE, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (packageInfo != null) {
            putDataToSharedPrefs();
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
                .setMessage("Application " + appName + " not installed")
                .setPositiveButton("Install", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        putDataToSharedPrefs();
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

    private void showImageEditDialog() {
        String[] items = {"Delete image", "Find image on the web"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CardEditorActivity.this)
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

        String frontString = editTextFront.getText().toString();
        String backString = editTextBack.getText().toString();

        String query;
        if (frontString.length() > 0) {
            query = frontString;
        } else if (backString.length() > 0) {
            query = backString;
        } else {
            Toast.makeText(this, "Front and Back are empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://www.google.com/search?tbm=isch&q=" + Uri.encode(query);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void onSaveCard() {
        CardEntity dupCard = findDupCard();

        if (dupCard == null) {
            saveCard();
            openManageCardsActivity(ManageCardsService.RESULT_CARD_CHANGED);
        } else {
            showDupCardAlert(dupCard);
        }
    }

    private CardEntity findDupCard() {
        String frontText = editTextFront.getText().toString();
        String backText = editTextBack.getText().toString();

        // We don't check duplicity for incomplete cards
        if (frontText.length() > 0 && backText.length() > 0) {
            CardEntity dupCard = cardRepository.findDuplicateCard(frontText, backText);
            if (dupCard != null && dupCard.getId().equals(cardId)) {
                return null;
            }
            return dupCard;
        } else {
            return null;
        }
    }

    private void showDupCardAlert(@NonNull CardEntity dupCard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String dialogMessage = String.format(
                "The current card cannot be saved, because a duplicate card was found:<br /><br />Front<br /><b>%s</b><br />Back<br /><b>%s</b>",
                dupCard.getFront(),
                dupCard.getBack());
        builder.setTitle("Error")
                .setMessage(Html.fromHtml(dialogMessage))
                .setPositiveButton("Ok", null)
                .show();
    }

    private void saveCard() {
        String newFront = editTextFront.getText().toString();
        String newBack = editTextBack.getText().toString();

        if (card == null) {
            card = new CardEntity()
                    .setFront(newFront)
                    .setBack(newBack)
                    .setCreatedAt(System.currentTimeMillis())
                    .setLearnScore(0);
        } else {
            if (!newFront.equals(card.getFront()) || !newBack.equals(card.getBack())) {
                card.setType(LLearnConstants.CARD_TYPE_INCOMPLETE)
                        .setLearnScore(0)
                        .setFront(newFront)
                        .setBack(newBack);
            }
        }

        cardId = cardRepository.save(card);

        if (cardImageService.isTempImage(imagePath)) {
            cardImageService.removeCardImages(cardId);
            cardImageService.setCardImageFromTemp(cardId);
        }
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
        if (card != null) {
            cardRepository.deleteCard(card);
            openManageCardsActivity(ManageCardsService.RESULT_CARD_DELETED);
        }
        openManageCardsActivity(ManageCardsService.RESULT_CARD_NOT_CHANGED);
    }

    private void putDataToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("CARD_ID", cardId);
        editor.putInt("CARD_POSITION", cardPosition);
        editor.putString("IMAGE_PATH", imagePath);
        editor.putString("FRONT_TEXT", editTextFront.getText().toString());
        editor.putString("BACK_TEXT", editTextBack.getText().toString());
        editor.putBoolean("TRANSLATE_FRONT", translateFront);

        editor.commit();
    }

    private void loadDataFromSharedPrefs() {
        cardId = sharedPreferences.getLong("CARD_ID", 0L);
        cardPosition = sharedPreferences.getInt("CARD_POSITION", 0);
        imagePath = sharedPreferences.getString("IMAGE_PATH", "");
        frontText = sharedPreferences.getString("FRONT_TEXT", "");
        backText = sharedPreferences.getString("BACK_TEXT", "");
        translateFront = sharedPreferences.getBoolean("TRANSLATE_FRONT", true);

        if (cardId > 0L) {
            card = cardRepository.getCardWithId(cardId);
        }
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
        buttonSwap = findViewById(R.id.button_swap_front_back);
    }

}