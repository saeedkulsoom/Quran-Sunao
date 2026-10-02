package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.GoogleSavePasswordCard
import com.example.ui.components.GoogleSavePasswordDialog
import com.example.ui.components.QuranTopBar
import com.example.ui.theme.AppStrings
import com.example.ui.theme.IslamicEmerald

@Composable
fun SignupScreen(
    lang: String,
    onSignupSuccess: (name: String, email: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToPrivacy: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToPrivacy by remember { mutableStateOf(false) }
    var savePasswordToGoogle by remember { mutableStateOf(true) }
    var showGoogleSavePrompt by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var privacyError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            QuranTopBar(
                title = "Teacher Registration",
                subtitle = "Set up your Quran Sunao academy",
                showBackButton = true,
                onBackClick = onNavigateToLogin,
                currentLanguage = lang,
                onToggleLanguage = {}
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Create Teacher Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Manage your students' daily Sabaq, revision & attendance",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = null
                            },
                            label = { Text("Full Name (e.g. Ustadha Maryam)") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = IslamicEmerald) },
                            isError = nameError != null,
                            supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = null
                            },
                            label = { Text(AppStrings.get("email", lang)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = IslamicEmerald) },
                            isError = emailError != null,
                            supportingText = emailError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(AppStrings.get("phone_number", lang)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = IslamicEmerald) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = IslamicEmerald) },
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

                        Spacer(modifier = Modifier.height(16.dp))

                        // Privacy policy consent
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = agreedToPrivacy,
                                onCheckedChange = {
                                    agreedToPrivacy = it
                                    privacyError = null
                                },
                                colors = CheckboxDefaults.colors(checkedColor = IslamicEmerald)
                            )
                            Text(
                                text = "I agree to the ",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(
                                onClick = onNavigateToPrivacy,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Privacy Policy & Terms",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (privacyError != null) {
                            Text(
                                text = privacyError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Option to save credentials in Google Password Manager
                        GoogleSavePasswordCard(
                            enabled = savePasswordToGoogle,
                            onToggle = { savePasswordToGoogle = it },
                            lang = lang
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                var valid = true
                                if (name.isBlank()) {
                                    nameError = "Please enter your name"
                                    valid = false
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    emailError = "Please enter a valid email"
                                    valid = false
                                }
                                if (password.length < 6) {
                                    passwordError = "Password must be at least 6 characters"
                                    valid = false
                                }
                                if (!agreedToPrivacy) {
                                    privacyError = "You must agree to the Privacy Policy"
                                    valid = false
                                }
                                if (valid) {
                                    if (savePasswordToGoogle) {
                                        showGoogleSavePrompt = true
                                    } else {
                                        onSignupSuccess(name, email)
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
                                text = "Complete Registration",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already registered?", fontSize = 14.sp)
                    TextButton(onClick = onNavigateToLogin) {
                        Text("Log In Here", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        if (showGoogleSavePrompt) {
            GoogleSavePasswordDialog(
                email = email,
                onSaveToGoogle = {
                    showGoogleSavePrompt = false
                    onSignupSuccess(name, email)
                },
                onDismiss = {
                    showGoogleSavePrompt = false
                    onSignupSuccess(name, email)
                }
            )
        }
    }
}
