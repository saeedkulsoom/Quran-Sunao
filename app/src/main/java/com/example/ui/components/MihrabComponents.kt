package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceStatus
import com.example.data.model.AyahGrade
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.StatusCorrectGreen
import com.example.ui.theme.StatusLeaveBlue
import com.example.ui.theme.StatusMistakeRed
import com.example.ui.theme.StatusPracticeAmber

// Signature Mihrab Arch Card Shape (top corners have dramatic smooth curve mimicking a mihrab niche)
val MihrabArchShape = RoundedCornerShape(
    topStart = CornerSize(28.dp),
    topEnd = CornerSize(28.dp),
    bottomStart = CornerSize(16.dp),
    bottomEnd = CornerSize(16.dp)
)

val GentleArchShape = RoundedCornerShape(20.dp)

@Composable
fun MihrabCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = IslamicGold.copy(alpha = 0.35f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, MihrabArchShape),
        shape = MihrabArchShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun IslamicHeroCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    trailingAction: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MihrabArchShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(IslamicEmerald, IslamicEmeraldDark)
                )
            )
            .border(1.5.dp, IslamicGoldLight.copy(alpha = 0.6f), MihrabArchShape)
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (badgeText != null) {
                    Surface(
                        color = IslamicGold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGoldLight)
                    ) {
                        Text(
                            text = badgeText,
                            color = IslamicGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                trailingAction?.invoke()
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun AttendanceBadge(status: AttendanceStatus, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        AttendanceStatus.PRESENT -> StatusCorrectGreen to "Present"
        AttendanceStatus.ABSENT -> StatusMistakeRed to "Absent"
        AttendanceStatus.LEAVE -> StatusLeaveBlue to "Leave"
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AyahGradeBadge(grade: AyahGrade, modifier: Modifier = Modifier) {
    val (color, text) = when (grade) {
        AyahGrade.CORRECT -> StatusCorrectGreen to "Correct"
        AyahGrade.MISTAKE -> StatusMistakeRed to "Mistake"
        AyahGrade.NEEDS_PRACTICE -> StatusPracticeAmber to "Needs Practice"
        AyahGrade.UNGRADED -> MaterialTheme.colorScheme.outline to "Pending"
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
