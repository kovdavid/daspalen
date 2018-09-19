package com.github.davsx.llearn.service.Speaker;

import java.util.Locale;

public interface SpeakerService {
    void setLanguage(Locale locale);
    void speak(String text);
}