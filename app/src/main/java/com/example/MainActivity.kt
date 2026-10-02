package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.local.QuranDatabase
import com.example.data.repository.QuranRepository
import com.example.data.repository.StudentRepository
import com.example.ui.components.AudioPlayerManager
import com.example.ui.navigation.AppNavigation
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

  private lateinit var audioPlayerManager: AudioPlayerManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Database & DAOs
    val database = QuranDatabase.getDatabase(applicationContext)
    val studentRepository = StudentRepository(
      context = applicationContext,
      attendanceDao = database.attendanceDao(),
      sessionDao = database.sessionDao(),
      chatMessageDao = database.chatMessageDao(),
      callLogDao = database.callLogDao()
    )
    val quranRepository = QuranRepository(
      bookmarkDao = database.bookmarkDao()
    )
    audioPlayerManager = AudioPlayerManager(applicationContext)

    setContent {
      val systemInDark = isSystemInDarkTheme()
      val themeMode by studentRepository.themeMode.collectAsState()

      val isDark = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> systemInDark
      }

      MyApplicationTheme(darkTheme = isDark) {
        AppNavigation(
          studentRepository = studentRepository,
          quranRepository = quranRepository,
          audioPlayerManager = audioPlayerManager,
          isDarkTheme = isDark,
          onToggleDarkTheme = {
            studentRepository.setThemeMode(if (isDark) "light" else "dark")
          }
        )
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (::audioPlayerManager.isInitialized) {
      audioPlayerManager.release()
    }
  }
}
