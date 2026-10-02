package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.QuranRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Quran Sunao", appName)
  }

  @Test
  fun `verify Quran repository indexes 114 Surahs and 30 Paras`() {
    assertEquals(114, QuranRepository.surahsList.size)
    assertEquals(30, QuranRepository.parasList.size)
    
    val surahFatihah = QuranRepository.surahsList.first()
    assertEquals("Al-Fatihah", surahFatihah.nameEnglish)
    assertEquals(7, surahFatihah.totalAyahs)
    
    val paraOne = QuranRepository.parasList.first()
    assertEquals(1, paraOne.number)
  }
}
