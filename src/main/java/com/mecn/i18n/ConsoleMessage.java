package com.mecn.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ConsoleMessage {

    private static final String BASE_NAME = "i18n.messages";
    private final Locale locale;
    private final ResourceBundle bundle;

    public ConsoleMessage() {
        this(Locale.getDefault());
    }

    public ConsoleMessage(Locale locale) {
        this.locale = resolveLocale(locale);
        this.bundle = ResourceBundle.getBundle(BASE_NAME, this.locale);
    }

    private static Locale resolveLocale(Locale requested) {
        String lang = requested.getLanguage();
        if ("zh".equals(lang) || "en".equals(lang)) {
            return new Locale(lang);
        }
        return Locale.ENGLISH;
    }

    public String get(String key, Object... args) {
        try {
            String pattern = bundle.getString(key);
            if (args.length > 0) {
                return MessageFormat.format(pattern, args);
            }
            return pattern;
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public String raw(String key) {
        return get(key);
    }

    public String fmt(String key, Object... args) {
        return get(key, args);
    }
}