package com.github.davsx.daspalen.service.KindleImport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.github.davsx.daspalen.model.Card;
import com.github.davsx.daspalen.persistence.repository.DaspalenRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class KindleImportService {

    private static final String TAG = "KindleImportService";

    private Context context;
    private DaspalenRepository repository;

    public KindleImportService(DaspalenRepository repository, Context context) {
        this.repository = repository;
        this.context = context;
    }

    public void doImport(Intent intent) {
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
            ArrayList<Card> newCards = new ArrayList<>();
            for (String back : newBackStrings) {
                Log.i(TAG, "importing Card with back text " + back);
                newCards.add(Card.createNew("", back));
            }
            repository.createNewCards(newCards);
        }
    }
}
