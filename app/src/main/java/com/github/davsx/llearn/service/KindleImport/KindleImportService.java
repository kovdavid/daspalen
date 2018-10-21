package com.github.davsx.llearn.service.KindleImport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class KindleImportService {

    private static final String TAG = "KindleImportService";

    public static void doImport(Context context, Intent intent, CardRepository cardRepository) {
        ArrayList<Uri> uris = intent.getExtras().getParcelableArrayList(Intent.EXTRA_STREAM);
        if (uris == null || uris.size() != 1) {
            return;
        }

        Uri uri = uris.get(0);

        String html;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            html = builder.toString();
        } catch (Exception e) {
            return;
        }

        ArrayList<String> newBackStrings = new ArrayList<>();
        Elements elements = Jsoup.parse(html).select(".noteText");
        for (Element element : elements) {
            String word = element.text();
            if (word.length() > 0) {
                newBackStrings.add(word);
            }
        }

        if (newBackStrings.size() > 0) {
            ArrayList<CardEntity> newCards = new ArrayList<>();
            for (String back : newBackStrings) {
                Log.i(TAG, "importing Card with text " + back);
                CardEntity card = new CardEntity()
                        .setCreatedAt(System.currentTimeMillis())
                        .setBack(back);
                newCards.add(card);
            }
            cardRepository.saveMany(newCards);
        }
    }
}
