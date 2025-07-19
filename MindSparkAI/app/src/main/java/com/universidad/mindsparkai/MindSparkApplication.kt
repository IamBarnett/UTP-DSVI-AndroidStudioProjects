package com.universidad.mindsparkai

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MindSparkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicialización de la aplicación
        setupAppConfiguration()
    }

    private fun setupAppConfiguration() {
        // Configuraciones globales de la app

        // Configurar handlers de excepciones no capturadas
        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            // Log de errores críticos
            android.util.Log.e("MindSparkAI", "Uncaught exception", exception)
        }

        // Configurar cualquier librería global aquí
        // Por ejemplo: Crashlytics, Analytics, etc.
    }
}