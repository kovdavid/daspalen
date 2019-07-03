package com.github.davsx.daspalen.activities.CardEditor;

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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import com.github.davsx.daspalen.service.CardImage.CardImageService;
import com.github.davsx.daspalen.service.ImageService.GoogleImageService;
import com.github.davsx.daspalen.service.ManageCards.ManageCardsService;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import com.github.davsx.daspalen.service.Speaker.SpeakerService;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.inject.Inject;

public class CardEditorActivity extends AppCompatActivity {

    private static final String TAG = "CardEditorActivity";

    @Inject
    DaspalenRepository repository;
    @Inject
    CardImageService cardImageService;
    @Inject
    SpeakerService speakerService;
    @Inject
    SettingsService settingsService;
    @Inject
    OkHttpClient httpClient;

    private ImageView imageView;
    private EditText editTextFront;
    private EditText editTextBack;
    private ImageButton imageButtonFront;
    private ImageButton imageButtonBack;
    private ImageButton imageButtonImage;
    private TextView textViewQuizInfo;
    private ToggleButton toggleButtonCardEnabled;
    private ToggleButton toggleButtonNotificationEnabled;
    private TableLayout tableEnableButtons;

    private Card card = null;
    private Long cardId = 0L;
    private Integer cardPosition = 0;
    private String imagePath = null;
    private String imageHash = null;
    private String frontText = "";
    private String backText = "";
    private boolean translateFront = true;

