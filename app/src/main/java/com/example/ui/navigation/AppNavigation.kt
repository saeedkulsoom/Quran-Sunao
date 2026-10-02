package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.QuranRepository
import com.example.data.repository.StudentRepository
import com.example.ui.components.AudioMiniPlayer
import com.example.ui.components.AudioPlayerManager
import com.example.ui.components.IncomingCallDialog
import com.example.ui.components.QuranTopBar
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.SignupScreen
import com.example.ui.screens.call.ActiveCallScreen
import com.example.ui.screens.call.CallsScreen
import com.example.ui.screens.chat.ChatsListScreen
import com.example.ui.screens.chat.ConversationScreen
import com.example.ui.screens.legal.LegalScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.quran.QuranBrowserScreen
import com.example.ui.screens.quran.SurahReaderScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.student.StudentAttendanceScreen
import com.example.ui.screens.student.StudentDashboardScreen
import com.example.ui.screens.teacher.TeacherAttendanceScreen
import com.example.ui.screens.teacher.TeacherDashboardScreen
import com.example.ui.screens.teacher.TeacherListeningSessionScreen
import com.example.ui.screens.teacher.TeacherStudentsScreen
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val titleKey: String, val icon: ImageVector) {
    // Splash
    data object Splash : Screen("splash", "app_title", Icons.Default.MenuBook)

    // Auth
    data object Login : Screen("login", "login", Icons.Default.Person)
    data object Signup : Screen("signup", "signup", Icons.Default.Person)

    // Teacher tabs
    data object TeacherDashboard : Screen("teacher_dashboard", "dashboard", Icons.Default.Dashboard)
    data object TeacherStudents : Screen("teacher_students", "students_and_attendance", Icons.Default.People)
    data object TeacherAttendance : Screen("teacher_attendance", "attendance", Icons.Default.CalendarMonth)
    data object TeacherChat : Screen("chats", "chat", Icons.Default.Chat)
    data object Chats : Screen("chats", "chat", Icons.Default.Chat)
    data object QuranBrowser : Screen("quran_browser", "quran", Icons.Default.MenuBook)
    data object TeacherCalls : Screen("teacher_calls", "start_call", Icons.Default.Call)

    // Student tabs
    data object StudentDashboard : Screen("student_dashboard", "dashboard", Icons.Default.Dashboard)
    data object StudentAttendance : Screen("student_attendance", "attendance", Icons.Default.CalendarMonth)
    data object StudentChat : Screen("chats", "chat", Icons.Default.Chat)

    // Shared
    data object Profile : Screen("profile", "profile", Icons.Default.Person)
    data object Settings : Screen("settings", "settings", Icons.Default.Settings)
}

