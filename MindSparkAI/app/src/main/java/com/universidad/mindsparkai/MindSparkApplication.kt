package com.universidad.mindsparkai

import android.app.Application

// @HiltAndroidApp  // ← COMENTADO TEMPORALMENTE
class MindSparkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicialización básica sin Hilt
    }
}