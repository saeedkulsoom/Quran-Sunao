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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CallLogEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldLight
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ChatNavTab {
    CHATS,
    STATUS,
    CALLS
}

data class RecitationStatusItem(
    val id: String,
    val authorName: String,
    val isTeacher: Boolean,
    val statusText: String,
    val timeAgo: String,
    val paraInfo: String = ""
)

@Composable
fun ChatsListScreen(
    currentUser: User,
    students: List<User>,
    messages: List<ChatMessageEntity>,
    callLogs: List<CallLogEntity>,
    lang: String,
    onNavigateToConversation: (chatId: String) -> Unit,
    onStartCall: (User, isVideo: Boolean) -> Unit,
    onToggleLanguage: () -> Unit = {},
    onAddStudent: (name: String, email: String, phone: String, parent: String, surah: Int, ayah: Int, para: Int) -> Unit = { _, _, _, _, _, _, _ -> },
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToQuran: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val isTeacher = currentUser.role == UserRole.TEACHER
    var currentTab by remember { mutableStateOf(ChatNavTab.CHATS) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showNewChatDialog by remember { mutableStateOf(false) }
    var showAddStatusDialog by remember { mutableStateOf(false) }
    var myCurrentStatus by remember { mutableStateOf("📖 Practicing Sabaq recitation with Tajweed rules.") }

    // Pre-populated status feed
    val statusList = remember {
        listOf(
            RecitationStatusItem(
                id = "status_1",
                authorName = "Ustadha Aminah Khan",
                isTeacher = true,
                statusText = "💡 Tajweed Tip: Always elongate Madd Muttasil by 4-5 counts during Surah recitation.",
                timeAgo = "15 min ago",
                paraInfo = "Teacher Note"
            ),
            RecitationStatusItem(
                id = "status_2",
                authorName = "Fatima Zahra",
                isTeacher = false,
                statusText = "Alhamdulillah! Completed revision of Surah Al-Mulk today with 0 mistakes 🌟",
                timeAgo = "1 hour ago",
                paraInfo = "Para 29"
            ),
            RecitationStatusItem(
                id = "status_3",
                authorName = "Zaid Ali",
                isTeacher = false,
                statusText = "Practicing Makharij for Noon Sakinah & Tanween. Ready for today's Sabaq session! 🤲",
                timeAgo = "3 hours ago",
                paraInfo = "Para 1"
            )
        )
    }

    // Effective chat contacts
    val chatContacts: List<User> = if (isTeacher) {
        if (students.isNotEmpty()) {
            students
        } else {
            // Provide a starter student contact so the chat screen is immediately functional
            listOf(
                User(
                    id = "sample_student_1",
                    email = "fatima@quransunao.com",
                    name = "Fatima Zahra",
                    role = UserRole.STUDENT,
                    phone = "+92 300 1234567",
                    currentPara = 1,
                    currentSurah = 1,
                    currentSurahName = "Al-Fatihah",
                    currentAyah = 7,
                    courseTrack = "Hifz Quran"
                )
            )
        }
    } else {
        // Student chats with their teacher
        listOf(
            User(
                id = "teacher_1",
                email = "teacher@quransunao.com",
                name = "Ustadha Aminah Khan",
                role = UserRole.TEACHER,
                phone = "+92 300 5558822",
                bio = "Certified Tajweed Specialist",
                institution = "Madrasa Quran Sunao"
            )
        )
    }

    // Filter contacts based on search query
    val filteredContacts = chatContacts.filter { contact ->
        val contactMessages = messages.filter {
            if (isTeacher) it.studentId == contact.id else true
        }
        val lastMessage = contactMessages.maxByOrNull { it.timestamp }
        contact.name.contains(searchQuery, ignoreCase = true) ||
                (lastMessage?.text?.contains(searchQuery, ignoreCase = true) == true)
    }

    // Calculate total unread messages
    val totalUnread = messages.count { !it.isRead && (if (isTeacher) !it.isFromTeacher else it.isFromTeacher) }

    Scaffold(
        topBar = {
            Surface(
                color = IslamicEmerald,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    if (isSearchActive) {
                        // In-App Search Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                isSearchActive = false
                                searchQuery = ""
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        AppStrings.get("search_messages", lang),
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 14.sp
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = IslamicGold,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    } else {
                        // Standard Top Bar with App Name, Search, and 3-Dot Overflow Menu
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onNavigateToDashboard,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Dashboard",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("app_title", lang),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { isSearchActive = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Box {
                                    IconButton(onClick = { showMenu = !showMenu }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Menu",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(AppStrings.get("new_chat", lang)) },
                                            onClick = {
                                                showMenu = false
                                                showNewChatDialog = true
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(AppStrings.get("language_toggle", lang)) },
                                            onClick = {
                                                showMenu = false
                                                onToggleLanguage()
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(AppStrings.get("dashboard", lang)) },
                                            onClick = {
                                                showMenu = false
                                                onNavigateToDashboard()
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(AppStrings.get("quran", lang)) },
                                            onClick = {
                                                showMenu = false
                                                onNavigateToQuran()
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(AppStrings.get("settings", lang)) },
                                            onClick = {
                                                showMenu = false
                                                onNavigateToSettings()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // WhatsApp-style Bottom Navigation: Chats, Status, Calls
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, IslamicEmerald.copy(alpha = 0.15f))
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    // 1. CHATS TAB
                    NavigationBarItem(
                        selected = currentTab == ChatNavTab.CHATS,
                        onClick = { currentTab = ChatNavTab.CHATS },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (totalUnread > 0) {
                                        Badge(
                                            containerColor = Color(0xFF25D366),
                                            contentColor = Color.White
                                        ) {
                                            Text(totalUnread.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = AppStrings.get("chats", lang)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = AppStrings.get("chats", lang),
                                fontWeight = if (currentTab == ChatNavTab.CHATS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IslamicEmerald,
                            selectedTextColor = IslamicEmerald,
                            indicatorColor = IslamicEmerald.copy(alpha = 0.12f)
                        )
                    )

                    // 2. STATUS TAB
                    NavigationBarItem(
                        selected = currentTab == ChatNavTab.STATUS,
                        onClick = { currentTab = ChatNavTab.STATUS },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.DonutLarge,
                                contentDescription = AppStrings.get("status", lang)
                            )
                        },
                        label = {
                            Text(
                                text = AppStrings.get("status", lang),
                                fontWeight = if (currentTab == ChatNavTab.STATUS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IslamicEmerald,
                            selectedTextColor = IslamicEmerald,
                            indicatorColor = IslamicEmerald.copy(alpha = 0.12f)
                        )
                    )

                    // 3. CALLS TAB
                    NavigationBarItem(
                        selected = currentTab == ChatNavTab.CALLS,
                        onClick = { currentTab = ChatNavTab.CALLS },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = AppStrings.get("calls", lang)
                            )
                        },
                        label = {
                            Text(
                                text = AppStrings.get("calls", lang),
                                fontWeight = if (currentTab == ChatNavTab.CALLS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IslamicEmerald,
                            selectedTextColor = IslamicEmerald,
                            indicatorColor = IslamicEmerald.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            when (currentTab) {
                ChatNavTab.CHATS -> {
                    FloatingActionButton(
                        onClick = { showNewChatDialog = true },
                        containerColor = IslamicEmerald,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = AppStrings.get("new_chat", lang)
                        )
                    }
                }
                ChatNavTab.STATUS -> {
                    FloatingActionButton(
                        onClick = { showAddStatusDialog = true },
                        containerColor = IslamicEmerald,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Status"
                        )
                    }
                }
                ChatNavTab.CALLS -> {
                    FloatingActionButton(
                        onClick = {
                            val target = chatContacts.firstOrNull()
                            if (target != null) {
                                onStartCall(target, false)
                            }
                        },
                        containerColor = IslamicEmerald,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "New Call"
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ChatNavTab.CHATS -> {
                    ChatsTabContent(
                        contacts = filteredContacts,
                        messages = messages,
                        isTeacher = isTeacher,
                        lang = lang,
                        onOpenConversation = { contact ->
                            onNavigateToConversation(contact.id)
                        }
                    )
                }
                ChatNavTab.STATUS -> {
                    StatusTabContent(
                        currentUser = currentUser,
                        myStatus = myCurrentStatus,
                        statusList = statusList,
                        lang = lang,
                        onUpdateMyStatus = { showAddStatusDialog = true }
                    )
                }
                ChatNavTab.CALLS -> {
                    CallsTabContent(
                        callLogs = callLogs,
                        contacts = chatContacts,
                        lang = lang,
                        onStartCall = onStartCall
                    )
                }
            }
        }
    }

    // New Chat Dialog
    if (showNewChatDialog) {
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = {
                Text(
                    text = AppStrings.get("select_contact", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isTeacher) "Select a student to start a direct chat session:" else "Chat with your verified Qur'an teacher:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    chatContacts.forEach { contact ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    showNewChatDialog = false
                                    onNavigateToConversation(contact.id)
                                },
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(IslamicEmerald.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contact.name.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = IslamicEmerald
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = contact.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (contact.role == UserRole.TEACHER) "Ustadha • Verified Teacher" else "Sabaq: Para ${contact.currentPara}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNewChatDialog = false }) {
                    Text(AppStrings.get("cancel", lang), color = IslamicEmerald)
                }
            }
        )
    }

    // Add / Update Status Dialog
    if (showAddStatusDialog) {
        var statusInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddStatusDialog = false },
            title = {
                Text(
                    text = AppStrings.get("tap_add_status", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Share today's Sabaq progress, Surah milestone, or a du'a with your Qur'an circle:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = statusInput,
                        onValueChange = { statusInput = it },
                        placeholder = { Text("e.g. Completed revision of Para 15 today with Tajweed!") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (statusInput.isNotBlank()) {
                            myCurrentStatus = statusInput.trim()
                        }
                        showAddStatusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                ) {
                    Text("Post Status", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStatusDialog = false }) {
                    Text(AppStrings.get("cancel", lang), color = IslamicEmerald)
                }
            }
        )
    }
}

// -------------------------------------------------------------
// CHATS TAB CONTENT
// -------------------------------------------------------------
@Composable
private fun ChatsTabContent(
    contacts: List<User>,
    messages: List<ChatMessageEntity>,
    isTeacher: Boolean,
    lang: String,
    onOpenConversation: (User) -> Unit
) {
    if (contacts.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(IslamicEmerald.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = IslamicEmerald,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = AppStrings.get("no_chats_yet", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = AppStrings.get("start_chat_hint", lang),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(contacts) { contact ->
                val contactMessages = messages.filter {
                    if (isTeacher) it.studentId == contact.id else true
                }
                val lastMessage = contactMessages.maxByOrNull { it.timestamp }
                val unreadCount = contactMessages.count {
                    !it.isRead && (if (isTeacher) !it.isFromTeacher else it.isFromTeacher)
                }

                // Chat Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenConversation(contact) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Avatar with Initials
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(IslamicEmerald.copy(alpha = 0.12f))
                            .border(1.5.dp, IslamicEmerald.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.name.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = IslamicEmerald
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Contact Name, Last Message, and Badges
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = contact.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (lastMessage != null) {
                                val timeText = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(lastMessage.timestamp))
                                Text(
                                    text = timeText,
                                    fontSize = 11.sp,
                                    color = if (unreadCount > 0) Color(0xFF25D366) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val previewText = lastMessage?.text
                                ?: if (contact.role == UserRole.TEACHER) "Tap to chat with your teacher"
                                else "Sabaq: Para ${contact.currentPara} • ${contact.currentSurahName}"

                            Text(
                                text = previewText,
                                fontSize = 13.sp,
                                color = if (unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            // Unread count badge (WhatsApp green circle)
                            if (unreadCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF25D366)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Thin divider line between every chat row, starting after the avatar (like WhatsApp)
                HorizontalDivider(
                    modifier = Modifier.padding(start = 82.dp, end = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// STATUS TAB CONTENT
// -------------------------------------------------------------
@Composable
private fun StatusTabContent(
    currentUser: User,
    myStatus: String,
    statusList: List<RecitationStatusItem>,
    lang: String,
    onUpdateMyStatus: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            // My Status Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onUpdateMyStatus() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(56.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(IslamicEmerald.copy(alpha = 0.15f))
                            .border(1.5.dp, IslamicEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.name.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = IslamicEmerald
                        )
                    }
                    // Plus badge
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF25D366))
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.get("my_status", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = myStatus,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = AppStrings.get("recent_updates", lang),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        items(statusList) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Avatar with Green Ring
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(IslamicEmerald.copy(alpha = 0.1f))
                        .border(2.dp, Color(0xFF25D366), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.authorName.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = IslamicEmerald
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.authorName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = item.timeAgo,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.statusText,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    if (item.paraInfo.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.paraInfo,
                            fontSize = 11.sp,
                            color = IslamicGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(start = 66.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// -------------------------------------------------------------
// CALLS TAB CONTENT
// -------------------------------------------------------------
@Composable
private fun CallsTabContent(
    callLogs: List<CallLogEntity>,
    contacts: List<User>,
    lang: String,
    onStartCall: (User, isVideo: Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = AppStrings.get("call_history", lang),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (callLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = IslamicEmerald,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Start a Live Qur'an Recitation Call",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Call students or your teacher with crystal clear audio and video to recite and correct Tajweed.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Quick Call Contacts",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }

            items(contacts) { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(IslamicEmerald.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.name.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = IslamicEmerald
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = contact.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Sabaq: Para ${contact.currentPara}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onStartCall(contact, false) }) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call",
                            tint = IslamicEmerald
                        )
                    }
                    IconButton(onClick = { onStartCall(contact, true) }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = IslamicEmerald
                        )
                    }
                }
            }
        } else {
            items(callLogs) { log ->
                val timeStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                val targetContact = contacts.find { it.id == log.studentId } ?: contacts.firstOrNull()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(IslamicEmerald.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = log.studentName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = IslamicEmerald
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.studentName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CallMade,
                                contentDescription = null,
                                tint = Color(0xFF25D366),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$timeStr • ${log.type}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (targetContact != null) {
                        IconButton(onClick = { onStartCall(targetContact, log.type.contains("Video", ignoreCase = true)) }) {
                            Icon(
                                imageVector = if (log.type.contains("Video", ignoreCase = true)) Icons.Default.Videocam else Icons.Default.Call,
                                contentDescription = "Call Back",
                                tint = IslamicEmerald
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(start = 62.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
