package com.example.ui.screens.teacher

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.AyahGrade
import com.example.data.model.User
import com.example.data.repository.QuranRepository
import com.example.ui.components.AyahGradeBadge
import com.example.ui.components.MihrabArchShape
import com.example.ui.components.MihrabCard
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.StatusCorrectGreen
import com.example.ui.theme.StatusMistakeRed
import com.example.ui.theme.StatusPracticeAmber

@Composable
fun TeacherListeningSessionScreen(
    student: User,
    quranRepository: QuranRepository,
    lang: String,
    onPlayAudio: (Ayah) -> Unit,
    onCompleteSession: (
        correctCount: Int,
        mistakeCount: Int,
        needsPracticeCount: Int,
        remarks: String,
        advancePointer: Boolean,
        endAyah: Int
    ) -> Unit,
    onCancel: () -> Unit
) {
    val ayahs = remember(student.currentSurah) {
        quranRepository.getAyahsForSurah(student.currentSurah)
    }

    // Per-Ayah grading state map: AyahNumber -> AyahGrade
    val ayahGrades = remember {
        mutableStateMapOf<Int, AyahGrade>().apply {
            ayahs.forEach { ayah ->
                this[ayah.ayahNumberInSurah] = AyahGrade.UNGRADED
            }
        }
    }

    var teacherRemarks by remember { mutableStateOf("Masha'Allah, good fluency. Keep practicing Tajweed rules on noon sakin and tanween.") }
    var advancePointer by remember { mutableStateOf(true) }

    val correctCount = ayahGrades.values.count { it == AyahGrade.CORRECT }
    val mistakeCount = ayahGrades.values.count { it == AyahGrade.MISTAKE }
    val practiceCount = ayahGrades.values.count { it == AyahGrade.NEEDS_PRACTICE }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Session Header Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), MihrabArchShape),
            shape = MihrabArchShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Live Listening: ${student.name}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Para ${student.currentPara} • ${student.currentSurahName} (Starting Ayah ${student.currentAyah})",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        color = StatusCorrectGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = StatusCorrectGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Counters Row: Correct, Mistake, Needs Practice
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScoreCounterBadge(
                        label = AppStrings.get("correct", lang),
                        count = correctCount,
                        color = StatusCorrectGreen,
                        modifier = Modifier.weight(1f)
                    )
                    ScoreCounterBadge(
                        label = AppStrings.get("mistake", lang),
                        count = mistakeCount,
                        color = StatusMistakeRed,
                        modifier = Modifier.weight(1f)
                    )
                    ScoreCounterBadge(
                        label = AppStrings.get("needs_practice", lang),
                        count = practiceCount,
                        color = StatusPracticeAmber,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ayahs List for Listening
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ayahs) { ayah ->
                val currentGrade = ayahGrades[ayah.ayahNumberInSurah] ?: AyahGrade.UNGRADED
                val isCurrentPointer = ayah.ayahNumberInSurah == student.currentAyah

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            if (isCurrentPointer) 2.dp else 1.dp,
                            if (isCurrentPointer) IslamicGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrentPointer) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(IslamicEmerald.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${ayah.ayahNumberInSurah}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IslamicEmerald
                                    )
                                }
                                if (isCurrentPointer) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = IslamicGold.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Starting Pointer",
                                            fontSize = 10.sp,
                                            color = IslamicGoldLight,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AyahGradeBadge(grade = currentGrade)
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { onPlayAudio(ayah) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Listen to Qari",
                                        tint = IslamicGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Arabic Verse (Centered, Uthmani style calligraphic presentation)
                        Text(
                            text = ayah.textArabic,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 34.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Urdu Translation
                        Text(
                            text = ayah.textUrdu,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // One-tap Grade marking buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Correct button
                            Button(
                                onClick = { ayahGrades[ayah.ayahNumberInSurah] = AyahGrade.CORRECT },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentGrade == AyahGrade.CORRECT) StatusCorrectGreen else StatusCorrectGreen.copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (currentGrade == AyahGrade.CORRECT) Color.White else StatusCorrectGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    AppStrings.get("correct", lang),
                                    fontSize = 11.sp,
                                    color = if (currentGrade == AyahGrade.CORRECT) Color.White else StatusCorrectGreen
                                )
                            }

                            // Mistake button
                            Button(
                                onClick = { ayahGrades[ayah.ayahNumberInSurah] = AyahGrade.MISTAKE },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentGrade == AyahGrade.MISTAKE) StatusMistakeRed else StatusMistakeRed.copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (currentGrade == AyahGrade.MISTAKE) Color.White else StatusMistakeRed
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    AppStrings.get("mistake", lang),
                                    fontSize = 11.sp,
                                    color = if (currentGrade == AyahGrade.MISTAKE) Color.White else StatusMistakeRed
                                )
                            }

                            // Needs Practice button
                            Button(
                                onClick = { ayahGrades[ayah.ayahNumberInSurah] = AyahGrade.NEEDS_PRACTICE },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentGrade == AyahGrade.NEEDS_PRACTICE) StatusPracticeAmber else StatusPracticeAmber.copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Icon(
                                    Icons.Default.PriorityHigh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (currentGrade == AyahGrade.NEEDS_PRACTICE) Color.White else StatusPracticeAmber
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Practice",
                                    fontSize = 11.sp,
                                    color = if (currentGrade == AyahGrade.NEEDS_PRACTICE) Color.White else StatusPracticeAmber
                                )
                            }
                        }
                    }
                }
            }

            // Bottom session completion section
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = AppStrings.get("teacher_remarks", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = teacherRemarks,
                            onValueChange = { teacherRemarks = it },
                            placeholder = { Text("Write Tajweed advice, Makhraj corrections, or commendations...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = advancePointer,
                                onCheckedChange = { advancePointer = it },
                                colors = CheckboxDefaults.colors(checkedColor = IslamicEmerald)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppStrings.get("advance_pointer", lang),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val highestGradedAyah = ayahGrades.filter { it.value != AyahGrade.UNGRADED }.keys.maxOrNull() ?: student.currentAyah
                                onCompleteSession(
                                    correctCount,
                                    mistakeCount,
                                    practiceCount,
                                    teacherRemarks,
                                    advancePointer,
                                    highestGradedAyah
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save & Mark Attendance Present",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ScoreCounterBadge(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = color,
                maxLines = 1
            )
        }
    }
}
