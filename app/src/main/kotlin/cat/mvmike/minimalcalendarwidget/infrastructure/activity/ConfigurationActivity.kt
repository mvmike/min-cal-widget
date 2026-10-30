// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.infrastructure.activity

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import cat.mvmike.minimalcalendarwidget.R
import cat.mvmike.minimalcalendarwidget.domain.configuration.clearAllConfiguration
import cat.mvmike.minimalcalendarwidget.domain.intent.getAppWidgetId
import cat.mvmike.minimalcalendarwidget.infrastructure.fragment.SettingsFragment
import cat.mvmike.minimalcalendarwidget.infrastructure.resolver.SystemResolver.isDarkThemeEnabled

class ConfigurationActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, appWidgetId: Int) = context.startActivity(
            Intent(context, ConfigurationActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
        )
    }

    private val appWidgetId
        get() = intent?.getAppWidgetId() ?: INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(
            when (applicationContext.isDarkThemeEnabled()) {
                true -> androidx.appcompat.R.style.Theme_AppCompat
                else -> androidx.appcompat.R.style.Theme_AppCompat_DayNight
            }
        )

        super.onCreate(savedInstanceState)

        setContentView(R.layout.configuration)
        showSettings()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        showSettings()
    }

    @Suppress("unused_parameter")
    fun onClickResetSettingsButton(view: View) =
        clearAllConfiguration(applicationContext, appWidgetId)

    @Suppress("unused_parameter")
    fun onClickCloseSettingsButton(view: View) {
        setResult(RESULT_OK, Intent().putExtra(EXTRA_APPWIDGET_ID, appWidgetId))
        finishAfterTransition()
    }

    private fun showSettings() {
        if (appWidgetId == INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.configuration_view, SettingsFragment().apply { arguments = Bundle().apply { putInt(EXTRA_APPWIDGET_ID, appWidgetId) } })
            .commit()
    }
}