package com.OrderSuperfast;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

public class ContextUtils extends ContextWrapper {
    public ContextUtils(Context base) {
        super(base);
    }

    /**
     * Funcion que cambia el idioma de la aplicación de distintas formas dependiendo de la version del SDK del dispositivo
     * @param context Contexto de la aplicación
     * @param localeToSwitchTo Nuevo "Local" al que cambiar
     * @return
     */
    public static ContextWrapper updateLocale(Context context, Locale localeToSwitchTo) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(localeToSwitchTo);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
        } else {
            configuration.locale = localeToSwitchTo;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            context = context.createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return new ContextUtils(context);
    }

}