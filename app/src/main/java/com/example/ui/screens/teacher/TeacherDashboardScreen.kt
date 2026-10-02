package com.example.ui.screens.teacher

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.User
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.IslamicHeroCard
import com.example.ui.components.MihrabArchShape
import com.example.ui.components.MihrabCard
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.StatusCorrectGreen
import com.example.ui.theme.StatusLeaveBlue
import com.example.ui.theme.StatusMistakeRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeacherDashboardScreen(
    students: List<User>,
    todayAttendance: List<AttendanceEntity>,
    notifications: List<NotificationItem>,
    lang: String,
    onStartListening: (User) -> Unit,
    onStartCall: (User) -> Unit,
    onOpenChat: (User) -> Unit,
    onCycleAttendance: (User) -> Unit,
    onMarkNotificationRead: (String) -> Unit,
    onMarkAllNotificationsRead: () -> Unit,
    onDismissNotification: (String) -> Unit,
    onNavigateToAttendanceSheet: () -> Unit,
    onNavigateToQuran: () -> Unit,
    onAddStudentClick: () -> Unit
) {
    var selectedNotifFilter by remember { mutableStateOf("All") }

    val presentCount = todayAttendance.count { it.status == AttendanceStatus.PRESENT }
    val absentCount = todayAttendance.count { it.status == AttendanceStatus.ABSENT }
    val leaveCount = todayAttendance.count { it.status == AttendanceStatus.LEAVE }
    val totalStudents = students.size
    val pendingCount = (totalStudents - presentCount).coerceAtLeast(0)

    val unreadNotifsCount = notifications.count { !it.isRead }

    // Filter notifications
    val filteredNotifications = remember(notifications, selectedNotifFilter) {
        when (selectedNotifFilter) {
            "Sabaq" -> notifications.filter { it.type == NotificationType.SABAQ }
            "Calls" -> notifications.filter { it.type == NotificationType.CALL }
            "Attendance" -> notifications.filter { it.type == NotificationType.ATTENDANCE }
            "Chat" -> notifications.filter { it.type == NotificationType.CHAT }
            else -> notifications
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Greeting Banner with Hijri Date
        item {
            Spacer(modifier = Modifier.height(4.dp))
            IslamicHeroCard(
                title = "Assalamu Alaikum, Ustadha Aminah!",
                subtitle = "Today you have $pendingCount Sabaq listening sessions pending. May Allah bless your teachings.",
                badgeText = "Today • ${SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date())}"
            )
        }

        // 2. Lively Key Metrics Row with Visual Progress
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    number = totalStudents.toString(),
                    label = AppStrings.get("total_students", lang),
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    textColor = MaterialTheme.colorScheme.primary
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    number = "$presentCount/$totalStudents",
                    label = AppStrings.get("today_attendance", lang),
                    containerColor = StatusCorrectGreen.copy(alpha = 0.12f),
                    textColor = StatusCorrectGreen
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    number = pendingCount.toString(),
                    label = AppStrings.get("pending_sessions", lang),
                    containerColor = IslamicGold.copy(alpha = 0.15f),
                    textColor = IslamicGoldLight
                )
            }
        }

        // 3. Lively Notification Center Hub
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Notification Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(IslamicEmerald.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (unreadNotifsCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (unreadNotifsCount > 0) IslamicEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppStrings.get("notifications", lang),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (unreadNotifsCount > 0) "$unreadNotifsCount unread alerts" else "All caught up!",
                                    fontSize = 12.sp,
                                    color = if (unreadNotifsCount > 0) IslamicEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (unreadNotifsCount > 0) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }

                        if (unreadNotifsCount > 0) {
                            TextButton(
                                onClick = onMarkAllNotificationsRead,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = IslamicEmerald
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = AppStrings.get("mark_all_read", lang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = IslamicEmerald
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter chips row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val filters = listOf("All", "Sabaq", "Calls", "Attendance", "Chat")
                        items(filters) { filter ->
                            FilterChip(
                                selected = selectedNotifFilter == filter,
                                onClick = { selectedNotifFilter = filter },
                                label = {
                                    Text(
                                        text = filter,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedNotifFilter == filter) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslamicEmerald.copy(alpha = 0.15f),
                                    selectedLabelColor = IslamicEmerald
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notifications List Feed
                    if (filteredNotifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No notifications in this category",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            filteredNotifications.forEach { notif ->
                                LivelyNotificationRow(
                                    notification = notif,
                                    onReadClick = { onMarkNotificationRead(notif.id) },
                                    onDismiss = { onDismissNotification(notif.id) },
                                    onAction = {
                                        onMarkNotificationRead(notif.id)
                                        when (notif.type) {
                                            NotificationType.SABAQ -> {
                                                val targetStd = students.find { it.id == notif.studentId }
                                                    ?: students.firstOrNull()
                                                targetStd?.let { onStartListening(it) }
                                            }
                                            NotificationType.CALL -> {
                                                val targetStd = students.find { it.id == notif.studentId }
                                                    ?: students.firstOrNull()
                                                targetStd?.let { onStartCall(it) }
                                            }
                                            NotificationType.ATTENDANCE -> {
                                                onNavigateToAttendanceSheet()
                                            }
                                            NotificationType.CHAT -> {
                                                val targetStd = students.find { it.id == notif.studentId }
                                                    ?: students.firstOrNull()
                                                targetStd?.let { onOpenChat(it) }
                                            }
                                            NotificationType.HADITH -> {
                                                onNavigateToQuran()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Daily Hadith & Motivation Card
        item {
            MihrabCard(borderColor = IslamicGold) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IslamicGold.copy(alpha = 0.25f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = IslamicGoldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = AppStrings.get("daily_ayah", lang),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGoldLight
                                )
                            }
                        }
                        Text(
                            text = "Surah Al-Muzzammil 73:4",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "“And recite the Qur'an with measured, beautiful recitation.”",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 5. Daily Sabaq Queue Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Student Recitations",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Tap to listen, call or update attendance",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onNavigateToAttendanceSheet() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Full Sheet",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 6. Student Sabaq Action Cards
        items(students) { student ->
            val attendanceRecord = todayAttendance.find { it.studentId == student.id }
            val currentStatus = attendanceRecord?.status ?: AttendanceStatus.ABSENT

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Student Avatar with Mihrab clip
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(MihrabArchShape)
                            .background(IslamicEmerald)
                            .border(1.5.dp, IslamicGold, MihrabArchShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Info: Name, Sabaq Pointer, Attendance Badge
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = student.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Attendance Badge (clickable to cycle)
                            AttendanceBadge(
                                status = currentStatus,
                                modifier = Modifier.clickable { onCycleAttendance(student) }
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // Sabaq location pointer
                        Text(
                            text = "Para ${student.currentPara} • ${student.currentSurahName}, Ayah ${student.currentAyah}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )

                        if (student.parentContact.isNotBlank()) {
                            Text(
                                text = student.parentContact,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Action buttons: Listen Sabaq (Primary), Video Call, WhatsApp Chat
                    IconButton(
                        onClick = { onStartListening(student) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IslamicEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = "Listen Sabaq",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { onStartCall(student) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IslamicGold.copy(alpha = 0.2f))
                            .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Student",
                            tint = IslamicGoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { onOpenChat(student) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat with Student",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun LivelyNotificationRow(
    notification: NotificationItem,
    onReadClick: () -> Unit,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    val iconColor = when (notification.type) {
        NotificationType.SABAQ -> IslamicEmerald
        NotificationType.CALL -> IslamicGold
        NotificationType.ATTENDANCE -> Color(0xFF1976D2)
        NotificationType.CHAT -> Color(0xFF00897B)
        NotificationType.HADITH -> Color(0xFF8E24AA)
    }

    val iconVector = when (notification.type) {
        NotificationType.SABAQ -> Icons.Default.Hearing
        NotificationType.CALL -> Icons.Default.VideoCall
        NotificationType.ATTENDANCE -> Icons.Default.CheckCircle
        NotificationType.CHAT -> Icons.Default.Chat
        NotificationType.HADITH -> Icons.Default.AutoAwesome
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (!notification.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (!notification.isRead) IslamicEmerald.copy(alpha = 0.4f) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onReadClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon Pill
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 13.sp,
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = notification.timestamp,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = notification.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (notification.actionText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAction,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = iconColor)
                    ) {
                        Text(
                            text = notification.actionText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    number: String,
    label: String,
    containerColor: Color,
    textColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = number,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = textColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
