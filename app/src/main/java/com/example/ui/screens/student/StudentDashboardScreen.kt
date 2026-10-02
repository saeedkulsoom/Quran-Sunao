package com.example.ui.screens.student

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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.ListeningSessionEntity
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.User
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.IslamicHeroCard
import com.example.ui.components.MihrabArchShape
import com.example.ui.components.MihrabCard
import com.example.ui.screens.teacher.LivelyNotificationRow
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.StatusCorrectGreen
import com.example.ui.theme.StatusMistakeRed
import com.example.ui.theme.StatusPracticeAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentDashboardScreen(
    student: User,
    recentSessions: List<ListeningSessionEntity>,
    attendanceRecords: List<AttendanceEntity>,
    notifications: List<NotificationItem> = emptyList(),
    lang: String,
    onContinueSabaq: (surahNumber: Int, ayahNumber: Int) -> Unit,
    onOpenChat: () -> Unit,
    onOpenQuranBrowser: () -> Unit,
    onMarkNotificationRead: (String) -> Unit = {},
    onDismissNotification: (String) -> Unit = {},
    onMarkAllNotificationsRead: () -> Unit = {}
) {
    // Progress calculation
    val totalParas = 30
    val currentPara = student.currentPara.coerceIn(1, 30)
    val progressFraction = (currentPara.toFloat() / totalParas).coerceIn(0.05f, 1f)

    // Attendance stats
    val totalDays = attendanceRecords.size
    val presentDays = attendanceRecords.count { it.status == AttendanceStatus.PRESENT }
    val attendanceRate = if (totalDays > 0) (presentDays * 100 / totalDays) else 100

    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todayAttendance = attendanceRecords.find { it.date == todayDate }?.status ?: AttendanceStatus.ABSENT

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            IslamicHeroCard(
                title = "Assalamu Alaikum, ${student.name.split(" ").firstOrNull() ?: student.name}!",
                subtitle = "Your daily Qur'an, kept simple. Revise your daily Sabaq and stay consistent with Allah's words.",
                badgeText = "Student Portal"
            )
        }

        // Current Sabaq Pointer (Signature Mihrab Card)
        item {
            MihrabCard(borderColor = IslamicGold) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppStrings.get("current_sabaq", lang).uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = student.currentSurahName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Surface(
                        color = IslamicGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("AYAH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                            Text("${student.currentAyah}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Juz / Para ${student.currentPara} • Lesson active",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Today: ${if (todayAttendance == AttendanceStatus.PRESENT) "Completed" else "Pending"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (todayAttendance == AttendanceStatus.PRESENT) StatusCorrectGreen else StatusPracticeAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Primary Action: Continue Sabaq
                Button(
                    onClick = { onContinueSabaq(student.currentSurah, student.currentAyah) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue Sabaq & Recite (Ayah ${student.currentAyah})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Student's Lively Notification Center
        if (notifications.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(IslamicEmerald.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Book,
                                        contentDescription = null,
                                        tint = IslamicEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = AppStrings.get("notifications", lang),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            val unreadCount = notifications.count { !it.isRead }
                            if (unreadCount > 0) {
                                TextButton(onClick = onMarkAllNotificationsRead) {
                                    Text(
                                        text = AppStrings.get("mark_all_read", lang),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = IslamicEmerald
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            notifications.take(3).forEach { notif ->
                                LivelyNotificationRow(
                                    notification = notif,
                                    onReadClick = { onMarkNotificationRead(notif.id) },
                                    onDismiss = { onDismissNotification(notif.id) },
                                    onAction = {
                                        onMarkNotificationRead(notif.id)
                                        when (notif.type) {
                                            NotificationType.SABAQ -> onContinueSabaq(student.currentSurah, student.currentAyah)
                                            NotificationType.CALL, NotificationType.CHAT -> onOpenChat()
                                            NotificationType.ATTENDANCE -> {}
                                            NotificationType.HADITH -> onOpenQuranBrowser()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Qur'an Progress & Attendance Dual Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Progress Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Qur'an Progress",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Para $currentPara of 30",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmerald
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = IslamicGold,
                            trackColor = IslamicGold.copy(alpha = 0.2f)
                        )
                    }
                }

                // Attendance Rate Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Attendance",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$attendanceRate%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusCorrectGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$presentDays of $totalDays days present",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Recent Teacher Remarks and Tajweed feedback
        item {
            Text(
                text = "Teacher's Recent Remarks & Tajweed Notes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (recentSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your listening session records will appear here as Ustadha marks your recitations.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(recentSessions.take(3)) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${session.surahName} (Ayahs ${session.startAyah}-${session.endAyah})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = session.dateString,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScorePill(label = "Correct", count = session.correctCount, color = StatusCorrectGreen)
                            ScorePill(label = "Mistakes", count = session.mistakeCount, color = StatusMistakeRed)
                            ScorePill(label = "Needs Practice", count = session.needsPracticeCount, color = StatusPracticeAmber)
                        }

                        if (session.remarks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Ustadha: \"${session.remarks}\"",
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Bottom Action: Ask Ustadha
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = IslamicEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask Ustadha", color = MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = onOpenQuranBrowser,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Browse Qur'an", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ScorePill(label: String, count: Int, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = "$label: $count",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
