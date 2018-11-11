package com.github.davsx.daspalen.service.Speaker;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class SpeakerServiceImpl implements SpeakerService {
    private TextToSpeech tts;
    private int ttsStatus = TextToSpeech.ERROR;

    public SpeakerServiceImpl(Context context) {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttsStatus = status;
            }
        });
    }

    @Override
    public void setLanguage(Locale locale) {
        tts.setLanguage(locale);
    }

    @Override
    public void speak(String text) {
        if (ttsStatus == TextToSpeech.SUCCESS) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");
        }
    }
}
