package com.github.davsx.llearn.data.KindleImport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class KindleImportReceiver {
    private Intent intent;
    private Context context;

    public KindleImportReceiver(Context context, Intent intent) {
        this.intent = intent;
        this.context = context;
    }

    public ArrayList<String> receiveBackStrings() {
        ArrayList<Uri> uris = intent.getExtras().getParcelableArrayList(Intent.EXTRA_STREAM);
        if (uris.size() != 1) {
            return null;
        }

        Uri uri = uris.get(0);

        String html;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            html = builder.toString();
        } catch (Exception e) {
            return null;
        }

        ArrayList<String> newBackStrings = new ArrayList<>();
        Elements elements = Jsoup.parse(html).select(".noteText");
        for (Element element : elements) {
            String word = element.text();
            if (word.length() > 0) {
                newBackStrings.add(word);
                Log.d("DAVS", "KindleImport word " + word);
            }
        }

        if (newBackStrings.size() > 0) {

        }
        return newBackStrings;
    }
}
