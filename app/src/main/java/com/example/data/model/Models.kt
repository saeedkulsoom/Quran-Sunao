package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    TEACHER,
    STUDENT
}

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val phone: String = "",
    val parentContact: String = "",
    val currentPara: Int = 1,
    val currentSurah: Int = 1,
    val currentSurahName: String = "Al-Fatihah",
    val currentAyah: Int = 1,
    val joinedDate: String = "",
    val bio: String = "",
    val qualifications: String = "",
    val ageGrade: String = "",
    val courseTrack: String = "Nazra Quran",
    val institution: String = "",
    val experienceYears: String = "",
    val availableTimings: String = "",
    val notes: String = ""
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LEAVE
}

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val date: String, // format: "YYYY-MM-DD"
    val status: AttendanceStatus,
    val notes: String = ""
)

@Entity(tableName = "sessions")
data class ListeningSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val surahNumber: Int,
    val surahName: String,
    val startAyah: Int,
    val endAyah: Int,
    val correctCount: Int,
    val mistakeCount: Int,
    val needsPracticeCount: Int,
    val remarks: String,
    val timestamp: Long,
    val dateString: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val senderId: String,
    val senderName: String,
    val isFromTeacher: Boolean,
    val text: String,
    val timestamp: Long,
    val isRead: Boolean = true
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val textArabic: String = "",
    val note: String = "",
    val timestamp: Long
)

@Entity(tableName = "call_logs")
data class CallLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val type: String, // "IN_APP", "GOOGLE_MEET", "ZOOM"
    val link: String = "",
    val durationMinutes: Int = 15,
    val timestamp: Long
)

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameUrdu: String,
    val englishTranslation: String,
    val totalAyahs: Int,
    val revelationType: String, // "Meccan" or "Medinan"
    val paraNumber: Int
)

data class Ayah(
    val globalNumber: Int,
    val surahNumber: Int,
    val ayahNumberInSurah: Int,
    val textArabic: String,
    val textEnglish: String,
    val textUrdu: String,
    val audioUrl: String
)

data class Para(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val startSurahNumber: Int,
    val startSurahName: String,
    val startAyah: Int
) {
    val startSurahAyah: String get() = "Ayah $startAyah"
}

enum class AyahGrade {
    UNGRADED,
    CORRECT,
    MISTAKE,
    NEEDS_PRACTICE
}

enum class NotificationType {
    SABAQ,
    ATTENDANCE,
    CALL,
    CHAT,
    HADITH
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val actionText: String? = null,
    val studentId: String? = null,
    val studentName: String? = null
)
