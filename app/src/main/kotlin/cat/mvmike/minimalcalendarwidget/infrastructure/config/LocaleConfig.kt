// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.config

import android.content.Context
import java.util.Locale

object LocaleConfig {

    private val supportedLocales: Set<Locale> = setOf(
        Locale.ENGLISH,
        Locale("ca"), // catalan
        Locale("hr"), // croatian
        Locale("nl"), // dutch
        Locale("eo"), // esperanto
        Locale("fr"), // french
        Locale("de"), // german
        Locale("lt"), // lithuanian
        Locale("nb"), // norwegian
        Locale("pl"), // polish
        Locale("pt"), // portuguese
        Locale("pa"), // punjabi
        Locale("ru"), // russian
        Locale("es") // spanish
    )

    fun getLocale(context: Context): Locale = context.resources.configuration.locales
        .takeIf { !it.isEmpty }
        ?.let { supportedLocales.firstOrNull { sl -> sl.language == it[0].language } }
        ?: Locale.ENGLISH

}
