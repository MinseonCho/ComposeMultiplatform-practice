package data.datasource

import java.util.prefs.Preferences

class PreferencesDataSource {
    private val prefs = Preferences.userRoot().node("cholink_tester")

    fun getAdbPath(): String {
        return prefs.get(KEY_ADB_PATH, "")
    }

    fun saveAdbPath(adbPath: String) {
        prefs.put(KEY_ADB_PATH, adbPath)
        prefs.flush()
    }

    companion object {
        private const val KEY_ADB_PATH = "adbPath"
    }
} 
