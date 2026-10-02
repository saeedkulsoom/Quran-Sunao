package com.example.ui.screens.student

import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.ui.components.AttendanceBadge
import com.example.ui.components.MihrabArchShape
import com.example.ui.screens.teacher.StatBox
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.StatusCorrectGreen
import com.example.ui.theme.StatusLeaveBlue
import com.example.ui.theme.StatusMistakeRed

@Composable
fun StudentAttendanceScreen(
    attendanceRecords: List<AttendanceEntity>,
    lang: String
) {
    val totalRecords = attendanceRecords.size
    val presentCount = attendanceRecords.count { it.status == AttendanceStatus.PRESENT }
    val absentCount = attendanceRecords.count { it.status == AttendanceStatus.ABSENT }
    val leaveCount = attendanceRecords.count { it.status == AttendanceStatus.LEAVE }
    val attendanceRate = if (totalRecords > 0) (presentCount * 100 / totalRecords) else 100

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Summary Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), MihrabArchShape),
            shape = MihrabArchShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "My Attendance History",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Overall Record: $attendanceRate% Attendance Rate",
                    fontSize = 14.sp,
                    color = StatusCorrectGreen,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(label = "Present", count = presentCount, color = StatusCorrectGreen, modifier = Modifier.weight(1f))
                    StatBox(label = "Absent", count = absentCount, color = StatusMistakeRed, modifier = Modifier.weight(1f))
                    StatBox(label = "Leave", count = leaveCount, color = StatusLeaveBlue, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Daily Attendance Log",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(attendanceRecords) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = record.date,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (record.notes.isNotEmpty()) {
                                Text(
                                    text = record.notes,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        AttendanceBadge(status = record.status)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
