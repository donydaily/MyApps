package com.donydaily.myapps

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageHelper {
    // Mengambil kode bahasa yang sedang aktif ("id" atau "en")
    fun getCurrentLanguage(): String {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        return if (!currentLocales.isEmpty) {
            currentLocales.get(0)?.language ?: Locale.getDefault().language
        } else {
            // Default mengikuti bahasa sistem HP
            Locale.getDefault().language
        }
    }

    // Mengubah bahasa aplikasi secara instan
    fun setLanguage(languageCode: String) {
        val appLocales = if (languageCode.isEmpty()) {
            LocaleListCompat.getEmptyLocaleList() // Ikuti Sistem HP
        } else {
            LocaleListCompat.forLanguageTags(languageCode)
        }
        AppCompatDelegate.setApplicationLocales(appLocales)
    }
}

