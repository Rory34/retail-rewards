package com.rorysteerprojects.retailrewards.config;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceLookup {

    public static String getMessage(String key) {
        Locale locale = new Locale("en", "US");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);

        try {
            return resourceBundle.getString(key);
        }
        catch (MissingResourceException e) {
            return "????" + key;
        }
    }
}