@Composable
fun AppNavigation(
    studentRepository: StudentRepository,
    quranRepository: QuranRepository,
    audioPlayerManager: AudioPlayerManager,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val currentUser by studentRepository.currentUser.collectAsState()
    val students by studentRepository.students.collectAsState()
    val currentLang by studentRepository.currentLanguage.collectAsState()
    val themeMode by studentRepository.themeMode.collectAsState()
    val allAttendance by studentRepository.getAllAttendance().collectAsState(initial = emptyList())
    val allMessages by studentRepository.allChatMessages.collectAsState(initial = emptyList())
    val allCallLogs by studentRepository.allCallLogs.collectAsState(initial = emptyList())
    val bookmarks by quranRepository.bookmarks.collectAsState(initial = emptyList())
    val playbackState by audioPlayerManager.playbackState.collectAsState()
    val incomingCall by studentRepository.incomingCall.collectAsState()
    val activeCall by studentRepository.activeCall.collectAsState()
    val notifications by studentRepository.notifications.collectAsState()

    var selectedDateForAttendance by remember { mutableStateOf(studentRepository.todayDateString) }
    var selectedStudentForChat by remember { mutableStateOf<User?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Splash.route

    val userRole = currentUser?.role ?: UserRole.TEACHER

    // Clean, balanced primary navigation tabs
    val teacherNavItems = listOf(
        Screen.TeacherDashboard,
        Screen.TeacherStudents,
        Screen.TeacherChat,
        Screen.QuranBrowser,
        Screen.Settings
    )

    val studentNavItems = listOf(
        Screen.StudentDashboard,
        Screen.StudentChat,
        Screen.QuranBrowser,
        Screen.Settings
    )

    val bottomNavItems = if (userRole == UserRole.TEACHER) teacherNavItems else studentNavItems

    val showBottomBar = currentUser != null &&
            !currentRoute.startsWith("surah_reader") &&
            !currentRoute.startsWith("teacher_listening") &&
            !currentRoute.startsWith("legal") &&
            !currentRoute.startsWith("chats") &&
            !currentRoute.startsWith("chat/") &&
            currentRoute != Screen.Login.route &&
            currentRoute != Screen.Signup.route &&
            currentRoute != Screen.Splash.route

    val showTopBar = currentUser != null &&
            !currentRoute.startsWith("surah_reader") &&
            !currentRoute.startsWith("teacher_listening") &&
            !currentRoute.startsWith("legal") &&
            !currentRoute.startsWith("chats") &&
            !currentRoute.startsWith("chat/") &&
            currentRoute != Screen.Login.route &&
            currentRoute != Screen.Signup.route &&
            currentRoute != Screen.Splash.route

    val currentScreenTitle = when {
        currentRoute.startsWith("teacher_dashboard") -> AppStrings.get("dashboard", currentLang)
        currentRoute.startsWith("student_dashboard") -> AppStrings.get("current_sabaq", currentLang)
        currentRoute.startsWith("teacher_students") -> AppStrings.get("students", currentLang)
        currentRoute.startsWith("teacher_attendance") || currentRoute.startsWith("student_attendance") -> AppStrings.get("attendance", currentLang)
        currentRoute.startsWith("teacher_chat") || currentRoute.startsWith("student_chat") -> AppStrings.get("chat", currentLang)
        currentRoute.startsWith("quran_browser") -> AppStrings.get("quran", currentLang)
        currentRoute.startsWith("teacher_calls") -> AppStrings.get("start_call", currentLang)
        currentRoute.startsWith("profile") -> AppStrings.get("profile", currentLang)
        currentRoute.startsWith("settings") -> AppStrings.get("settings", currentLang)
        else -> AppStrings.get("app_title", currentLang)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                if (showTopBar) {
                    QuranTopBar(
                        title = currentScreenTitle,
                        subtitle = if (userRole == UserRole.TEACHER) "Ustadha Portal" else "Student Portal",
                        showBackButton = false,
                        currentLanguage = currentLang,
                        onToggleLanguage = { studentRepository.toggleLanguage() },
                        currentRole = userRole,
                        onSwitchRole = {
                            val nextRole = if (userRole == UserRole.TEACHER) UserRole.STUDENT else UserRole.TEACHER
                            studentRepository.switchUserRole(nextRole)
                            val targetDest = if (nextRole == UserRole.TEACHER) Screen.TeacherDashboard.route else Screen.StudentDashboard.route
                            navController.navigate(targetDest) {
                                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleDarkTheme
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) {
                    Surface(
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, IslamicGold.copy(alpha = 0.25f))
                    ) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ) {
                            bottomNavItems.forEach { screen ->
                                val isSelected = currentRoute == screen.route
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = AppStrings.get(screen.titleKey, currentLang),
                                            tint = if (isSelected) IslamicEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = AppStrings.get(screen.titleKey, currentLang),
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) IslamicEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    )
                                )
                            }
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
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash.route
                ) {
                    // Splash Screen
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onSplashFinished = {
                                val target = if (currentUser == null) {
                                    Screen.Login.route
                                } else if (userRole == UserRole.TEACHER) {
                                    Screen.TeacherDashboard.route
                                } else {
                                    Screen.StudentDashboard.route
                                }
                                navController.navigate(target) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // Auth
                    composable(Screen.Login.route) {
                        LoginScreen(
                            lang = currentLang,
                            onLoginSuccess = { email, role ->
                                studentRepository.login(email, role)
                                val target = if (role == UserRole.TEACHER) Screen.TeacherDashboard.route else Screen.StudentDashboard.route
                                navController.navigate(target) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToSignup = { navController.navigate(Screen.Signup.route) }
                        )
                    }

                    composable(Screen.Signup.route) {
                        SignupScreen(
                            lang = currentLang,
                            onSignupSuccess = { name, email ->
                                studentRepository.signupTeacher(name, email)
                                navController.navigate(Screen.TeacherDashboard.route) {
                                    popUpTo(Screen.Signup.route) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = { navController.popBackStack() },
                            onNavigateToPrivacy = { navController.navigate("legal/true") }
                        )
                    }

                    // Teacher Dashboard
                    composable(Screen.TeacherDashboard.route) {
                        val todayAttendance = allAttendance.filter { it.date == studentRepository.todayDateString }
                        TeacherDashboardScreen(
                            students = students,
                            todayAttendance = todayAttendance,
                            notifications = notifications,
                            lang = currentLang,
                            onStartListening = { student ->
                                navController.navigate("teacher_listening/${student.id}")
                            },
                            onStartCall = { student ->
                                coroutineScope.launch {
                                    studentRepository.startCall(student.id, student.name, "Native Video")
                                }
                            },
                            onOpenChat = { student ->
                                selectedStudentForChat = student
                                navController.navigate(Screen.TeacherChat.route)
                            },
                            onCycleAttendance = { student ->
                                coroutineScope.launch {
                                    studentRepository.cycleAttendance(student.id, student.name, studentRepository.todayDateString)
                                }
                            },
                            onMarkNotificationRead = { notifId ->
                                studentRepository.markNotificationRead(notifId)
                            },
                            onMarkAllNotificationsRead = {
                                studentRepository.markAllNotificationsRead()
                            },
                            onDismissNotification = { notifId ->
                                studentRepository.dismissNotification(notifId)
                            },
                            onNavigateToAttendanceSheet = {
                                navController.navigate(Screen.TeacherStudents.route)
                            },
                            onNavigateToQuran = {
                                navController.navigate(Screen.QuranBrowser.route)
                            },
                            onAddStudentClick = {
                                navController.navigate(Screen.TeacherStudents.route)
                            }
                        )
                    }

                    // Teacher Students & Attendance (Combined)
                    composable(Screen.TeacherStudents.route) {
                        TeacherStudentsScreen(
                            students = students,
                            allAttendance = allAttendance,
                            selectedDate = selectedDateForAttendance,
                            onDateSelected = { selectedDateForAttendance = it },
                            onCycleStatus = { student ->
                                coroutineScope.launch {
                                    studentRepository.cycleAttendance(student.id, student.name, selectedDateForAttendance)
                                }
                            },
                            onSetAttendance = { studentId, studentName, date, status, notes ->
                                coroutineScope.launch {
                                    studentRepository.setAttendance(studentId, studentName, date, status, notes)
                                }
                            },
                            onBulkMarkAttendance = { date, status ->
                                coroutineScope.launch {
                                    studentRepository.bulkMarkAttendance(date, status)
                                }
                            },
                            onUpdateStudentDetails = { studentId, name, phone, parentContact, surah, ayah, para ->
                                studentRepository.updateStudentDetails(
                                    studentId = studentId,
                                    name = name,
                                    phone = phone,
                                    parentContact = parentContact,
                                    surah = surah,
                                    ayah = ayah,
                                    para = para
                                )
                            },
                            lang = currentLang,
                            onStartListening = { student ->
                                navController.navigate("teacher_listening/${student.id}")
                            },
                            onStartCall = { student, isVideo ->
                                coroutineScope.launch {
                                    studentRepository.startCall(student.id, student.name, if (isVideo) "Native Video" else "Audio Call")
                                }
                            },
                            onOpenChat = { student ->
                                selectedStudentForChat = student
                                navController.navigate("chat/${student.id}")
                            },
                            onAddStudent = { name, email, phone, parent, surah, ayah, para ->
                                studentRepository.addStudent(name, email, phone, parent, surah, ayah, para)
                            },
                            onUpdatePointer = { studentId, surah, surahName, ayah, para ->
                                studentRepository.updateStudentLessonPointer(studentId, surah, surahName, ayah, para)
                            }
                        )
                    }

                    // Teacher Attendance (Unified with Students & Attendance)
                    composable(Screen.TeacherAttendance.route) {
                        TeacherStudentsScreen(
                            students = students,
                            allAttendance = allAttendance,
                            selectedDate = selectedDateForAttendance,
                            onDateSelected = { selectedDateForAttendance = it },
                            onCycleStatus = { student ->
                                coroutineScope.launch {
                                    studentRepository.cycleAttendance(student.id, student.name, selectedDateForAttendance)
                                }
                            },
                            onSetAttendance = { studentId, studentName, date, status, notes ->
                                coroutineScope.launch {
                                    studentRepository.setAttendance(studentId, studentName, date, status, notes)
                                }
                            },
                            onBulkMarkAttendance = { date, status ->
                                coroutineScope.launch {
                                    studentRepository.bulkMarkAttendance(date, status)
                                }
                            },
                            onUpdateStudentDetails = { studentId, name, phone, parentContact, surah, ayah, para ->
                                studentRepository.updateStudentDetails(
                                    studentId = studentId,
                                    name = name,
                                    phone = phone,
                                    parentContact = parentContact,
                                    surah = surah,
                                    ayah = ayah,
                                    para = para
                                )
                            },
                            lang = currentLang,
                            onStartListening = { student ->
                                navController.navigate("teacher_listening/${student.id}")
                            },
                            onStartCall = { student, isVideo ->
                                coroutineScope.launch {
                                    studentRepository.startCall(student.id, student.name, if (isVideo) "Native Video" else "Audio Call")
                                }
                            },
                            onOpenChat = { student ->
                                selectedStudentForChat = student
                                navController.navigate("chat/${student.id}")
                            },
                            onAddStudent = { name, email, phone, parent, surah, ayah, para ->
                                studentRepository.addStudent(name, email, phone, parent, surah, ayah, para)
                            },
                            onUpdatePointer = { studentId, surah, surahName, ayah, para ->
                                studentRepository.updateStudentLessonPointer(studentId, surah, surahName, ayah, para)
                            }
                        )
                    }

                    // Chat List Screen ("chats" route)
                    composable("chats") {
                        currentUser?.let { user ->
                            ChatsListScreen(
                                currentUser = user,
                                students = students,
                                messages = allMessages,
                                callLogs = allCallLogs,
                                lang = currentLang,
                                onNavigateToConversation = { chatId ->
                                    navController.navigate("chat/$chatId")
                                },
                                onStartCall = { targetUser, isVideo ->
                                    coroutineScope.launch {
                                        studentRepository.startCall(targetUser.id, targetUser.name, if (isVideo) "Native Video" else "Audio Call")
                                    }
                                },
                                onToggleLanguage = { studentRepository.toggleLanguage() },
                                onAddStudent = { name, email, phone, parent, surah, ayah, para ->
                                    studentRepository.addStudent(name, email, phone, parent, surah, ayah, para)
                                },
                                onNavigateToDashboard = {
                                    val target = if (userRole == UserRole.TEACHER) Screen.TeacherDashboard.route else Screen.StudentDashboard.route
                                    navController.navigate(target) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                    }
                                },
                                onNavigateToQuran = {
                                    navController.navigate(Screen.QuranBrowser.route) { launchSingleTop = true }
                                },
                                onNavigateToSettings = {
                                    navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                                }
                            )
                        }
                    }

                    // 1-on-1 Conversation Screen ("chat/{chatId}" route with backstack handling)
                    composable(
                        route = "chat/{chatId}",
                        arguments = listOf(navArgument("chatId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                        currentUser?.let { user ->
                            ConversationScreen(
                                chatId = chatId,
                                currentUser = user,
                                students = students,
                                messages = allMessages,
                                lang = currentLang,
                                onBackClick = { navController.popBackStack() },
                                onSendMessage = { studentId, text ->
                                    coroutineScope.launch {
                                        studentRepository.sendChatMessage(
                                            studentId = studentId,
                                            senderId = user.id,
                                            senderName = user.name,
                                            isFromTeacher = user.role == UserRole.TEACHER,
                                            text = text
                                        )
                                    }
                                },
                                onStartCall = { targetUser, isVideo ->
                                    coroutineScope.launch {
                                        studentRepository.startCall(targetUser.id, targetUser.name, if (isVideo) "Native Video" else "Audio Call")
                                    }
                                }
                            )
                        }
                    }

                    // Forwarders for legacy chat routes
                    composable("teacher_chat") {
                        navController.navigate("chats") {
                            popUpTo("teacher_chat") { inclusive = true }
                        }
                    }
                    composable("student_chat") {
                        navController.navigate("chats") {
                            popUpTo("student_chat") { inclusive = true }
                        }
                    }

                    // Quran Browser
                    composable(Screen.QuranBrowser.route) {
                        QuranBrowserScreen(
                            surahs = QuranRepository.surahsList,
                            paras = QuranRepository.parasList,
                            bookmarks = bookmarks,
                            lang = currentLang,
                            onSelectSurah = { surahNumber, targetAyah ->
                                navController.navigate("surah_reader/$surahNumber/$targetAyah")
                            },
                            onSelectPara = { paraNumber ->
                                val para = QuranRepository.parasList.find { it.number == paraNumber }
                                val sNum = para?.startSurahNumber ?: 1
                                navController.navigate("surah_reader/$sNum/1")
                            },
                            onDeleteBookmark = { bookmark ->
                                coroutineScope.launch {
                                    quranRepository.deleteBookmark(bookmark)
                                }
                            }
                        )
                    }

                    // Teacher Calls
                    composable(Screen.TeacherCalls.route) {
                        CallsScreen(
                            students = students,
                            callLogs = allCallLogs,
                            onStartNativeCall = { student ->
                                coroutineScope.launch {
                                    studentRepository.startCall(student.id, student.name, "Native Video")
                                }
                            },
                            onSendMeetingInvite = { student, link ->
                                coroutineScope.launch {
                                    studentRepository.startCall(student.id, student.name, "Google Meet / Zoom", link)
                                    studentRepository.sendChatMessage(
                                        studentId = student.id,
                                        senderId = "teacher_1",
                                        senderName = "Ustadha Aminah Khan",
                                        isFromTeacher = true,
                                        text = "Join our live Sabaq recitation room here: $link"
                                    )
                                }
                            },
                            lang = currentLang
                        )
                    }

                    // Student Dashboard
                    composable(Screen.StudentDashboard.route) {
                        currentUser?.let { student ->
                            val recentSessions by studentRepository.getSessionsForStudent(student.id).collectAsState(initial = emptyList())
                            val studentAttendance by studentRepository.getAttendanceForStudent(student.id).collectAsState(initial = emptyList())

                            StudentDashboardScreen(
                                student = student,
                                recentSessions = recentSessions,
                                attendanceRecords = studentAttendance,
                                notifications = notifications,
                                lang = currentLang,
                                onContinueSabaq = { surahNumber, ayahNumber ->
                                    navController.navigate("surah_reader/$surahNumber/$ayahNumber")
                                },
                                onOpenChat = { navController.navigate(Screen.StudentChat.route) },
                                onOpenQuranBrowser = { navController.navigate(Screen.QuranBrowser.route) },
                                onMarkNotificationRead = { notifId ->
                                    studentRepository.markNotificationRead(notifId)
                                },
                                onDismissNotification = { notifId ->
                                    studentRepository.dismissNotification(notifId)
                                },
                                onMarkAllNotificationsRead = {
                                    studentRepository.markAllNotificationsRead()
                                }
                            )
                        }
                    }

                    // Student Attendance
                    composable(Screen.StudentAttendance.route) {
                        currentUser?.let { student ->
                            val studentAttendance by studentRepository.getAttendanceForStudent(student.id).collectAsState(initial = emptyList())
                            StudentAttendanceScreen(
                                attendanceRecords = studentAttendance,
                                lang = currentLang
                            )
                        }
                    }

                    // Profile
                    composable(Screen.Profile.route) {
                        currentUser?.let { user ->
                            ProfileScreen(
                                user = user,
                                lang = currentLang,
                                onUpdateTeacherProfile = { name, phone, bio, qualifications, institution, experienceYears, availableTimings ->
                                    studentRepository.updateTeacherProfile(
                                        name = name,
                                        phone = phone,
                                        bio = bio,
                                        qualifications = qualifications,
                                        institution = institution,
                                        experienceYears = experienceYears,
                                        availableTimings = availableTimings
                                    )
                                },
                                onUpdateStudentProfile = { name, phone, parentContact, ageGrade, courseTrack ->
                                    studentRepository.updateUserProfile(
                                        name = name,
                                        phone = phone,
                                        bio = "",
                                        qualifications = "",
                                        parentContact = parentContact,
                                        ageGrade = ageGrade,
                                        courseTrack = courseTrack
                                    )
                                }
                            )
                        }
                    }

                    // Settings (with embedded Profile)
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            currentLanguage = currentLang,
                            onToggleLanguage = { studentRepository.toggleLanguage() },
                            themeMode = themeMode,
                            onSetThemeMode = { studentRepository.setThemeMode(it) },
                            currentRole = userRole,
                            onSwitchRole = {
                                val nextRole = if (userRole == UserRole.TEACHER) UserRole.STUDENT else UserRole.TEACHER
                                studentRepository.switchUserRole(nextRole)
                                val targetDest = if (nextRole == UserRole.TEACHER) Screen.TeacherStudents.route else Screen.StudentDashboard.route
                                navController.navigate(targetDest) {
                                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                }
                            },
                            onNavigateToPrivacy = { navController.navigate("legal/true") },
                            onNavigateToTerms = { navController.navigate("legal/false") },
                            onLogout = {
                                studentRepository.logout()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            user = currentUser,
                            onUpdateProfile = { name, phone, bio, qualifications ->
                                studentRepository.updateUserProfile(name, phone, bio, qualifications)
                            }
                        )
                    }

                    // Surah Reader Screen
                    composable(
                        route = "surah_reader/{surahNumber}/{ayahNumber}",
                        arguments = listOf(
                            navArgument("surahNumber") { type = NavType.IntType },
                            navArgument("ayahNumber") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val sNum = backStackEntry.arguments?.getInt("surahNumber") ?: 1
                        val aNum = backStackEntry.arguments?.getInt("ayahNumber") ?: 1
                        val surah = QuranRepository.surahsList.find { it.number == sNum } ?: QuranRepository.surahsList.first()
                        val ayahs = quranRepository.getAyahsForSurah(sNum)

                        Column(modifier = Modifier.fillMaxSize()) {
                            QuranTopBar(
                                title = surah.nameEnglish,
                                subtitle = "${surah.nameArabic} • Ayahs 1-${surah.totalAyahs}",
                                showBackButton = true,
                                onBackClick = { navController.popBackStack() },
                                currentLanguage = currentLang,
                                onToggleLanguage = { studentRepository.toggleLanguage() },
                                currentRole = userRole,
                                onSwitchRole = {},
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = onToggleDarkTheme
                            )

                            SurahReaderScreen(
                                surah = surah,
                                ayahs = ayahs,
                                initialAyahNumber = aNum,
                                currentPlayingAyah = if (playbackState.currentSurahNumber == sNum) playbackState.currentAyahNumber else 0,
                                isPlaying = playbackState.isPlaying,
                                bookmarks = bookmarks,
                                onPlayAyahAudio = { ayah ->
                                    audioPlayerManager.playAyah(
                                        surahNumber = sNum,
                                        surahName = surah.nameEnglish,
                                        ayahNumber = ayah.ayahNumberInSurah,
                                        audioUrl = ayah.audioUrl
                                    )
                                },
                                onSaveBookmark = { sNo, sName, ayahNo, note ->
                                    coroutineScope.launch {
                                        quranRepository.addBookmark(sNo, sName, ayahNo, note)
                                    }
                                },
                                lang = currentLang
                            )
                        }
                    }

                    // Live Listening Session Screen
                    composable(
                        route = "teacher_listening/{studentId}",
                        arguments = listOf(navArgument("studentId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                        val student = studentRepository.getStudentById(studentId)

                        if (student != null) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                QuranTopBar(
                                    title = "Sabaq Listening Session",
                                    subtitle = "${student.name} • ${student.currentSurahName}",
                                    showBackButton = true,
                                    onBackClick = { navController.popBackStack() },
                                    currentLanguage = currentLang,
                                    onToggleLanguage = { studentRepository.toggleLanguage() }
                                )

                                TeacherListeningSessionScreen(
                                    student = student,
                                    quranRepository = quranRepository,
                                    lang = currentLang,
                                    onPlayAudio = { ayah ->
                                        audioPlayerManager.playAyah(
                                            surahNumber = student.currentSurah,
                                            surahName = student.currentSurahName,
                                            ayahNumber = ayah.ayahNumberInSurah,
                                            audioUrl = ayah.audioUrl
                                        )
                                    },
                                    onCompleteSession = { correct, mistake, practice, remarks, advance, endAyah ->
                                        coroutineScope.launch {
                                            studentRepository.completeListeningSession(
                                                studentId = student.id,
                                                studentName = student.name,
                                                surahNumber = student.currentSurah,
                                                surahName = student.currentSurahName,
                                                startAyah = student.currentAyah,
                                                endAyah = endAyah,
                                                correctCount = correct,
                                                mistakeCount = mistake,
                                                needsPracticeCount = practice,
                                                remarks = remarks,
                                                advanceLessonPointer = advance
                                            )
                                            navController.popBackStack()
                                        }
                                    },
                                    onCancel = { navController.popBackStack() }
                                )
                            }
                        }
                    }

                    // Legal Screen (Privacy Policy / Terms)
                    composable(
                        route = "legal/{isPrivacy}",
                        arguments = listOf(navArgument("isPrivacy") { type = NavType.BoolType })
                    ) { backStackEntry ->
                        val isPrivacy = backStackEntry.arguments?.getBoolean("isPrivacy") ?: true
                        LegalScreen(
                            isPrivacyPolicy = isPrivacy,
                            lang = currentLang,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }

                // Audio Mini-Player floating over bottom
                AudioMiniPlayer(
                    state = playbackState,
                    onPlayPauseToggle = {
                        if (playbackState.isPlaying) audioPlayerManager.pause() else audioPlayerManager.resume()
                    },
                    onDismiss = { audioPlayerManager.stop() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }

        // Incoming Call Modal Dialog (Simulation when call starts)
        incomingCall?.let { call ->
            IncomingCallDialog(
                call = call,
                lang = currentLang,
                onAccept = {
                    studentRepository.dismissIncomingCall()
                    // Active call already set, will render ActiveCallScreen
                },
                onDismiss = {
                    studentRepository.dismissIncomingCall()
                    studentRepository.endActiveCall()
                }
            )
        }

        // Active In-App Video Call Screen
        activeCall?.let { call ->
            ActiveCallScreen(
                call = call,
                lang = currentLang,
                onEndCall = {
                    studentRepository.endActiveCall()
                }
            )
        }
    }
}
