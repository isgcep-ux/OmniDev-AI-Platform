package com.example.ui.screens.auth

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextSecondary
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevBlueLight
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight
import com.example.ui.theme.DevRose

@Composable
fun LoginScreen(
    isAuthenticated: Boolean,
    currentUserEmail: String?,
    currentDisplayName: String?,
    isGuest: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onSignInEmail: (String, String) -> Unit,
    onSignUpEmail: (String, String) -> Unit,
    onSignInGoogle: (Context) -> Unit,
    onContinueGuest: () -> Unit,
    onSignOut: () -> Unit,
    onDismissError: () -> Unit,
    onEnterWorkspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var selectedAuthTab by remember { mutableStateOf(0) } // 0: Sign In, 1: Sign Up
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var localValidationMsg by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF090D16),
                        Color(0xFF04060A)
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Logo & Branding
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(DevBlueLight, DevIndigoLight)
                        )
                    )
                    .border(1.5.dp, Color(0x5538BDF8), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "DevStudio Logo",
                    tint = Color(0xFF040814),
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "DevStudio Hub",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Project IDX & Google AI Studio Developer Suite",
                color = DarkTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // If already authenticated or in guest mode, show active session card
            if (isAuthenticated || isGuest) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_active_session_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(DevEmeraldLight.copy(alpha = 0.5f), DevBlueLight.copy(alpha = 0.5f))
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isGuest) DevAmber.copy(alpha = 0.2f) else DevEmeraldLight.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isGuest) Icons.Default.AccountCircle else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (isGuest) DevAmber else DevEmeraldLight,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isGuest) "Guest Developer Mode" else "Authenticated Developer",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = currentUserEmail ?: if (isGuest) "Local / Offline Exploration Mode" else (currentDisplayName ?: "Signed in via Google / Firebase"),
                            color = DarkTextSecondary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onEnterWorkspace,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("enter_workspace_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DevBlueLight,
                                    contentColor = Color(0xFF040814)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Enter Workspace", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }

                            OutlinedButton(
                                onClick = onSignOut,
                                modifier = Modifier.testTag("sign_out_btn"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DevRose),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                                    brush = Brush.linearGradient(listOf(DevRose.copy(alpha = 0.6f), DevRose.copy(alpha = 0.6f)))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Sign Out")
                            }
                        }
                    }
                }
            } else {
                // Auth Box: Sign In / Sign Up Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_form_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            colors = listOf(DarkCardBorder, DarkCardBorder.copy(alpha = 0.4f))
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Segmented Tab Row: Sign In vs Sign Up
                        TabRow(
                            selectedTabIndex = selectedAuthTab,
                            containerColor = Color(0xFF0B101C),
                            contentColor = DevBlueLight,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedAuthTab]),
                                    color = DevBlueLight
                                )
                            },
                            divider = {},
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .testTag("auth_tabs")
                        ) {
                            Tab(
                                selected = selectedAuthTab == 0,
                                onClick = {
                                    selectedAuthTab = 0
                                    localValidationMsg = null
                                    onDismissError()
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sign In", fontWeight = if (selectedAuthTab == 0) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            )
                            Tab(
                                selected = selectedAuthTab == 1,
                                onClick = {
                                    selectedAuthTab = 1
                                    localValidationMsg = null
                                    onDismissError()
                                },
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Create Account", fontWeight = if (selectedAuthTab == 1) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Error Banner (from Firebase or local validation)
                        val activeError = errorMessage ?: localValidationMsg
                        AnimatedVisibility(
                            visible = activeError != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            if (activeError != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 14.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DevRose.copy(alpha = 0.15f))
                                        .border(1.dp, DevRose.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = "Error",
                                            tint = DevRose,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = activeError,
                                            color = Color(0xFFFFB4AB),
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = {
                                                localValidationMsg = null
                                                onDismissError()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Dismiss",
                                                tint = Color(0xFFFFB4AB),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Email Field
                        Text(
                            text = "Email Address",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                localValidationMsg = null
                            },
                            placeholder = { Text("developer@example.com", color = Color(0xFF64748B), fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = DevBlueLight, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DevBlueLight,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Field
                        Text(
                            text = "Password",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                localValidationMsg = null
                            },
                            placeholder = { Text("••••••••", color = Color(0xFF64748B), fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = DevBlueLight, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (selectedAuthTab == 1) ImeAction.Next else ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = {
                                    focusManager.clearFocus()
                                    if (email.isBlank() || password.isBlank()) {
                                        localValidationMsg = "Please fill in all required fields"
                                    } else {
                                        onSignInEmail(email.trim(), password)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DevBlueLight,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Confirm Password (Sign Up Only)
                        if (selectedAuthTab == 1) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Confirm Password",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    localValidationMsg = null
                                },
                                placeholder = { Text("••••••••", color = Color(0xFF64748B), fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = DevIndigoLight, modifier = Modifier.size(18.dp))
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (email.isBlank() || password.isBlank()) {
                                            localValidationMsg = "Please fill in all required fields"
                                        } else if (password != confirmPassword) {
                                            localValidationMsg = "Passwords do not match"
                                        } else if (password.length < 6) {
                                            localValidationMsg = "Password must be at least 6 characters"
                                        } else {
                                            onSignUpEmail(email.trim(), password)
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_confirm_password_field"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DevIndigoLight,
                                    unfocusedBorderColor = DarkCardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF0F172A),
                                    unfocusedContainerColor = Color(0xFF0F172A)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (email.isBlank() || password.isBlank()) {
                                    localValidationMsg = "Please enter both email and password"
                                    return@Button
                                }
                                if (selectedAuthTab == 1) {
                                    if (password.length < 6) {
                                        localValidationMsg = "Password must be at least 6 characters"
                                        return@Button
                                    }
                                    if (password != confirmPassword) {
                                        localValidationMsg = "Passwords do not match"
                                        return@Button
                                    }
                                    onSignUpEmail(email.trim(), password)
                                } else {
                                    onSignInEmail(email.trim(), password)
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("login_submit_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedAuthTab == 0) DevBlueLight else DevIndigoLight,
                                contentColor = Color(0xFF040814)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF040814),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (selectedAuthTab == 0) "Sign In to Studio" else "Create Developer Account",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Divider
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DarkCardBorder)
                            Text(
                                text = "OR",
                                color = DarkTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = DarkCardBorder)
                        }

                        // Google Sign-In Button with Credential Manager
                        OutlinedButton(
                            onClick = {
                                localValidationMsg = null
                                onDismissError()
                                onSignInGoogle(context)
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("google_signin_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF131B2E),
                                contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = !isLoading).copy(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4285F4),
                                        Color(0xFF34A853),
                                        Color(0xFFFBBC05),
                                        Color(0xFFEA4335)
                                    )
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Google Credential Manager",
                                    tint = DevBlueLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Continue with Google (Credential Manager)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Guest Mode / Direct Workspace Access
                        OutlinedButton(
                            onClick = onContinueGuest,
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("guest_signin_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0x0AFFFFFF),
                                contentColor = Color(0xFF94A3B8)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = !isLoading).copy(
                                brush = Brush.linearGradient(
                                    listOf(DarkCardBorder, DarkCardBorder)
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Explore Offline / Guest Mode",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = DevEmeraldLight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Powered by Firebase Auth & Google Credential Manager",
                    color = DarkTextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
