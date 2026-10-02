package com.example.data.repository

import android.content.Context
import com.example.data.local.AttendanceDao
import com.example.data.local.CallLogDao
import com.example.data.local.ChatMessageDao
import com.example.data.local.ListeningSessionDao
import com.example.data.model.AttendanceEntity
import com.example.data.model.AttendanceStatus
import com.example.data.model.CallLogEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ListeningSessionEntity
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StudentRepository(
    private val context: Context,
    private val attendanceDao: AttendanceDao,
    private val sessionDao: ListeningSessionDao,
    private val chatMessageDao: ChatMessageDao,
    private val callLogDao: CallLogDao
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // Current logged in user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Students managed by teacher
    private val _students = MutableStateFlow<List<User>>(emptyList())
    val students: StateFlow<List<User>> = _students.asStateFlow()

    // App language: "en" or "ur"
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // App theme mode: "light", "dark", "system"
    private val _themeMode = MutableStateFlow("light")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Incoming call state for student simulation
    private val _incomingCall = MutableStateFlow<CallLogEntity?>(null)
    val incomingCall: StateFlow<CallLogEntity?> = _incomingCall.asStateFlow()

    // Active call state
    private val _activeCall = MutableStateFlow<CallLogEntity?>(null)
    val activeCall: StateFlow<CallLogEntity?> = _activeCall.asStateFlow()

    // Lively Notifications StateFlow
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Google Password save preference
    private val _isGooglePasswordSaved = MutableStateFlow(true)
    val isGooglePasswordSaved: StateFlow<Boolean> = _isGooglePasswordSaved.asStateFlow()

    fun setGooglePasswordSaved(saved: Boolean) {
        _isGooglePasswordSaved.value = saved
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun addNotification(notification: NotificationItem) {
        _notifications.value = listOf(notification) + _notifications.value
    }

    fun loadTeacherNotifications() {
        _notifications.value = listOf(
            NotificationItem(
                id = "notif_1",
                title = "Welcome to Quran Sunao",
                message = "Enroll your students with their Sabaq pointer to track daily attendance and recitation.",
                timestamp = "Today",
                type = NotificationType.SABAQ,
                isRead = false,
                actionText = "Add Student"
            ),
            NotificationItem(
                id = "notif_2",
                title = "Daily Attendance Register",
                message = "Mark today's attendance with 1-tap Present, Absent, or Leave tracking.",
                timestamp = "Today",
                type = NotificationType.ATTENDANCE,
                isRead = false,
                actionText = "Mark Attendance"
            ),
            NotificationItem(
                id = "notif_3",
                title = "Ayah of the Day",
                message = "“And recite the Qur'an with measured recitation.” (Surah Al-Muzzammil 73:4)",
                timestamp = "Today",
                type = NotificationType.HADITH,
                isRead = true,
                actionText = "Read Quran"
            )
        )
    }

    fun loadStudentNotifications() {
        _notifications.value = listOf(
            NotificationItem(
                id = "notif_s1",
                title = "Daily Sabaq Reminder",
                message = "Revise your current Para before class recitation.",
                timestamp = "Today",
                type = NotificationType.SABAQ,
                isRead = false,
                actionText = "Open Sabaq"
            ),
            NotificationItem(
                id = "notif_s2",
                title = "Daily Motivation",
                message = "“The best among you are those who learn the Qur'an and teach it.” (Bukhari)",
                timestamp = "Today",
                type = NotificationType.HADITH,
                isRead = true
            )
        )
    }

    // Today's date string YYYY-MM-DD
    val todayDateString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        // Clean start: No dummy seed students
        _students.value = emptyList()

        // Teacher profile ready for information editing
        val teacherUser = User(
            id = "teacher_1",
            email = "teacher@quransunao.com",
            name = "Ustadha Aminah Khan",
            role = UserRole.TEACHER,
            phone = "+92 300 5558822",
            parentContact = "+92 300 5558833",
            bio = "Certified Hafiza with Ijazah in Tajweed from Al-Azhar (Cairo). Guiding students in beautiful recitation and retention.",
            qualifications = "Ijazah in Hafs 'an 'Asim, B.A. Islamic Studies, Certified Tajweed Specialist",
            institution = "Madrasa Quran Sunao",
            experienceYears = "8+ Years",
            availableTimings = "04:00 PM - 08:00 PM (Mon-Sat)"
        )
        _currentUser.value = teacherUser

        loadTeacherNotifications()
    }

    fun login(email: String, role: UserRole): Boolean {
        if (role == UserRole.TEACHER) {
            _currentUser.value = User(
                id = "teacher_1",
                email = email.ifEmpty { "teacher@quransunao.com" },
                name = "Ustadha Aminah Khan",
                role = UserRole.TEACHER,
                phone = "+92 300 5558822",
                institution = "Madrasa Quran Sunao",
                experienceYears = "8+ Years",
                availableTimings = "04:00 PM - 08:00 PM"
            )
            loadTeacherNotifications()
        } else {
            val found = _students.value.find { it.email.equals(email, ignoreCase = true) }
            _currentUser.value = found ?: User(
                id = "std_${System.currentTimeMillis()}",
                email = email.ifEmpty { "student@quransunao.com" },
                name = "Student",
                role = UserRole.STUDENT,
                phone = ""
            )
            loadStudentNotifications()
        }
        return true
    }

    fun signupTeacher(name: String, email: String): Boolean {
        _currentUser.value = User(
            id = "teacher_${System.currentTimeMillis()}",
            email = email,
            name = name,
            role = UserRole.TEACHER,
            bio = "Certified Qur'an Teacher",
            qualifications = "Tajweed & Recitation"
        )
        loadTeacherNotifications()
        return true
    }

    fun logout() {
        _currentUser.value = null
    }

    fun switchUserRole(role: UserRole) {
        if (role == UserRole.TEACHER) {
            login("ustadha.aminah@quransunao.com", UserRole.TEACHER)
        } else {
            val firstStd = _students.value.firstOrNull()
            login(firstStd?.email ?: "fatima@quransunao.com", UserRole.STUDENT)
        }
    }

    fun selectStudentForStudentView(student: User) {
        _currentUser.value = student
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == "en") "ur" else "en"
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    // Add student with comprehensive information
    fun addStudent(
        name: String,
        email: String,
        phone: String,
        parentContact: String,
        surah: Int,
        ayah: Int,
        para: Int,
        ageGrade: String = "",
        courseTrack: String = "Nazra Quran",
        notes: String = ""
    ) {
        val surahName = QuranRepository.surahsList.find { it.number == surah }?.nameEnglish ?: "Al-Fatihah"
        val newStudent = User(
            id = "std_${System.currentTimeMillis()}",
            email = email,
            name = name,
            role = UserRole.STUDENT,
            phone = phone,
            parentContact = parentContact,
            currentPara = para,
            currentSurah = surah,
            currentSurahName = surahName,
            currentAyah = ayah,
            joinedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
            ageGrade = ageGrade,
            courseTrack = courseTrack,
            notes = notes
        )
        _students.value = _students.value + newStudent

        // Welcoming notification
        addNotification(
            NotificationItem(
                id = "notif_${System.currentTimeMillis()}",
                title = "New Student Enrolled",
                message = "$name registered in $courseTrack starting from Para $para.",
                timestamp = "Just now",
                type = NotificationType.SABAQ,
                isRead = false,
                actionText = "View Record",
                studentId = newStudent.id,
                studentName = newStudent.name
            )
        )
    }

    fun updateStudentLessonPointer(studentId: String, newSurah: Int, newSurahName: String, newAyah: Int, newPara: Int) {
        _students.value = _students.value.map { s ->
            if (s.id == studentId) {
                s.copy(
                    currentSurah = newSurah,
                    currentSurahName = newSurahName,
                    currentAyah = newAyah,
                    currentPara = newPara
                )
            } else s
        }

        if (_currentUser.value?.id == studentId) {
            _currentUser.value = _currentUser.value?.copy(
                currentSurah = newSurah,
                currentSurahName = newSurahName,
                currentAyah = newAyah,
                currentPara = newPara
            )
        }
    }

    fun updateTeacherProfile(
        name: String,
        phone: String,
        bio: String,
        qualifications: String,
        institution: String = "",
        experienceYears: String = "",
        availableTimings: String = ""
    ) {
        _currentUser.value = _currentUser.value?.copy(
            name = name,
            phone = phone,
            bio = bio,
            qualifications = qualifications,
            institution = institution.ifEmpty { _currentUser.value?.institution ?: "" },
            experienceYears = experienceYears.ifEmpty { _currentUser.value?.experienceYears ?: "" },
            availableTimings = availableTimings.ifEmpty { _currentUser.value?.availableTimings ?: "" }
        )
    }

    fun updateUserProfile(
        name: String,
        phone: String,
        bio: String,
        qualifications: String,
        parentContact: String = "",
        ageGrade: String = "",
        courseTrack: String = ""
    ) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            name = name,
            phone = phone,
            bio = bio,
            qualifications = qualifications,
            parentContact = parentContact.ifEmpty { current.parentContact },
            ageGrade = ageGrade.ifEmpty { current.ageGrade },
            courseTrack = courseTrack.ifEmpty { current.courseTrack }
        )
        _currentUser.value = updated
        if (current.role == UserRole.STUDENT) {
            _students.value = _students.value.map { if (it.id == current.id) updated else it }
        }
    }

    fun updateStudentDetails(
        studentId: String,
        name: String,
        email: String = "",
        phone: String,
        parentContact: String,
        surah: Int,
        ayah: Int,
        para: Int,
        ageGrade: String = "",
        courseTrack: String = "Nazra Quran",
        notes: String = ""
    ) {
        val surahName = QuranRepository.surahsList.find { it.number == surah }?.nameEnglish ?: "Al-Fatihah"
        _students.value = _students.value.map { s ->
            if (s.id == studentId) {
                s.copy(
                    name = name,
                    email = email.ifEmpty { s.email },
                    phone = phone,
                    parentContact = parentContact,
                    currentSurah = surah,
                    currentSurahName = surahName,
                    currentAyah = ayah,
                    currentPara = para,
                    ageGrade = ageGrade.ifEmpty { s.ageGrade },
                    courseTrack = courseTrack.ifEmpty { s.courseTrack },
                    notes = notes.ifEmpty { s.notes }
                )
            } else s
        }

        if (_currentUser.value?.id == studentId) {
            _currentUser.value = _currentUser.value?.copy(
                name = name,
                email = email.ifEmpty { _currentUser.value?.email ?: "" },
                phone = phone,
                parentContact = parentContact,
                currentSurah = surah,
                currentSurahName = surahName,
                currentAyah = ayah,
                currentPara = para,
                ageGrade = ageGrade.ifEmpty { _currentUser.value?.ageGrade ?: "" },
                courseTrack = courseTrack.ifEmpty { _currentUser.value?.courseTrack ?: "Nazra Quran" },
                notes = notes.ifEmpty { _currentUser.value?.notes ?: "" }
            )
        }
    }

    // Attendance
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceForStudent(studentId)
    }

    fun getAllAttendance(): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAllAttendance()
    }

    suspend fun bulkMarkAttendance(date: String, status: AttendanceStatus) {
        _students.value.forEach { student ->
            val existing = attendanceDao.getTodayAttendanceForStudent(student.id, date)
            attendanceDao.insertOrUpdate(
                AttendanceEntity(
                    id = existing?.id ?: 0,
                    studentId = student.id,
                    studentName = student.name,
                    date = date,
                    status = status,
                    notes = existing?.notes ?: ""
                )
            )
        }
    }

    suspend fun setAttendance(studentId: String, studentName: String, date: String, status: AttendanceStatus, notes: String = "") {
        val existing = attendanceDao.getTodayAttendanceForStudent(studentId, date)
        attendanceDao.insertOrUpdate(
            AttendanceEntity(
                id = existing?.id ?: 0,
                studentId = studentId,
                studentName = studentName,
                date = date,
                status = status,
                notes = notes.ifEmpty { existing?.notes ?: "" }
            )
        )
    }

    suspend fun cycleAttendance(studentId: String, studentName: String, date: String) {
        val existing = attendanceDao.getTodayAttendanceForStudent(studentId, date)
        val nextStatus = when (existing?.status) {
            null -> AttendanceStatus.PRESENT
            AttendanceStatus.PRESENT -> AttendanceStatus.ABSENT
            AttendanceStatus.ABSENT -> AttendanceStatus.LEAVE
            AttendanceStatus.LEAVE -> AttendanceStatus.PRESENT
        }
        attendanceDao.insertOrUpdate(
            AttendanceEntity(
                id = existing?.id ?: 0,
                studentId = studentId,
                studentName = studentName,
                date = date,
                status = nextStatus,
                notes = existing?.notes ?: ""
            )
        )
    }

    // Listening Sessions
    val allSessions: Flow<List<ListeningSessionEntity>> = sessionDao.getAllSessions()

    fun getSessionsForStudent(studentId: String): Flow<List<ListeningSessionEntity>> {
        return sessionDao.getSessionsForStudent(studentId)
    }

    suspend fun completeListeningSession(
        studentId: String,
        studentName: String,
        surahNumber: Int,
        surahName: String,
        startAyah: Int,
        endAyah: Int,
        correctCount: Int,
        mistakeCount: Int,
        needsPracticeCount: Int,
        remarks: String,
        advanceLessonPointer: Boolean
    ) {
        // Record session
        sessionDao.insertSession(
            ListeningSessionEntity(
                studentId = studentId,
                studentName = studentName,
                surahNumber = surahNumber,
                surahName = surahName,
                startAyah = startAyah,
                endAyah = endAyah,
                correctCount = correctCount,
                mistakeCount = mistakeCount,
                needsPracticeCount = needsPracticeCount,
                remarks = remarks,
                timestamp = System.currentTimeMillis(),
                dateString = todayDateString
            )
        )

        // Automatically mark present for today
        setAttendance(studentId, studentName, todayDateString, AttendanceStatus.PRESENT, "Session completed: $surahName Ayah $startAyah-$endAyah")

        // Advance student lesson pointer if enabled
        if (advanceLessonPointer) {
            val surah = QuranRepository.surahsList.find { it.number == surahNumber }
            val nextAyah = endAyah + 1
            if (surah != null && nextAyah > surah.totalAyahs) {
                val nextSurahNum = if (surahNumber < 114) surahNumber + 1 else 1
                val nextSurah = QuranRepository.surahsList.find { it.number == nextSurahNum }
                updateStudentLessonPointer(
                    studentId = studentId,
                    newSurah = nextSurahNum,
                    newSurahName = nextSurah?.nameEnglish ?: "Al-Fatihah",
                    newAyah = 1,
                    newPara = nextSurah?.paraNumber ?: 1
                )
            } else {
                updateStudentLessonPointer(
                    studentId = studentId,
                    newSurah = surahNumber,
                    newSurahName = surahName,
                    newAyah = nextAyah,
                    newPara = surah?.paraNumber ?: 1
                )
            }
        }
    }

    // Chat
    fun getChatMessagesForStudent(studentId: String): Flow<List<ChatMessageEntity>> {
        return chatMessageDao.getMessagesForStudent(studentId)
    }

    val allChatMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()

    suspend fun sendChatMessage(studentId: String, senderId: String, senderName: String, isFromTeacher: Boolean, text: String) {
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                studentId = studentId,
                senderId = senderId,
                senderName = senderName,
                isFromTeacher = isFromTeacher,
                text = text,
                timestamp = System.currentTimeMillis(),
                isRead = isFromTeacher
            )
        )

        // Simulated instant response after 1-2 seconds (as requested)
        if (!isFromTeacher) {
            coroutineScope.launch {
                kotlinx.coroutines.delay(1400)
                val replies = listOf(
                    "Wa Alaikum Assalam! JazakAllah Khair. I have noted this. Keep up your daily recitation!",
                    "ماشاءاللہ! بہت خوب، تجوید کے قواعد کا خاص خیال رکھیں۔",
                    "Masha'Allah! Very pleased with your dedication. We will practice this ayah in our next session.",
                    "وعلیکم السلام! بارک اللہ فیکم، روزانہ آدھا گھنٹہ ضرور دہرائیں۔",
                    "Barakallahu feekum! Please revise Para ${getStudentById(studentId)?.currentPara ?: 1} tonight."
                )
                chatMessageDao.insertMessage(
                    ChatMessageEntity(
                        studentId = studentId,
                        senderId = "teacher_1",
                        senderName = "Ustadha Aminah Khan",
                        isFromTeacher = true,
                        text = replies.random(),
                        timestamp = System.currentTimeMillis(),
                        isRead = false
                    )
                )
            }
        } else {
            coroutineScope.launch {
                kotlinx.coroutines.delay(1400)
                val student = getStudentById(studentId)
                val studentName = student?.name ?: "Student"
                val studentReplies = listOf(
                    "Wa Alaikum Assalam Ustadha! JazakAllahu Khairan, I am revising right now.",
                    "وعلیکم السلام استاد جی! میں نے آج کا پارہ دہرا لیا ہے، سبق سنانے کے لیے تیار ہوں۔",
                    "Ji Ustadha, I noted down your Tajweed instructions carefully.",
                    "جزاک اللہ خیراً! انشاء اللہ کل کی کلاس میں بالکل درست پڑھوں گا۔",
                    "Alhamdulillah, thank you so much Ustadha for your guidance!"
                )
                chatMessageDao.insertMessage(
                    ChatMessageEntity(
                        studentId = studentId,
                        senderId = studentId,
                        senderName = studentName,
                        isFromTeacher = false,
                        text = studentReplies.random(),
                        timestamp = System.currentTimeMillis(),
                        isRead = false
                    )
                )
            }
        }
    }

    suspend fun markChatAsRead(studentId: String) {
        chatMessageDao.markAsRead(studentId)
    }

    // Calls
    val allCallLogs: Flow<List<CallLogEntity>> = callLogDao.getAllCallLogs()

    suspend fun startCall(studentId: String, studentName: String, type: String, link: String = "") {
        val call = CallLogEntity(
            studentId = studentId,
            studentName = studentName,
            type = type,
            link = link,
            durationMinutes = 15,
            timestamp = System.currentTimeMillis()
        )
        callLogDao.insertCallLog(call)
        _activeCall.value = call
        // Also trigger incoming call notification simulation for student
        _incomingCall.value = call
    }

    fun dismissIncomingCall() {
        _incomingCall.value = null
    }

    fun endActiveCall() {
        _activeCall.value = null
        _incomingCall.value = null
    }

    fun getStudentById(studentId: String): User? {
        return _students.value.find { it.id == studentId }
    }
}
