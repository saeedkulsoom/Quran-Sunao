package com.example.ui.screens.chat

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessageEntity
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    currentUser: User,
    students: List<User>,
    selectedStudent: User?,
    onSelectStudent: (User) -> Unit,
    messages: List<ChatMessageEntity>,
    onSendMessage: (studentId: String, text: String) -> Unit,
    lang: String,
    onStartCall: (User, Boolean) -> Unit = { _, _ -> }
) {
    val isTeacher = currentUser.role == UserRole.TEACHER

    // Instagram-style navigation:
    // If null, show conversation inbox list.
    // When a student name/row is clicked, open their dedicated chat view.
    var activeConversation by remember(selectedStudent) {
        mutableStateOf<User?>(selectedStudent)
    }

    if (activeConversation == null && isTeacher) {
        // --- INBOX / CONVERSATIONS LIST VIEW (Instagram style) ---
        TeacherConversationsInbox(
            students = students,
            messages = messages,
            lang = lang,
            onOpenConversation = { student ->
                activeConversation = student
                onSelectStudent(student)
            }
        )
    } else {
        // --- 1-ON-1 CONVERSATION SCREEN ---
        val targetStudent = if (isTeacher) (activeConversation ?: students.firstOrNull()) else currentUser
        IndividualChatView(
            currentUser = currentUser,
            student = targetStudent,
            isTeacher = isTeacher,
            messages = messages,
            lang = lang,
            onBackClick = {
                activeConversation = null
            },
            onSendMessage = onSendMessage,
            onStartCall = onStartCall
        )
    }
}

@Composable
private fun TeacherConversationsInbox(
    students: List<User>,
    messages: List<ChatMessageEntity>,
    lang: String,
    onOpenConversation: (User) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredStudents = students.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Instagram-style Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search messages and students...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IslamicEmerald,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Direct Messages",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${filteredStudents.size} Chats",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(IslamicEmerald.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = IslamicEmerald,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = if (students.isEmpty()) "No Student Conversations Yet" else "No matching students",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (students.isEmpty())
                            "Add students from the Students tab to initiate direct chat and voice/video sessions."
                        else "Check the student's name and try again.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredStudents) { student ->
                    val studentMessages = messages.filter { it.studentId == student.id }
                    val lastMessage = studentMessages.maxByOrNull { it.timestamp }
                    val unreadCount = studentMessages.count { !it.isRead && !it.isFromTeacher }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOpenConversation(student) }
                            .border(
                                1.dp,
                                if (unreadCount > 0) IslamicGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                RoundedCornerShape(16.dp)
                            ),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = if (unreadCount > 0) 2.dp else 0.5.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Instagram-styled Avatar with ring
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(IslamicEmerald.copy(alpha = 0.12f))
                                    .border(2.dp, if (unreadCount > 0) IslamicGold else IslamicEmerald, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = IslamicEmerald
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Conversation snippet
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = student.name,
                                        fontSize = 15.sp,
                                        fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (lastMessage != null) {
                                        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(lastMessage.timestamp))
                                        Text(
                                            text = timeStr,
                                            fontSize = 11.sp,
                                            color = if (unreadCount > 0) IslamicGoldLight else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = lastMessage?.text ?: "Tap to start conversation • Sabaq: Para ${student.currentPara}",
                                        fontSize = 12.sp,
                                        color = if (unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (unreadCount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(IslamicGold),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = unreadCount.toString(),
                                                color = Color.Black,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}

@Composable
private fun IndividualChatView(
    currentUser: User,
    student: User?,
    isTeacher: Boolean,
    messages: List<ChatMessageEntity>,
    lang: String,
    onBackClick: () -> Unit,
    onSendMessage: (studentId: String, text: String) -> Unit,
    onStartCall: (User, Boolean) -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val activeStudentId = student?.id ?: ""
    val studentMessages = messages.filter { it.studentId == activeStudentId }

    LaunchedEffect(studentMessages.size) {
        if (studentMessages.isNotEmpty()) {
            listState.animateScrollToItem(studentMessages.size - 1)
        }
    }

    val quickReminders = listOf(
        "⏰ Don't forget your Sabaq session today at 5 PM!",
        "📖 Please revise your current Para before class.",
        "🌟 Masha'Allah, great progress on your Tajweed!",
        "🤲 May Allah bless your Qur'an journey with retention."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Instagram-style 1-on-1 Chat Header with Back Arrow, Avatar, Name, Call Buttons
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, IslamicEmerald.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (Instagram DM back navigation)
                if (isTeacher) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Messages",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(IslamicEmerald.copy(alpha = 0.15f))
                        .border(1.5.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isTeacher) (student?.name?.take(2)?.uppercase() ?: "ST") else "UK",
                        fontWeight = FontWeight.Bold,
                        color = IslamicEmerald,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Name and Sabaq Pointer
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTeacher) (student?.name ?: "Student") else "Ustadha Aminah Khan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isTeacher)
                            "Para ${student?.currentPara ?: 1} • ${student?.currentSurahName ?: "Al-Fatihah"}"
                        else "Teacher • Verified Ustadha",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // In-Chat Voice Call button
                IconButton(
                    onClick = {
                        student?.let { onStartCall(it, false) }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(IslamicEmerald.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Audio Call",
                        tint = IslamicEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // In-Chat Video Call button
                IconButton(
                    onClick = {
                        student?.let { onStartCall(it, true) }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(IslamicEmerald.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = IslamicEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Quick Sabaq reminders for teacher
        if (isTeacher) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickReminders) { reminder ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                student?.let { onSendMessage(it.id, reminder) }
                            }
                            .border(1.dp, IslamicGold.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = reminder,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (studentMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Start conversation with ${student?.name ?: "Student"}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Send a reminder, Sabaq feedback, or start a live recitation call.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            items(studentMessages) { msg ->
                val isSelf = if (isTeacher) msg.isFromTeacher else !msg.isFromTeacher
                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isSelf) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isSelf) 16.dp else 4.dp,
                                    bottomEnd = if (isSelf) 4.dp else 16.dp
                                )
                            )
                            .background(
                                if (isSelf) IslamicEmerald else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                1.dp,
                                if (isSelf) IslamicGold.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = msg.senderName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelf) IslamicGold else IslamicEmerald
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = msg.text,
                                fontSize = 14.sp,
                                color = if (isSelf) Color.White else MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timeStr,
                                fontSize = 10.sp,
                                color = if (isSelf) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text(AppStrings.get("type_message", lang), fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank() && student != null) {
                        onSendMessage(student.id, textInput.trim())
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(IslamicEmerald)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
