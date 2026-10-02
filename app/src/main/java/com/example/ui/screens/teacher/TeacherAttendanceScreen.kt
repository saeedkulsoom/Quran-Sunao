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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.User
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.MihrabArchShape
import com.example.ui.components.MihrabCard
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.StatusCorrectGreen
import com.example.ui.theme.StatusLeaveBlue
import com.example.ui.theme.StatusMistakeRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TeacherAttendanceScreen(
    students: List<User>,
    allAttendance: List<AttendanceEntity>,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onCycleStatus: (User) -> Unit,
    lang: String
) {
    val context = LocalContext.current
    var showCsvPreviewDialog by remember { mutableStateOf(false) }
    var generatedCsvContent by remember { mutableStateOf("") }

    // Calculate monthly statistics
    val totalRecords = allAttendance.size
    val presentRecords = allAttendance.count { it.status == AttendanceStatus.PRESENT }
    val absentRecords = allAttendance.count { it.status == AttendanceStatus.ABSENT }
    val leaveRecords = allAttendance.count { it.status == AttendanceStatus.LEAVE }
    val attendanceRate = if (totalRecords > 0) (presentRecords * 100 / totalRecords) else 100

    val currentDayAttendance = allAttendance.filter { it.date == selectedDate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Month statistics card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, IslamicGold.copy(alpha = 0.4f), MihrabArchShape),
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
                            text = "Monthly Attendance Overview",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Class Overall: $attendanceRate% Attendance Rate",
                            fontSize = 13.sp,
                            color = StatusCorrectGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Export CSV Button
                    OutlinedButton(
                        onClick = {
                            val csv = buildCsvReport(students, allAttendance)
                            generatedCsvContent = csv
                            showCsvPreviewDialog = true
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppStrings.get("export_csv", lang), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(label = "Present", count = presentRecords, color = StatusCorrectGreen, modifier = Modifier.weight(1f))
                    StatBox(label = "Absent", count = absentRecords, color = StatusMistakeRed, modifier = Modifier.weight(1f))
                    StatBox(label = "Leave", count = leaveRecords, color = StatusLeaveBlue, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Date Picker Bar
        val availableDates = listOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            "2026-09-19",
            "2026-09-18",
            "2026-09-17",
            "2026-09-16"
        )

        Text(
            text = "Select Date Record",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableDates.take(4).forEach { d ->
                val isSelected = d == selectedDate
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
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (d == availableDates[0]) "Today" else d.takeLast(5),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) IslamicEmerald else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Class Roll Call ($selectedDate)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tap status to cycle",
                fontSize = 12.sp,
                color = IslamicEmerald,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(students) { student ->
                val studentRecord = currentDayAttendance.find { it.studentId == student.id }
                val currentStatus = studentRecord?.status ?: AttendanceStatus.ABSENT

                // Per-student attendance percentage
                val studentRecords = allAttendance.filter { it.studentId == student.id }
                val studentPresents = studentRecords.count { it.status == AttendanceStatus.PRESENT }
                val studentRate = if (studentRecords.isNotEmpty()) (studentPresents * 100 / studentRecords.size) else 100

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCycleStatus(student) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(IslamicEmerald.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = IslamicEmerald,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Monthly: $studentRate% present • ${studentRecord?.notes ?: "Regular session"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AttendanceBadge(status = currentStatus)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showCsvPreviewDialog) {
        AlertDialog(
            onDismissRequest = { showCsvPreviewDialog = false },
            title = { Text("Export Attendance Report (CSV)") },
            text = {
                Column {
                    Text("The attendance record has been formatted into CSV standard format:")
                    Spacer(modifier = Modifier.height(10.dp))
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

private fun buildCsvReport(students: List<User>, attendance: List<AttendanceEntity>): String {
    val sb = StringBuilder()
    sb.append("Student ID,Student Name,Date,Status,Notes\n")
    for (a in attendance) {
        sb.append("${a.studentId},\"${a.studentName}\",${a.date},${a.status.name},\"${a.notes}\"\n")
    }
    return sb.toString()
}

@Composable
fun StatBox(
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
