package com.example.ui.screens.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuranTopBar

@Composable
fun LegalScreen(
    isPrivacyPolicy: Boolean,
    lang: String,
    onBackClick: () -> Unit
) {
    val title = if (isPrivacyPolicy) "Privacy Policy" else "Terms of Use"
    val subtitle = "Last updated: September 2026 • Quran Sunao Academy"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        QuranTopBar(
            title = title,
            subtitle = subtitle,
            showBackButton = true,
            onBackClick = onBackClick,
            currentLanguage = lang,
            onToggleLanguage = {}
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (isPrivacyPolicy) {
                        LegalSection(
                            heading = "1. Introduction",
                            body = "Quran Sunao ('we', 'our', or 'us') respects the privacy of our teachers, students, and parents. This Privacy Policy describes how we collect, store, and safeguard your personal recitation and educational data in compliance with Google Play Developer Policies and global data protection standards."
                        )
                        LegalSection(
                            heading = "2. Information We Collect",
                            body = "We only collect data necessary to provide and enhance your Qur'an learning experience:\n• Account Information: Name, email address, phone number, and optional parent contact information.\n• Academic Progress: Current Para, Surah, Ayah pointers, attendance records, and teacher remarks.\n• Communication: Messages sent between teachers and students for recitation coordination and lesson reminders."
                        )
                        LegalSection(
                            heading = "3. Offline-First Data Storage",
                            body = "Your daily Qur'an recitation history, bookmarks, and attendance entries are securely stored locally on your device using encrypted Room database storage. You can continue reading and practicing even without an active internet connection."
                        )
                        LegalSection(
                            heading = "4. Zero-Permission Photo & Media Access",
                            body = "We respect your device integrity. We do not request broad device storage permissions. We never sell, rent, or share personal data with third-party advertisers."
                        )
                        LegalSection(
                            heading = "5. Contacting Us",
                            body = "If you have questions regarding this Privacy Policy or your academic data, contact our support team via WhatsApp helpline at +92 300 5558833 or email privacy@quransunao.com."
                        )
                    } else {
                        LegalSection(
                            heading = "1. Acceptance of Terms",
                            body = "By accessing or using Quran Sunao, you agree to be bound by these Terms of Use. If you do not agree to these terms, please do not use the application."
                        )
                        LegalSection(
                            heading = "2. Role of the Platform",
                            body = "Quran Sunao provides an interactive educational portal designed to facilitate daily Sabaq tracking, attendance record keeping, and teacher-student recitation sessions."
                        )
                        LegalSection(
                            heading = "3. User Conduct",
                            body = "Users agree to use Quran Sunao with adab (respect) and integrity. Any harassment, abusive language in chats, or misuse of video call facilities will result in immediate termination of account access."
                        )
                        LegalSection(
                            heading = "4. Sacred Text & Integrity",
                            body = "The Holy Qur'an text and translations included in this application are preserved with rigorous care according to the Medina Mushaf (Hafs 'an 'Asim standard). No unauthorized modifications to the sacred text are permitted."
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun LegalSection(heading: String, body: String) {
    Text(
        text = heading,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = body,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 20.sp
    )
    Spacer(modifier = Modifier.height(16.dp))
}
