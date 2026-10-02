package com.example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    chatId: String,
    currentUser: User,
    students: List<User>,
    messages: List<ChatMessageEntity>,
    lang: String,
    onBackClick: () -> Unit,
    onSendMessage: (studentId: String, text: String) -> Unit,
    onStartCall: (User, isVideo: Boolean) -> Unit
) {
    val isTeacher = currentUser.role == UserRole.TEACHER

    // Resolve target contact
    val contactUser = remember(chatId, students, isTeacher) {
        if (isTeacher) {
            students.find { it.id == chatId } ?: User(
                id = chatId,
                email = "student@quransunao.com",
                name = "Fatima Zahra",
                role = UserRole.STUDENT,
                currentPara = 1,
                currentSurahName = "Al-Fatihah"
            )
        } else {
            User(
                id = "teacher_1",
                email = "teacher@quransunao.com",
                name = "Ustadha Aminah Khan",
                role = UserRole.TEACHER,
                phone = "+92 300 5558822",
                institution = "Madrasa Quran Sunao",
                qualifications = "Certified Tajweed Specialist"
            )
        }
    }

    val activeStudentId = if (isTeacher) contactUser.id else currentUser.id
    val conversationMessages = messages.filter { it.studentId == activeStudentId }

    var textInput by remember { mutableStateOf("") }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showAttachmentMenu by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll to the latest message whenever messages count changes
    LaunchedEffect(conversationMessages.size) {
        if (conversationMessages.isNotEmpty()) {
            listState.animateScrollToItem(conversationMessages.size - 1)
        }
    }

    // Quick Sabaq suggestions
    val quickSuggestions = remember(lang, isTeacher) {
        if (isTeacher) {
            if (lang == "ur") {
                listOf(
                    "⏰ آج شام 5 بجے سبق سنانے کا وقت ہے!",
                    "📖 کلاس سے پہلے اپنا موجودہ پارہ دہرا لیں۔",
                    "🌟 ماشاءاللہ! آپ کی تجوید میں بہتری آئی ہے۔",
                    "🤲 اللہ تعالیٰ آپ کے حفظ و فہم میں برکت دے۔"
                )
            } else {
                listOf(
                    "⏰ Don't forget your Sabaq session today at 5 PM!",
                    "📖 Please revise your current Para before class.",
                    "🌟 Masha'Allah, great progress on your Tajweed!",
                    "🤲 May Allah bless your Qur'an retention with ease."
                )
            }
        } else {
            if (lang == "ur") {
                listOf(
                    "السلام علیکم استاد جی! میں نے آج کا پارہ دہرا لیا ہے۔",
                    "استاد جی! کیا میں ابھی سبق سنا سکتا ہوں؟",
                    "جزاک اللہ خیراً استاد جی!",
                    "انشاءاللہ کل کے سبق میں غلطیاں نہیں ہوں گی۔"
                )
            } else {
                listOf(
                    "Assalamu Alaikum Ustadha! I have revised today's Para.",
                    "Ready to recite today's Sabaq session!",
                    "JazakAllahu Khairan for the recitation feedback.",
                    "Practicing the rules of Noon Sakinah right now."
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = IslamicEmerald,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onBackClick() }
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // Avatar in Top Bar
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .border(1.dp, IslamicGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = contactUser.name.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                title = {
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { showProfileDialog = true }
                    ) {
                        Text(
                            text = contactUser.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF25D366))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppStrings.get("online", lang),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    // Audio Call icon
                    IconButton(onClick = { onStartCall(contactUser, false) }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Audio Call",
                            tint = Color.White
                        )
                    }

                    // Video Call icon
                    IconButton(onClick = { onStartCall(contactUser, true) }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = Color.White
                        )
                    }

                    // 3-dot overflow menu
                    Box {
                        IconButton(onClick = { showOverflowMenu = !showOverflowMenu }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("view_contact", lang)) },
                                onClick = {
                                    showOverflowMenu = false
                                    showProfileDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("attach_sabaq", lang)) },
                                onClick = {
                                    showOverflowMenu = false
                                    val sabaqInfo = "Current Sabaq: Para ${contactUser.currentPara}, ${contactUser.currentSurahName} (Ayah ${contactUser.currentAyah})"
                                    onSendMessage(activeStudentId, sabaqInfo)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("clear_chat", lang)) },
                                onClick = {
                                    showOverflowMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Quick Sabaq Suggestion Pills
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickSuggestions) { suggestion ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onSendMessage(activeStudentId, suggestion)
                            }
                            .border(0.5.dp, IslamicEmerald.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = suggestion,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Message List with Date Separators
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Group messages by date
                val grouped = conversationMessages.groupBy { getMessageDateLabel(it.timestamp, lang) }

                grouped.forEach { (dateLabel, msgs) ->
                    // Date Separator Pill
                    item(key = "header_$dateLabel") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                                shadowElevation = 0.5.dp
                            ) {
                                Text(
                                    text = dateLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Message Items
                    items(msgs, key = { it.id }) { msg ->
                        val isSelf = if (isTeacher) msg.isFromTeacher else !msg.isFromTeacher
                        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))

                        // Light green bubble for sent, white/grey for received
                        val bubbleColor = if (isSelf) {
                            Color(0xFFE2F7CB) // WhatsApp light green
                        } else {
                            if (MaterialTheme.colorScheme.surface == Color.White) Color(0xFFFFFFFF) else MaterialTheme.colorScheme.surfaceVariant
                        }

                        val textColor = if (isSelf) Color(0xFF111B21) else MaterialTheme.colorScheme.onSurface

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
                                            bottomStart = if (isSelf) 16.dp else 2.dp,
                                            bottomEnd = if (isSelf) 2.dp else 16.dp
                                        )
                                    )
                                    .background(bubbleColor)
                                    .border(
                                        0.5.dp,
                                        if (isSelf) Color(0xFFBCE6A2) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isSelf) 16.dp else 2.dp,
                                            bottomEnd = if (isSelf) 2.dp else 16.dp
                                        )
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Column {
                                    if (!isSelf) {
                                        Text(
                                            text = msg.senderName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IslamicEmerald
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }

                                    Text(
                                        text = msg.text,
                                        fontSize = 14.sp,
                                        color = textColor,
                                        lineHeight = 20.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Timestamp and tick icons inside bubble
                                    Row(
                                        modifier = Modifier.align(Alignment.End),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = timeStr,
                                            fontSize = 10.sp,
                                            color = Color.Black.copy(alpha = 0.55f)
                                        )

                                        if (isSelf) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.DoneAll,
                                                contentDescription = "Read",
                                                tint = Color(0xFF34B7F1), // WhatsApp double blue/cyan tick
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Bottom Input Bar (with Emoji, rounded text field, Attach, and Mic/Send button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            textInput += " 🤲 "
                        }) {
                            Icon(
                                imageVector = Icons.Default.EmojiEmotions,
                                contentDescription = "Emoji",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = {
                                Text(
                                    AppStrings.get("type_message", lang),
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.weight(1f),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        // Attach icon (paperclip)
                        Box {
                            IconButton(onClick = { showAttachmentMenu = !showAttachmentMenu }) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Attach",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            DropdownMenu(
                                expanded = showAttachmentMenu,
                                onDismissRequest = { showAttachmentMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(AppStrings.get("attach_sabaq", lang)) },
                                    onClick = {
                                        showAttachmentMenu = false
                                        textInput = "Sabaq Pointer: Para ${contactUser.currentPara}, ${contactUser.currentSurahName}"
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("🎤 " + AppStrings.get("voice_note_sent", lang)) },
                                    onClick = {
                                        showAttachmentMenu = false
                                        onSendMessage(activeStudentId, AppStrings.get("voice_note_sent", lang))
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Round Send Button (Mic when empty, Send arrow when typed)
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSendMessage(activeStudentId, textInput.trim())
                            textInput = ""
                        } else {
                            // Empty field sends voice note recording simulation
                            onSendMessage(activeStudentId, AppStrings.get("voice_note_sent", lang))
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(IslamicEmerald)
                ) {
                    Icon(
                        imageVector = if (textInput.isBlank()) Icons.Default.Mic else Icons.AutoMirrored.Filled.Send,
                        contentDescription = if (textInput.isBlank()) "Record Voice Note" else "Send",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }

    // Contact Profile Info Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text(
                    text = contactUser.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (contactUser.role == UserRole.TEACHER) "Ustadha Aminah Khan (Verified Teacher)" else "Student Information",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = IslamicEmerald
                    )
                    if (contactUser.phone.isNotBlank()) {
                        Text(text = "📞 Phone: ${contactUser.phone}", fontSize = 13.sp)
                    }
                    if (contactUser.role == UserRole.STUDENT) {
                        Text(text = "📖 Current Para: ${contactUser.currentPara}", fontSize = 13.sp)
                        Text(text = "📜 Surah: ${contactUser.currentSurahName} (Ayah ${contactUser.currentAyah})", fontSize = 13.sp)
                    } else {
                        Text(text = "🏛️ Institution: ${contactUser.institution}", fontSize = 13.sp)
                        Text(text = "🌟 Ijazah: Hafs 'an 'Asim, Al-Azhar (Cairo)", fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("OK", color = IslamicEmerald)
                }
            }
        )
    }
}

// Helper to determine Today / Yesterday / Date label
private fun getMessageDateLabel(timestamp: Long, lang: String): String {
    val cal = Calendar.getInstance()
    val today = Calendar.getInstance()
    cal.timeInMillis = timestamp

    val isSameDay = cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

    today.add(Calendar.DAY_OF_YEAR, -1)
    val isYesterday = cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

    return when {
        isSameDay -> AppStrings.get("today", lang)
        isYesterday -> AppStrings.get("yesterday", lang)
        else -> SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
