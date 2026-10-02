package com.example.ui.screens.quran

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.BookmarkEntity
import com.example.data.model.Surah
import com.example.data.repository.QuranRepository
import com.example.ui.components.MihrabArchShape
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight

enum class QuranDisplayMode {
    ARABIC_ONLY,
    ARABIC_ENGLISH,
    ARABIC_URDU,
    ALL
}

@Composable
fun SurahReaderScreen(
    surah: Surah,
    ayahs: List<Ayah>,
    initialAyahNumber: Int = 1,
    currentPlayingAyah: Int = 0,
    isPlaying: Boolean = false,
    bookmarks: List<BookmarkEntity>,
    onPlayAyahAudio: (Ayah) -> Unit,
    onSaveBookmark: (surahNumber: Int, surahName: String, ayahNumber: Int, note: String) -> Unit,
    lang: String
) {
    val context = LocalContext.current
    var displayMode by remember { mutableStateOf(QuranDisplayMode.ALL) }
    var bookmarkingAyah by remember { mutableStateOf<Ayah?>(null) }
    var bookmarkNote by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    LaunchedEffect(initialAyahNumber) {
        if (initialAyahNumber > 1 && ayahs.isNotEmpty()) {
            val targetIndex = (initialAyahNumber - 1).coerceIn(0, ayahs.size - 1)
            listState.animateScrollToItem(targetIndex + 1) // +1 because item 0 is Surah header
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Translation View Mode Selector Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ModeChip(
                    text = AppStrings.get("arabic_only", lang),
                    selected = displayMode == QuranDisplayMode.ARABIC_ONLY,
                    onClick = { displayMode = QuranDisplayMode.ARABIC_ONLY }
                )
                ModeChip(
                    text = AppStrings.get("with_english", lang),
                    selected = displayMode == QuranDisplayMode.ARABIC_ENGLISH,
                    onClick = { displayMode = QuranDisplayMode.ARABIC_ENGLISH }
                )
                ModeChip(
                    text = AppStrings.get("with_urdu", lang),
                    selected = displayMode == QuranDisplayMode.ARABIC_URDU,
                    onClick = { displayMode = QuranDisplayMode.ARABIC_URDU }
                )
                ModeChip(
                    text = AppStrings.get("all_translations", lang),
                    selected = displayMode == QuranDisplayMode.ALL,
                    onClick = { displayMode = QuranDisplayMode.ALL }
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Surah Decorative Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MihrabArchShape)
                        .background(
                            Brush.linearGradient(
                                listOf(IslamicEmerald, IslamicEmeraldDark)
                            )
                        )
                        .border(1.5.dp, IslamicGoldLight.copy(alpha = 0.7f), MihrabArchShape)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = surah.nameArabic,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${surah.nameEnglish} • Surah ${surah.number}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Para ${surah.paraNumber} • ${surah.totalAyahs} Ayahs • ${surah.revelationType}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        // Bismillah (except Surah At-Tawbah #9)
                        if (surah.number != 9) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = IslamicGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGoldLight.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Ayahs
            items(ayahs) { ayah ->
                val isCurrentlyPlaying = currentPlayingAyah == ayah.ayahNumberInSurah && isPlaying
                val isBookmarked = bookmarks.any { it.surahNumber == surah.number && it.ayahNumber == ayah.ayahNumberInSurah }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            if (isCurrentlyPlaying) 2.dp else 1.dp,
                            if (isCurrentlyPlaying) IslamicGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Top toolbar: Ayah number & Action icons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ayah Number badge
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrentlyPlaying) IslamicGold else IslamicEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${ayah.ayahNumberInSurah}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrentlyPlaying) Color.Black else IslamicEmerald
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Bookmark icon
                                IconButton(
                                    onClick = {
                                        bookmarkingAyah = ayah
                                        bookmarkNote = if (isBookmarked) "Revision needed" else "Revise daily Sabaq"
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isBookmarked) IslamicGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Audio recitation play button
                                IconButton(
                                    onClick = { onPlayAyahAudio(ayah) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCurrentlyPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Listen to Recitation",
                                        tint = IslamicEmerald,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Arabic Calligraphic Text
                        Text(
                            text = ayah.textArabic,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right,
                            lineHeight = 36.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Urdu Translation
                        if (displayMode == QuranDisplayMode.ARABIC_URDU || displayMode == QuranDisplayMode.ALL) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = ayah.textUrdu,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Right,
                                lineHeight = 22.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // English Translation
                        if (displayMode == QuranDisplayMode.ARABIC_ENGLISH || displayMode == QuranDisplayMode.ALL) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = ayah.textEnglish,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 19.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Bookmark note dialog
    bookmarkingAyah?.let { ayah ->
        AlertDialog(
            onDismissRequest = { bookmarkingAyah = null },
            title = { Text("Bookmark Ayah ${ayah.ayahNumberInSurah}") },
            text = {
                Column {
                    Text("Add an optional study note for Ustadha or daily revision:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bookmarkNote,
                        onValueChange = { bookmarkNote = it },
                        placeholder = { Text("e.g. Revise tomorrow / Tajweed noon sakin") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveBookmark(surah.number, surah.nameEnglish, ayah.ayahNumberInSurah, bookmarkNote)
                        Toast.makeText(context, "Ayah ${ayah.ayahNumberInSurah} bookmarked!", Toast.LENGTH_SHORT).show()
                        bookmarkingAyah = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                ) {
                    Text("Save Bookmark")
                }
            },
            dismissButton = {
                TextButton(onClick = { bookmarkingAyah = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (selected) IslamicEmerald else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}
