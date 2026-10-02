package com.example.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.components.GoogleSavePasswordCard
import com.example.ui.components.GoogleSavePasswordDialog
import com.example.ui.components.MihrabArchShape
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald
import com.example.ui.theme.IslamicEmeraldDark
import com.example.ui.theme.IslamicGold

@Composable
fun LoginScreen(
    lang: String,
    onLoginSuccess: (email: String, role: UserRole) -> Unit,
    onNavigateToSignup: () -> Unit
) {
    var selectedRoleIndex by remember { mutableIntStateOf(0) } // 0: Teacher, 1: Student
    var email by remember { mutableStateOf("ustadha.aminah@quransunao.com") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailSent by remember { mutableStateOf(false) }
    var savePasswordToGoogle by remember { mutableStateOf(true) }
    var showGoogleSavePrompt by remember { mutableStateOf(false) }

    val currentRole = if (selectedRoleIndex == 0) UserRole.TEACHER else UserRole.STUDENT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Logo & Mihrab Header
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(MihrabArchShape)
                    .background(
                        Brush.linearGradient(
                            listOf(IslamicEmerald, IslamicEmeraldDark)
                        )
                    )
                    .border(2.dp, IslamicGold, MihrabArchShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_quran_logo),
                    contentDescription = "Quran Sunao Logo",
                    modifier = Modifier.size(62.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = AppStrings.get("app_title", lang),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )

            Text(
                text = AppStrings.get("app_tagline", lang),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Role Selector Tab: Teacher vs Student
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, IslamicGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                TabRow(
                    selectedTabIndex = selectedRoleIndex,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedRoleIndex == 0,
                        onClick = {
                            selectedRoleIndex = 0
                            email = "ustadha.aminah@quransunao.com"
                        },
                        text = {
                            Text(
                                text = AppStrings.get("role_teacher", lang),
                                fontWeight = if (selectedRoleIndex == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedRoleIndex == 1,
                        onClick = {
                            selectedRoleIndex = 1
                            email = "fatima@quransunao.com"
                        },
                        text = {
                            Text(
                                text = AppStrings.get("role_student", lang),
                                fontWeight = if (selectedRoleIndex == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form inputs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (selectedRoleIndex == 0) "Teacher Portal Login" else "Student Portal Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text(AppStrings.get("email", lang)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email", tint = IslamicEmerald)
                        },
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text(AppStrings.get("password", lang)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password", tint = IslamicEmerald)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = passwordError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showForgotPasswordDialog = true }) {
                            Text(
                                text = AppStrings.get("forgot_password", lang),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Option to save password in Google Password Manager / Smart Lock
                    GoogleSavePasswordCard(
                        enabled = savePasswordToGoogle,
                        onToggle = { savePasswordToGoogle = it },
                        lang = lang
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            // Validation
                            var valid = true
                            if (email.isBlank() || !email.contains("@")) {
                                emailError = "Please enter a valid email address"
                                valid = false
                            }
                            if (password.length < 6) {
                                passwordError = "Password must be at least 6 characters"
                                valid = false
                            }
                            if (valid) {
                                if (savePasswordToGoogle) {
                                    showGoogleSavePrompt = true
                                } else {
                                    onLoginSuccess(email, currentRole)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicEmerald)
                    ) {
                        Text(
                            text = AppStrings.get("continue_email", lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            // One-tap Google sign-in simulation
                            onLoginSuccess(
                                if (selectedRoleIndex == 0) "teacher.google@quransunao.com" else "student.google@quransunao.com",
                                currentRole
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Google",
                            tint = IslamicGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppStrings.get("continue_google", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Teacher signup link
            if (selectedRoleIndex == 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Qur'an Teacher?",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onNavigateToSignup) {
                        Text(
                            text = "Register Teacher Account",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Text(
                    text = "Student accounts are enrolled directly by their Ustadha.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Forgot password dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = {
                    showForgotPasswordDialog = false
                    resetEmailSent = false
                },
                title = { Text(AppStrings.get("forgot_password", lang)) },
                text = {
                    if (resetEmailSent) {
                        Text("A password reset link with OTP verification has been sent to $email. Please check your inbox.")
                    } else {
                        Column {
                            Text("Enter your account email to receive a password reset instructions link.")
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                },
                confirmButton = {
                    if (resetEmailSent) {
                        Button(onClick = {
                            showForgotPasswordDialog = false
                            resetEmailSent = false
                        }) {
                            Text("Done")
                        }
                    } else {
                        Button(onClick = { resetEmailSent = true }) {
                            Text("Send Reset Link")
                        }
                    }
                },
                dismissButton = {
                    if (!resetEmailSent) {
                        TextButton(onClick = { showForgotPasswordDialog = false }) {
                            Text(AppStrings.get("cancel", lang))
                        }
                    }
                }
            )
        }

        // Google Password Manager Save Prompt
        if (showGoogleSavePrompt) {
            GoogleSavePasswordDialog(
                email = email,
                onSaveToGoogle = {
                    showGoogleSavePrompt = false
                    onLoginSuccess(email, currentRole)
                },
                onDismiss = {
                    showGoogleSavePrompt = false
                    onLoginSuccess(email, currentRole)
                }
            )
        }
    }
}
