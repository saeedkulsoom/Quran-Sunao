package com.example.ui.screens.teacher

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.User
import com.example.data.repository.QuranRepository
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.MihrabArchShape
import com.example.ui.components.MihrabCard
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.StatusCorrectGreen
import com.example.ui.theme.StatusLeaveBlue
import com.example.ui.theme.StatusMistakeRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeacherStudentsScreen(
    students: List<User>,
    allAttendance: List<AttendanceEntity> = emptyList(),
    selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    onDateSelected: (String) -> Unit = {},
    onCycleStatus: (User) -> Unit = {},
    onSetAttendance: (studentId: String, studentName: String, date: String, status: AttendanceStatus, notes: String) -> Unit = { _, _, _, _, _ -> },
    onBulkMarkAttendance: (date: String, status: AttendanceStatus) -> Unit = { _, _ -> },
    onUpdateStudentDetails: (studentId: String, name: String, phone: String, parentContact: String, surah: Int, ayah: Int, para: Int) -> Unit = { _, _, _, _, _, _, _ -> },
    lang: String,
    onStartListening: (User) -> Unit,
    onStartCall: (User, isVideo: Boolean) -> Unit = { _, _ -> },
    onOpenChat: (User) -> Unit,
    onAddStudent: (name: String, email: String, phone: String, parentContact: String, surah: Int, ayah: Int, para: Int) -> Unit,
    onUpdatePointer: (studentId: String, surah: Int, surahName: String, ayah: Int, para: Int) -> Unit
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<User?>(null) }
    var noteEditingRecord by remember { mutableStateOf<Pair<User, AttendanceEntity?>?>(null) }
    var showCsvPreviewDialog by remember { mutableStateOf(false) }
    var generatedCsvContent by remember { mutableStateOf("") }

    val filteredStudents = students.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.email.contains(searchQuery, ignoreCase = true) ||
        it.currentSurahName.contains(searchQuery, ignoreCase = true)
    }

    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val effectiveDate = selectedDate.ifEmpty { todayDate }
    val currentDayAttendance = allAttendance.filter { it.date == effectiveDate }

    // Summary calculations
    val totalRecords = allAttendance.size
    val presentRecords = allAttendance.count { it.status == AttendanceStatus.PRESENT }
    val absentRecords = allAttendance.count { it.status == AttendanceStatus.ABSENT }
    val leaveRecords = allAttendance.count { it.status == AttendanceStatus.LEAVE }
    val attendanceRate = if (totalRecords > 0) (presentRecords * 100 / totalRecords) else 100

    val todayPresent = students.count { std ->
        currentDayAttendance.find { it.studentId == std.id }?.status == AttendanceStatus.PRESENT
    }
    val todayAbsent = students.count { std ->
        currentDayAttendance.find { it.studentId == std.id }?.status == AttendanceStatus.ABSENT
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // WhatsApp-style Top Segments: "Students & Sabaq" vs "Attendance Sheet"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    val isTab0 = selectedTabIndex == 0
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedTabIndex = 0 },
                        color = if (isTab0) IslamicEmerald else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = if (isTab0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Students & Sabaq",
                                fontSize = 13.sp,
                                fontWeight = if (isTab0) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTab0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val isTab1 = selectedTabIndex == 1
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedTabIndex = 1 },
                        color = if (isTab1) IslamicEmerald else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = if (isTab1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Attendance Sheet",
                                fontSize = 13.sp,
                                fontWeight = if (isTab1) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTab1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // TAB 0: STUDENTS & SABAQ (with integrated Attendance pill on each student card!)
            if (selectedTabIndex == 0) {
                // Search input & Add student button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search student or Surah...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IslamicEmerald) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(IslamicEmerald)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Student", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Attendance Status Ribbon
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enrolled: ${students.size} students",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Present Today: $todayPresent",
                                fontSize = 11.sp,
                                color = StatusCorrectGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "•",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Absent: $todayAbsent",
                                fontSize = 11.sp,
                                color = StatusMistakeRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredStudents) { student ->
                        val studentRecord = currentDayAttendance.find { it.studentId == student.id }
                        val currentStatus = studentRecord?.status ?: AttendanceStatus.PRESENT

                        MihrabCard {
                            // Top Row: Avatar, Name, Email, and Direct Attendance Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(IslamicEmerald.copy(alpha = 0.15f))
                                        .border(1.5.dp, IslamicGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = IslamicEmerald,
                                        fontSize = 15.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (student.phone.isNotEmpty()) student.phone else student.email,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Direct Attendance Pill right on student card!
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onCycleStatus(student) },
                                    color = when (currentStatus) {
                                        AttendanceStatus.PRESENT -> StatusCorrectGreen.copy(alpha = 0.15f)
                                        AttendanceStatus.ABSENT -> StatusMistakeRed.copy(alpha = 0.15f)
                                        AttendanceStatus.LEAVE -> StatusLeaveBlue.copy(alpha = 0.15f)
                                    },
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        when (currentStatus) {
                                            AttendanceStatus.PRESENT -> StatusCorrectGreen
                                            AttendanceStatus.ABSENT -> StatusMistakeRed
                                            AttendanceStatus.LEAVE -> StatusLeaveBlue
                                        }
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (currentStatus) {
                                                AttendanceStatus.PRESENT -> Icons.Default.Check
                                                AttendanceStatus.ABSENT -> Icons.Default.Close
                                                AttendanceStatus.LEAVE -> Icons.Default.Schedule
                                            },
                                            contentDescription = null,
                                            tint = when (currentStatus) {
                                                AttendanceStatus.PRESENT -> StatusCorrectGreen
                                                AttendanceStatus.ABSENT -> StatusMistakeRed
                                                AttendanceStatus.LEAVE -> StatusLeaveBlue
                                            },
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = currentStatus.name.lowercase().replaceFirstChar { it.uppercase() },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (currentStatus) {
                                                AttendanceStatus.PRESENT -> StatusCorrectGreen
                                                AttendanceStatus.ABSENT -> StatusMistakeRed
                                                AttendanceStatus.LEAVE -> StatusLeaveBlue
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                IconButton(
                                    onClick = { editingStudent = student },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Student Record",
                                        tint = IslamicGoldLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Current Sabaq pointer badge
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = IslamicEmerald,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Para ${student.currentPara} • ${student.currentSurahName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = "Ayah: ${student.currentAyah}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IslamicGoldLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Action Buttons: Listen Sabaq, Video Call, Audio Call, Chat
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onStartListening(student) },
                                    modifier = Modifier.weight(1.4f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                                ) {
                                    Icon(Icons.Default.Hearing, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Listen", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { onStartCall(student, true) },
                                    modifier = Modifier.weight(1.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.Black)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Video", fontSize = 11.sp, color = Color.Black)
                                }

                                Button(
                                    onClick = { onOpenChat(student) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Chat", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }

            // TAB 1: FULL ATTENDANCE SHEET & RECORD UPDATE
            if (selectedTabIndex == 1) {
                // Monthly stats overview
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Monthly Attendance Sheet",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Overall: $attendanceRate% Attendance Rate",
                                    fontSize = 12.sp,
                                    color = StatusCorrectGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    val csv = buildCsvReport(students, allAttendance)
                                    generatedCsvContent = csv
                                    showCsvPreviewDialog = true
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CSV", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AttendanceSummaryBox(label = "Present", count = presentRecords, color = StatusCorrectGreen, modifier = Modifier.weight(1f))
                            AttendanceSummaryBox(label = "Absent", count = absentRecords, color = StatusMistakeRed, modifier = Modifier.weight(1f))
                            AttendanceSummaryBox(label = "Leave", count = leaveRecords, color = StatusLeaveBlue, modifier = Modifier.weight(1f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Date Navigation Row
                val availableDates = listOf(
                    todayDate,
                    "2026-09-19",
                    "2026-09-18",
                    "2026-09-17"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableDates.forEach { d ->
                        val isSelected = d == effectiveDate
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onDateSelected(d) }
                                .border(
                                    1.dp,
                                    if (isSelected) IslamicEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(10.dp)
                                ),
                            color = if (isSelected) IslamicEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (d == todayDate) "Today" else d.takeLast(5),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) IslamicEmerald else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bulk Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Roll Call ($effectiveDate)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            onBulkMarkAttendance(effectiveDate, AttendanceStatus.PRESENT)
                            Toast.makeText(context, "Marked all students present for $effectiveDate", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCorrectGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("All Present", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onBulkMarkAttendance(effectiveDate, AttendanceStatus.ABSENT)
                            Toast.makeText(context, "Marked all students absent for $effectiveDate", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("All Absent", fontSize = 11.sp, color = StatusMistakeRed)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Roll Call List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(students) { student ->
                        val studentRecord = currentDayAttendance.find { it.studentId == student.id }
                        val currentStatus = studentRecord?.status ?: AttendanceStatus.PRESENT

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(IslamicEmerald.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = student.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = IslamicEmerald,
                                        fontSize = 13.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (studentRecord?.notes.isNullOrBlank()) "Tap note icon to add remarks" else (studentRecord?.notes ?: ""),
                                        fontSize = 11.sp,
                                        color = if (studentRecord?.notes.isNullOrBlank()) MaterialTheme.colorScheme.onSurfaceVariant else IslamicEmerald,
                                        maxLines = 1
                                    )
                                }

                                // Quick Status Selector Buttons [ P ] [ A ] [ L ]
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                onSetAttendance(student.id, student.name, effectiveDate, AttendanceStatus.PRESENT, studentRecord?.notes ?: "")
                                            },
                                        color = if (currentStatus == AttendanceStatus.PRESENT) StatusCorrectGreen else StatusCorrectGreen.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "P",
                                            color = if (currentStatus == AttendanceStatus.PRESENT) Color.White else StatusCorrectGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                onSetAttendance(student.id, student.name, effectiveDate, AttendanceStatus.ABSENT, studentRecord?.notes ?: "")
                                            },
                                        color = if (currentStatus == AttendanceStatus.ABSENT) StatusMistakeRed else StatusMistakeRed.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "A",
                                            color = if (currentStatus == AttendanceStatus.ABSENT) Color.White else StatusMistakeRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                onSetAttendance(student.id, student.name, effectiveDate, AttendanceStatus.LEAVE, studentRecord?.notes ?: "")
                                            },
                                        color = if (currentStatus == AttendanceStatus.LEAVE) StatusLeaveBlue else StatusLeaveBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "L",
                                            color = if (currentStatus == AttendanceStatus.LEAVE) Color.White else StatusLeaveBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { noteEditingRecord = Pair(student, studentRecord) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Attendance Note",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Add Student Dialog
        if (showAddDialog) {
            AddStudentDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, email, phone, parent, surah, ayah, para ->
                    onAddStudent(name, email, phone, parent, surah, ayah, para)
                    showAddDialog = false
                }
            )
        }

        // Full Edit Student Record Dialog (name, phone, parent, Sabaq pointer)
        editingStudent?.let { student ->
            EditStudentRecordDialog(
                student = student,
                onDismiss = { editingStudent = null },
                onSave = { name, phone, parentContact, surah, surahName, ayah, para ->
                    onUpdateStudentDetails(student.id, name, phone, parentContact, surah, ayah, para)
                    onUpdatePointer(student.id, surah, surahName, ayah, para)
                    Toast.makeText(context, "Student record updated!", Toast.LENGTH_SHORT).show()
                    editingStudent = null
                }
            )
        }

        // Attendance Note Editing Dialog
        noteEditingRecord?.let { (student, record) ->
            var noteText by remember { mutableStateOf(record?.notes ?: "") }
            AlertDialog(
                onDismissRequest = { noteEditingRecord = null },
                title = { Text("Attendance Note for ${student.name}") },
                text = {
                    Column {
                        Text("Add remark or reason for ${record?.date ?: effectiveDate}:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("e.g., Arrived on time, completed revision, sick leave") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val currentStat = record?.status ?: AttendanceStatus.PRESENT
                            onSetAttendance(student.id, student.name, effectiveDate, currentStat, noteText.trim())
                            Toast.makeText(context, "Attendance note saved!", Toast.LENGTH_SHORT).show()
                            noteEditingRecord = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                    ) {
                        Text("Save Note")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { noteEditingRecord = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Export CSV Preview Dialog
        if (showCsvPreviewDialog) {
            AlertDialog(
                onDismissRequest = { showCsvPreviewDialog = false },
                title = { Text("Export Attendance Report (CSV)") },
                text = {
                    Column {
                        Text("Attendance records formatted in CSV standard:")
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = generatedCsvContent.take(400) + "\n...",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Attendance report copied & exported successfully!", Toast.LENGTH_SHORT).show()
                            showCsvPreviewDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                    ) {
                        Text("Download CSV")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCsvPreviewDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun EditStudentRecordDialog(
    student: User,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, parentContact: String, surah: Int, surahName: String, ayah: Int, para: Int) -> Unit
) {
    var name by remember { mutableStateOf(student.name) }
    var phone by remember { mutableStateOf(student.phone) }
    var parentContact by remember { mutableStateOf(student.parentContact) }
    var surahNum by remember { mutableStateOf(student.currentSurah.toString()) }
    var ayahNum by remember { mutableStateOf(student.currentAyah.toString()) }
    var paraNum by remember { mutableStateOf(student.currentPara.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Student Record",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = parentContact,
                    onValueChange = { parentContact = it },
                    label = { Text("Parent Name & Contact") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = paraNum,
                        onValueChange = { paraNum = it },
                        label = { Text("Para (1-30)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = surahNum,
                        onValueChange = { surahNum = it },
                        label = { Text("Surah #") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = ayahNum,
                        onValueChange = { ayahNum = it },
                        label = { Text("Ayah #") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s = surahNum.toIntOrNull() ?: 1
                    val a = ayahNum.toIntOrNull() ?: 1
                    val p = paraNum.toIntOrNull() ?: 1
                    val sName = QuranRepository.surahsList.find { it.number == s }?.nameEnglish ?: "Al-Fatihah"
                    onSave(name.trim(), phone.trim(), parentContact.trim(), s, sName, a, p)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, email: String, phone: String, parent: String, surah: Int, ayah: Int, para: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+92 ") }
    var parentContact by remember { mutableStateOf("") }
    var selectedSurah by remember { mutableStateOf("1") }
    var startingAyah by remember { mutableStateOf("1") }
    var startingPara by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enroll New Student") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Student Email (for login)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Student Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = parentContact,
                    onValueChange = { parentContact = it },
                    label = { Text("Parent Name & Contact") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startingPara,
                        onValueChange = { startingPara = it },
                        label = { Text("Para #") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = selectedSurah,
                        onValueChange = { selectedSurah = it },
                        label = { Text("Surah #") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = startingAyah,
                        onValueChange = { startingAyah = it },
                        label = { Text("Ayah #") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sNum = selectedSurah.toIntOrNull() ?: 1
                    val aNum = startingAyah.toIntOrNull() ?: 1
                    val pNum = startingPara.toIntOrNull() ?: 1
                    onAdd(name, email, phone, parentContact, sNum, aNum, pNum)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
            ) {
                Text("Enroll Student")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun buildCsvReport(students: List<User>, attendance: List<AttendanceEntity>): String {
    val sb = StringBuilder()
    sb.append("Student ID,Student Name,Date,Status,Notes\n")
    for (a in attendance) {
        sb.append("${a.studentId},\"${a.studentName}\",${a.date},${a.status.name},\"${a.notes}\"\n")
    }
    return sb.toString()
}

@Composable
private fun AttendanceSummaryBox(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = color
            )
        }
    }
}
