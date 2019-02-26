// Copyright (c) 2018, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.external;

import android.content.Context;
import android.os.LocaleList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings({
    "PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal",
    "PMD.AvoidSynchronizedAtMethodLevel",
    "PMD.AvoidUsingVolatile"
})
public class LocaleResolver {

    private static final Set<Locale> SUPPORTED_LOCALES = new HashSet<>(Arrays.asList(

        Locale.ENGLISH,
        // catalan
        new Locale("ca", "ES"),
        // spanish
        new Locale("es", "ES"),
        new Locale("es", "AR"),
        new Locale("es", "BO"),
        new Locale("es", "CL"),
        new Locale("es", "CO"),
        new Locale("es", "CR"),
        new Locale("es", "DO"),
        new Locale("es", "EC"),
        new Locale("es", "SV"),
        new Locale("es", "GT"),
        new Locale("es", "HN"),
        new Locale("es", "MX"),
        new Locale("es", "NI"),
        new Locale("es", "PA"),
        new Locale("es", "PY"),
        new Locale("es", "PE"),
        new Locale("es", "PR"),
        new Locale("es", "ES"),
        new Locale("es", "US"),
        new Locale("es", "UY"),
        new Locale("es", "VE"),
        // russian
        new Locale("ru", "RU"),
        // dutch
        new Locale("nl", "BE"),
        new Locale("nl", "NL")
    ));

    private static volatile LocaleResolver instance;

    private LocaleResolver() {
        // only purpose is to defeat external instantiation
    }

    public static synchronized LocaleResolver get() {

        if (instance == null) {
            instance = new LocaleResolver();
        }
        return instance;
    }

    // LOCALE

    public Locale getSafeLocale(final Context context) {
        LocaleList locales = context.getResources().getConfiguration().getLocales();
        return (locales.isEmpty() || !SUPPORTED_LOCALES.contains(locales.get(0))) ?
            Locale.ENGLISH : locales.get(0);
    }
}
