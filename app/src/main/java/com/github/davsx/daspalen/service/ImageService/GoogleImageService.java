package com.github.davsx.daspalen.service.ImageService;

import android.net.Uri;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.service.Settings.SettingsService;
import com.squareup.okhttp.Request;

public class GoogleImageService {

    public static Request getRequest(SettingsService settings, String queryText) {
        if (settings.getImageSearchApiKey() == null
                || settings.getImageSearchCxKey() == null
                || settings.getImageSearchApiKey().equals("")
                || settings.getImageSearchCxKey().equals("")) {
            return null;
        }

        String url = String.format(
                "https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&q=%s&searchType=image&num=8&fileType=png",
                settings.getImageSearchApiKey(),
                settings.getImageSearchCxKey(),
                Uri.encode(queryText)
        );

        return new Request.Builder()
                .url(url)
                .tag(DaspalenConstants.OKHTTP_TAG)
                .build();
    }

}