    private String receivedTranslation = "";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_editor);

        ((DaspalenApplication) getApplication()).getApplicationComponent().inject(this);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));
        sharedPreferences = getPreferences(MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("   Card");
        actionBar.setIcon(R.mipmap.daspalen_icon);

        imageView = findViewById(R.id.card_image);
        editTextFront = findViewById(R.id.edittext_front);
        editTextBack = findViewById(R.id.edittext_back);
        textViewQuizInfo = findViewById(R.id.textview_quiz_info);
        imageButtonFront = findViewById(R.id.imagebutton_front_text);
        imageButtonBack = findViewById(R.id.imagebutton_back_text);
        imageButtonImage = findViewById(R.id.imagebutton_image);
        toggleButtonCardEnabled = findViewById(R.id.toggle_button_card_enabled);
        toggleButtonNotificationEnabled = findViewById(R.id.toggle_button_notification_enabled);
        tableEnableButtons = findViewById(R.id.table_enable_buttons);

        ImageView buttonTTS = findViewById(R.id.button_tts);
        ImageView buttonSwap = findViewById(R.id.button_swap_front_back);

        buttonSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frontText = editTextFront.getText().toString();
                String backText = editTextBack.getText().toString();

                editTextFront.setText(backText);
                editTextBack.setText(frontText);
            }
        });

        buttonTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakerService.speak(editTextBack.getText().toString());
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.httpClient.dispatcher().cancelAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_card_editor, menu);

        if (card == null) {
            MenuItem item = menu.findItem(R.id.action_card_info);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                onActionSave();
                break;
            case R.id.action_card_info:
                CardInfoDialog.show(this, card);
                break;
            case R.id.action_cancel:
                onActionCancel();
                break;
        }

        return true;
    }

    private void onActionSave() {
        CardEntity duplicateCardEntity = findDuplicateCardEntity();
        boolean isNewCard = card == null;

        if (duplicateCardEntity == null) {
            saveCard();
            if (isNewCard) {
                openManageCardsActivity(ManageCardsService.RESULT_CARD_ADDED);
            } else {
                openManageCardsActivity(ManageCardsService.RESULT_CARD_CHANGED);
            }
        } else {
            showDuplicateCardAlert(duplicateCardEntity);
        }
    }

    private void onActionCancel() {
        openManageCardsActivity(ManageCardsService.RESULT_CARD_NOT_CHANGED);
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

        editTextFront.setText(frontText);
        editTextBack.setText(backText);

        if (card == null) {
            tableEnableButtons.setVisibility(View.GONE);
        } else {
            textViewQuizInfo.setText(card.getQuizInfo());

            toggleButtonCardEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    card.setCardEnabled(isChecked);
                    redrawToggleButtons();
                }
            });
            toggleButtonNotificationEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    card.setNotificationEnabled(isChecked);
                    redrawToggleButtons();
                }
            });

            redrawToggleButtons();
        }

        if (receivedTranslation != null && receivedTranslation.length() > 0) {
            showTranslationDialog();
        }

        // We do this twice, because imagePath can be set to tempImage
        if (imagePath == null && cardId != null) {
            imagePath = cardImageService.getCardImagePath(cardId);
            imageHash = cardImageService.getImageHash(imagePath);
        }
        if (imagePath != null) {
            imageHash = cardImageService.getImageHash(imagePath);
            Bitmap bmImg = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bmImg);
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        imageButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(CardEditorActivity.this, imageButtonImage);
                popup.getMenuInflater().inflate(R.menu.menu_card_editor_image, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_find_image_browser:
                                searchImageFromWeb();
                                break;
                            case R.id.action_google_custom_search:
                                chooseImageGoogleCustomSearch();
                                break;
                            case R.id.action_delete_image:
                                showImageDeleteConfirmDialog();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        imageButtonFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(CardEditorActivity.this, imageButtonFront);
                popup.getMenuInflater().inflate(R.menu.menu_card_editor_edittext, popup.getMenu());
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
                            case R.id.translate_reverso_context:
                                translateFrontWithReversoContext();
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
                popup.getMenuInflater().inflate(R.menu.menu_card_editor_edittext, popup.getMenu());
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
                            case R.id.translate_reverso_context:
                                translateBackWithReversoContext();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private void chooseImageGoogleCustomSearch() {
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

        Request request = GoogleImageService.getRequest(settingsService, query);
        if (request == null) {
            Toast.makeText(this, "Image search API KEY or CX KEY settings are missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ChosenBitmapHandler handler = new ChosenBitmapHandler() {
            @Override
            public void handle(Bitmap bitmap) {
                imagePath = cardImageService.saveTempBitmap(bitmap);
                imageHash = cardImageService.getImageHash(imagePath);
                imageView.setImageBitmap(bitmap);
            }
        };

        ImageChooserDialog dialog = new ImageChooserDialog(this);
        dialog.setInitialRequest(request);
        dialog.setChosenBitmapHandler(handler);
        dialog.setHttpClient(httpClient);
        dialog.show();
    }

    private void redrawToggleButtons() {
        toggleButtonCardEnabled.setChecked(card.getCardEnabled());
        toggleButtonNotificationEnabled.setChecked(card.getNotificationEnabled());

        int cardBg = card.getCardEnabled() ? R.color.colorGreen : R.color.colorAccent;
        int notificationBg = card.getNotificationEnabled() ? R.color.colorGreen : R.color.colorAccent;

        toggleButtonCardEnabled.setBackgroundResource(cardBg);
        toggleButtonNotificationEnabled.setBackgroundResource(notificationBg);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        Log.d(TAG, "handleIntent action[" + action + "] type[" + type + "]");

        if (action == null) {
            cardId = intent.getLongExtra("ID_CARD", 0L);
            if (cardId > 0L) {
                card = repository.getCardWithId(cardId);

                cardPosition = intent.getIntExtra("CARD_POSITION", 0);
                frontText = card.getFrontText();
                backText = card.getBackText();
                imagePath = cardImageService.getCardImagePath(cardId);
                imageHash = cardImageService.getImageHash(imagePath);
            } else {
                cardPosition = 0;
                frontText = "";
                backText = "";
                imagePath = null;
                imageHash = null;
            }
        } else if (action.equals(Intent.ACTION_SEND) && type != null) {
            switch (type) {
                case "image/jpeg":
                case "image/png":
                case "image/gif":
                case "image/*":
                    Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (imageUri != null) {
                        imagePath = cardImageService.saveTempImage(getContentResolver(), imageUri);
                        imageHash = cardImageService.getImageHash(imagePath);
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

    private void translateFrontWithReversoContext() {
        String frontText = editTextFront.getText().toString();
        if (frontText.equals("")) {
            Toast.makeText(this, "Front text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateFront = true;

        Uri uri = Uri.parse("http://context.reverso.net/translation/english-spanish/" + Uri.encode(frontText));
        translateWithReversoContext(uri);
    }

    private void translateBackWithReversoContext() {
        String backText = editTextBack.getText().toString();
        if (backText.equals("")) {
            Toast.makeText(this, "Back text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateFront = false;

        Uri uri = Uri.parse("http://context.reverso.net/translation/spanish-english/" + Uri.encode(backText));
        translateWithReversoContext(uri);
    }

    private void translateWithReversoContext(Uri uri) {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(DaspalenConstants.PKG_REVERSO_CONTEXT, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (packageInfo != null) {
            putDataToSharedPrefs();
            openReversoContext(uri);
        } else {
            String appName = "Reverso Context";
            String pkgName = DaspalenConstants.PKG_REVERSO_CONTEXT;
            alertInstallApp(appName, pkgName);
        }
    }

    private void openReversoContext(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setPackage(DaspalenConstants.PKG_REVERSO_CONTEXT);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        startActivity(intent);
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
            packageInfo = pm.getPackageInfo(DaspalenConstants.PKG_SPANISHDICT, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (packageInfo != null) {
            putDataToSharedPrefs();
            openSpanishDict(uri);
        } else {
            String appName = "SpanishDict";
            String pkgName = DaspalenConstants.PKG_SPANISHDICT;
            alertInstallApp(appName, pkgName);
        }
    }

    private void openSpanishDict(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setPackage(DaspalenConstants.PKG_SPANISHDICT);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        startActivity(intent);
    }

    private void translateFrontWithGoogleTranslate() {
        String frontText = editTextFront.getText().toString();
        if (frontText.equals("")) {
            Toast.makeText(this, "Front text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateFront = true;
        Uri uri = Uri.parse("http://translate.google.com/m/translate?sl=en&tl=es&q=" + Uri.encode(frontText));
        translateWithGoogleTranslate(uri);
    }

    private void translateBackWithGoogleTranslate() {
        String backText = editTextBack.getText().toString();
        if (backText.equals("")) {
            Toast.makeText(this, "Back text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        translateFront = false;
        Uri uri = Uri.parse("http://translate.google.com/m/translate?sl=es&tl=en&q=" + Uri.encode(backText));
        translateWithGoogleTranslate(uri);
    }

    private void translateWithGoogleTranslate(Uri uri) {
        PackageManager pm = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(DaspalenConstants.PKG_GOOGLE_TRANSLATE, 0);
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        if (packageInfo != null) {
            putDataToSharedPrefs();
            openGoogleTranslate(uri);
        } else {
            String appName = "Google Translate";
            String pkgName = DaspalenConstants.PKG_GOOGLE_TRANSLATE;

            alertInstallApp(appName, pkgName);
        }
    }

    private void openGoogleTranslate(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setPackage(DaspalenConstants.PKG_GOOGLE_TRANSLATE);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
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

    private void showImageDeleteConfirmDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cardImageService.removeCardImages(cardId);
                        imagePath = null;
                        imageHash = null;
                        imageView.setImageResource(android.R.drawable.ic_menu_report_image);
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

    private CardEntity findDuplicateCardEntity() {
        String frontText = editTextFront.getText().toString();
        String backText = editTextBack.getText().toString();

        // We don't check duplicity for incomplete cards
        if (frontText.length() > 0 && backText.length() > 0) {
            CardEntity dupCard = repository.findDuplicateCardEntity(frontText, backText);
            if (dupCard != null && dupCard.getCardId().equals(cardId)) {
                return null;
            }
            return dupCard;
        } else {
            return null;
        }
    }

    private void showDuplicateCardAlert(@NonNull CardEntity duplicateCard) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String dialogMessage = String.format(
                "The current card cannot be saved, because a duplicate card was found:<br /><br />Front<br " +
                        "/><b>%s</b><br />Back<br /><b>%s</b>",
                duplicateCard.getFrontText(),
                duplicateCard.getBackText());
        builder.setTitle("Error")
                .setMessage(Html.fromHtml(dialogMessage))
                .setPositiveButton("Ok", null)
                .show();
    }

    private void saveCard() {
        String newFront = editTextFront.getText().toString();
        String newBack = editTextBack.getText().toString();

        if (card == null) {
            card = Card.createNew(newFront, newBack);
            card.updateImageHash(imageHash);
            cardId = repository.createNewCard(card);
        } else {
            if (!newFront.equals(card.getFrontText()) || !newBack.equals(card.getBackText())) {
                card.updateTexts(newFront, newBack);
            }
            card.updateImageHash(imageHash);
            repository.updateCard(card);
        }

        if (cardImageService.isTempImage(imagePath)) {
            cardImageService.removeCardImages(cardId);
            cardImageService.setCardImageFromTemp(cardId);
        }
    }

    private void putDataToSharedPrefs() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("CARD_ID", cardId);
        editor.putInt("CARD_POSITION", cardPosition);
        editor.putString("IMAGE_PATH", imagePath);
        editor.putString("IMAGE_HASH", imageHash);
        editor.putString("FRONT_TEXT", editTextFront.getText().toString());
        editor.putString("BACK_TEXT", editTextBack.getText().toString());
        editor.putBoolean("TRANSLATE_FRONT", translateFront);

        editor.commit();
    }

    private void loadDataFromSharedPrefs() {
        cardId = sharedPreferences.getLong("CARD_ID", 0L);
        cardPosition = sharedPreferences.getInt("CARD_POSITION", 0);
        imagePath = sharedPreferences.getString("IMAGE_PATH", null);
        imageHash = sharedPreferences.getString("IMAGE_HASH", null);
        frontText = sharedPreferences.getString("FRONT_TEXT", "");
        backText = sharedPreferences.getString("BACK_TEXT", "");
        translateFront = sharedPreferences.getBoolean("TRANSLATE_FRONT", true);

        if (cardId > 0L) {
            card = repository.getCardWithId(cardId);
        }
    }

}
