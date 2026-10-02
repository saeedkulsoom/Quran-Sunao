package com.example.ui.screens.profile

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.components.MihrabArchShape
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight

@Composable
fun ProfileScreen(
    user: User,
    lang: String,
    onUpdateTeacherProfile: (name: String, phone: String, bio: String, qualifications: String, institution: String, experienceYears: String, availableTimings: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onUpdateStudentProfile: (name: String, phone: String, parentContact: String, ageGrade: String, courseTrack: String) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    val isTeacher = user.role == UserRole.TEACHER
    var showTeacherEditDialog by remember { mutableStateOf(false) }
    var showStudentEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Profile Top Header Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, IslamicGold.copy(alpha = 0.5f), MihrabArchShape),
                shape = MihrabArchShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(IslamicEmerald)
                            .border(2.dp, IslamicGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(2).uppercase(),
                            color = IslamicGold,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (isTeacher) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Teacher",
                                tint = IslamicGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.email,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = if (isTeacher) "Qur'an Teacher / Ustadha" else "Enrolled Student (${user.courseTrack.ifEmpty { "Nazra Quran" }})",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            if (isTeacher) showTeacherEditDialog = true else showStudentEditDialog = true
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isTeacher) "Edit Teacher Information" else "Edit Student Information",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // WhatsApp Direct Link Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Instant WhatsApp Support & Helpline",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Connect directly with our support team or teacher",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Link 1: Teacher Direct
                    WhatsAppRow(
                        title = AppStrings.get("whatsapp_1", lang),
                        number = "+92 300 5558822",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/923005558822?text=Assalamu%20Alaikum%20Ustadha,%20I%20have%20a%20question%20regarding%20my%20Quran%20Sabaq."))
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Link 2: Parent Helpline
                    WhatsAppRow(
                        title = AppStrings.get("whatsapp_2", lang),
                        number = "+92 300 5558833",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/923005558833?text=Assalamu%20Alaikum,%20I%20am%20calling%20from%20Quran%20Sunao%20parent%20support."))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        if (isTeacher) {
            // Detailed Teacher Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = IslamicEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Teacher Academic & Professional Details", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(icon = Icons.Default.Phone, label = "Contact Phone", value = user.phone.ifEmpty { "Not specified" })
                        InfoRow(icon = Icons.Default.LocationCity, label = "Institution / Madrasa", value = user.institution.ifEmpty { "Jamia Darul Quran & Sunnah" })
                        InfoRow(icon = Icons.Default.Timeline, label = "Teaching Experience", value = user.experienceYears.ifEmpty { "8+ Years in Tajweed & Hifz instruction" })
                        InfoRow(icon = Icons.Default.AccessTime, label = "Available Daily Timings", value = user.availableTimings.ifEmpty { "4:00 PM - 9:00 PM (PKT)" })

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = AppStrings.get("bio", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = user.bio.ifEmpty { "Dedicated Quran teacher specializing in Tajweed rules, correct Makhaarij pronunciation, and structured Sabaq/Sabqi memorization." },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = AppStrings.get("qualifications", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = user.qualifications.ifEmpty { "Hafiza-e-Quran, Certified Sanad in Hafs 'an 'Asim, Wifaq-ul-Madaris Al-Arabia Alimiyyah Degree" },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 19.sp
                        )
                    }
                }
            }
        } else {
            // Detailed Student Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = IslamicEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enrolled Student Information", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(icon = Icons.Default.MenuBook, label = "Current Lesson Pointer", value = "Para ${user.currentPara} • ${user.currentSurahName} (Ayah ${user.currentAyah})")
                        InfoRow(icon = Icons.Default.School, label = "Course Track", value = user.courseTrack.ifEmpty { "Nazra Quran" })
                        InfoRow(icon = Icons.Default.CalendarMonth, label = "Age & Academic Grade", value = user.ageGrade.ifEmpty { "Not specified" })
                        InfoRow(icon = Icons.Default.Phone, label = "Student Phone", value = user.phone.ifEmpty { "Not provided" })
                        InfoRow(icon = Icons.Default.ContactPhone, label = "Parent / Guardian Contact", value = user.parentContact.ifEmpty { "Not provided" })
                        InfoRow(icon = Icons.Default.CalendarMonth, label = "Enrollment Date", value = user.joinedDate)

                        if (user.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            InfoRow(icon = Icons.Default.Description, label = "Teacher Remarks", value = user.notes)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Teacher edit dialog
    if (showTeacherEditDialog) {
        var editName by remember { mutableStateOf(user.name) }
        var editPhone by remember { mutableStateOf(user.phone) }
        var editInstitution by remember { mutableStateOf(user.institution) }
        var editExperience by remember { mutableStateOf(user.experienceYears) }
        var editTimings by remember { mutableStateOf(user.availableTimings) }
        var editBio by remember { mutableStateOf(user.bio) }
        var editQual by remember { mutableStateOf(user.qualifications) }

        AlertDialog(
            onDismissRequest = { showTeacherEditDialog = false },
            title = { Text("Update Teacher Profile & Details", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Contact Phone") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = editInstitution,
                            onValueChange = { editInstitution = it },
                            label = { Text("Institution / Madrasa") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = editExperience,
                            onValueChange = { editExperience = it },
                            label = { Text("Teaching Experience (e.g. 5 Years)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = editTimings,
                            onValueChange = { editTimings = it },
                            label = { Text("Daily Available Timings") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            label = { Text("Bio & Teaching Approach") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = editQual,
                            onValueChange = { editQual = it },
                            label = { Text("Certifications & Ijazah") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateTeacherProfile(
                            editName.trim(),
                            editPhone.trim(),
                            editBio.trim(),
                            editQual.trim(),
                            editInstitution.trim(),
                            editExperience.trim(),
                            editTimings.trim()
                        )
                        Toast.makeText(context, "Teacher profile updated successfully!", Toast.LENGTH_SHORT).show()
                        showTeacherEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTeacherEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Student edit dialog
    if (showStudentEditDialog) {
        var editName by remember { mutableStateOf(user.name) }
        var editPhone by remember { mutableStateOf(user.phone) }
        var editParentContact by remember { mutableStateOf(user.parentContact) }
        var editAgeGrade by remember { mutableStateOf(user.ageGrade) }
        var editCourseTrack by remember { mutableStateOf(user.courseTrack.ifEmpty { "Nazra Quran" }) }

        AlertDialog(
            onDismissRequest = { showStudentEditDialog = false },
            title = { Text("Update Student Information", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Student Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editParentContact,
                        onValueChange = { editParentContact = it },
                        label = { Text("Parent / Guardian Contact") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAgeGrade,
                        onValueChange = { editAgeGrade = it },
                        label = { Text("Age & Academic Grade") },
                        placeholder = { Text("e.g. 12 Years • Grade 7") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCourseTrack,
                        onValueChange = { editCourseTrack = it },
                        label = { Text("Course Track") },
                        placeholder = { Text("Nazra Quran, Hifz, Tajweed, etc.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateStudentProfile(
                            editName.trim(),
                            editPhone.trim(),
                            editParentContact.trim(),
                            editAgeGrade.trim(),
                            editCourseTrack.trim()
                        )
                        Toast.makeText(context, "Student details saved successfully!", Toast.LENGTH_SHORT).show()
                        showStudentEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStudentEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WhatsAppRow(title: String, number: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.dp, Color(0xFF25D366).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        color = Color(0xFF25D366).copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E7E34))
                Text(number, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(
                color = Color(0xFF25D366),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Open WhatsApp",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = IslamicEmerald, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
