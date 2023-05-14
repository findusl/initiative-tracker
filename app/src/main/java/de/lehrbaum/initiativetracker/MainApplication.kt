package de.lehrbaum.initiativetracker

import android.app.Application

class MainApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		ContextHolder.appContext = applicationContext
	}
}
