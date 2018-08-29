package com.github.davsx.llearn.data.KindleImport;

import android.content.Intent;
import android.net.Uri;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class KindleImportReceiver {
    private Intent intent;

    public KindleImportReceiver(Intent intent) {
        this.intent = intent;
    }

    public ArrayList<String> receiveBackStrings() {
        ArrayList<Uri> uris = intent.getExtras().getParcelableArrayList(Intent.EXTRA_STREAM);
        if (uris.size() != 1) {
            return null;
        }

        Uri uri = uris.get(0);

        String html = "";
        try {

        } catch (Exception e) {
            return null;
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

        }
        return newBackStrings;
    }
}
