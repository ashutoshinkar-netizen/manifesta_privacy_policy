package com.example.ui.dialogs

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AffirmationEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.components.ManifestaLogoIcon
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaGlassBorderActive
import com.example.ui.theme.ManifestaMuted
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryBg
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText

private enum class AuthTab {
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD
}

/**
 * Section 9: Authentication & User Profiles
 * Handles: Email/password signup & login, Google Sign-In, Forgot/reset password, Persistent sessions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModalDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    var currentTab by remember { mutableStateOf(AuthTab.LOGIN) }

    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Forgot / Reset Password state
    var resetCodeInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var resetCodeDispatched by remember { mutableStateOf<String?>(null) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("auth_modal_dialog")
    ) {
        ManifestaGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            backgroundColor = ManifestaSecondaryBg,
            borderColor = ManifestaGlassBorder
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = ManifestaSecondaryText)
                    }
                }

                ManifestaLogoIcon(size = 44.dp, animated = true)

                Text(
                    text = when (currentTab) {
                        AuthTab.LOGIN -> "Welcome to Your Sanctuary"
                        AuthTab.SIGNUP -> "Begin Your Journey"
                        AuthTab.FORGOT_PASSWORD -> "Account Recovery"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = ManifestaText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = when (currentTab) {
                        AuthTab.LOGIN -> "Log in to sync your affirmations, streaks, and audio rituals securely."
                        AuthTab.SIGNUP -> "Create a private account to isolate your manifestation data."
                        AuthTab.FORGOT_PASSWORD -> "Enter your email to receive a secure recovery code."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = ManifestaSecondaryText,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                // Tabs Switcher (Login vs Sign Up)
                if (currentTab != AuthTab.FORGOT_PASSWORD) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ManifestaGlass)
                            .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (currentTab == AuthTab.LOGIN) ManifestaPrimary.copy(alpha = 0.35f) else Color.Transparent)
                                .clickable {
                                    currentTab = AuthTab.LOGIN
                                    errorMessage = null
                                    successMessage = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Log In",
                                color = if (currentTab == AuthTab.LOGIN) ManifestaText else ManifestaSecondaryText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (currentTab == AuthTab.SIGNUP) ManifestaPrimary.copy(alpha = 0.35f) else Color.Transparent)
                                .clickable {
                                    currentTab = AuthTab.SIGNUP
                                    errorMessage = null
                                    successMessage = null
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign Up",
                                color = if (currentTab == AuthTab.SIGNUP) ManifestaText else ManifestaSecondaryText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Error / Success Banner
                errorMessage?.let { err ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE53935).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFFE53935).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = err,
                            color = Color(0xFFFF8A80),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                successMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = msg,
                            color = Color(0xFFA5D6A7),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // FORM INPUTS
                when (currentTab) {
                    AuthTab.SIGNUP -> {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            placeholder = { Text("Your Full Name", color = ManifestaMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = ManifestaSecondaryText) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManifestaPrimary,
                                unfocusedBorderColor = ManifestaGlassBorder,
                                focusedTextColor = ManifestaText,
                                unfocusedTextColor = ManifestaText
                            )
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            placeholder = { Text("Email Address", color = ManifestaMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = ManifestaSecondaryText) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManifestaPrimary,
                                unfocusedBorderColor = ManifestaGlassBorder,
                                focusedTextColor = ManifestaText,
                                unfocusedTextColor = ManifestaText
                            )
                        )

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            placeholder = { Text("Password (min 6 characters)", color = ManifestaMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = ManifestaSecondaryText) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = "Toggle password",
                                        tint = ManifestaSecondaryText
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManifestaPrimary,
                                unfocusedBorderColor = ManifestaGlassBorder,
                                focusedTextColor = ManifestaText,
                                unfocusedTextColor = ManifestaText
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "By signing up, you agree to our ",
                                color = ManifestaSecondaryText,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Privacy Policy",
                                color = ManifestaAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    viewModel.dismissAuthModal()
                                    viewModel.openPrivacyPolicy()
                                }
                            )
                        }

                        ManifestaButton(
                            text = if (isLoading) "Creating Account..." else "Create Free Account",
                            onClick = {
                                if (emailInput.isBlank() || passwordInput.isBlank()) {
                                    errorMessage = "Please enter both email and password"
                                    return@ManifestaButton
                                }
                                isLoading = true
                                errorMessage = null
                                viewModel.signUpWithEmail(nameInput, emailInput, passwordInput) { success, msg ->
                                    isLoading = false
                                    if (!success) {
                                        errorMessage = msg
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "signup_submit_button"
                        )
                    }

                    AuthTab.LOGIN -> {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            placeholder = { Text("Email Address", color = ManifestaMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = ManifestaSecondaryText) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManifestaPrimary,
                                unfocusedBorderColor = ManifestaGlassBorder,
                                focusedTextColor = ManifestaText,
                                unfocusedTextColor = ManifestaText
                            )
                        )

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it },
                            placeholder = { Text("Password", color = ManifestaMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = ManifestaSecondaryText) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = "Toggle password",
                                        tint = ManifestaSecondaryText
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManifestaPrimary,
                                unfocusedBorderColor = ManifestaGlassBorder,
                                focusedTextColor = ManifestaText,
                                unfocusedTextColor = ManifestaText
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Forgot password?",
                                color = ManifestaSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable {
                                        currentTab = AuthTab.FORGOT_PASSWORD
                                        errorMessage = null
                                        successMessage = null
                                    }
                                    .padding(vertical = 4.dp)
                            )
                        }

                        ManifestaButton(
                            text = if (isLoading) "Signing In..." else "Log In",
                            onClick = {
                                if (emailInput.isBlank() || passwordInput.isBlank()) {
                                    errorMessage = "Please enter your email and password"
                                    return@ManifestaButton
                                }
                                isLoading = true
                                errorMessage = null
                                viewModel.loginWithEmail(emailInput, passwordInput) { success, msg ->
                                    isLoading = false
                                    if (!success) {
                                        errorMessage = msg
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "login_submit_button"
                        )
                    }

                    AuthTab.FORGOT_PASSWORD -> {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            placeholder = { Text("Registered Email Address", color = ManifestaMuted, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = ManifestaSecondaryText) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManifestaPrimary,
                                unfocusedBorderColor = ManifestaGlassBorder,
                                focusedTextColor = ManifestaText,
                                unfocusedTextColor = ManifestaText
                            )
                        )

                        if (resetCodeDispatched == null) {
                            ManifestaButton(
                                text = if (isLoading) "Sending Code..." else "Send Recovery Code",
                                onClick = {
                                    if (emailInput.isBlank()) {
                                        errorMessage = "Please enter your registered email"
                                        return@ManifestaButton
                                    }
                                    isLoading = true
                                    errorMessage = null
                                    viewModel.forgotPassword(emailInput) { success, codeOrErr ->
                                        isLoading = false
                                        if (success) {
                                            resetCodeDispatched = codeOrErr
                                            successMessage = "Verification code generated: $codeOrErr"
                                        } else {
                                            errorMessage = codeOrErr
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "send_reset_code_button"
                            )
                        } else {
                            OutlinedTextField(
                                value = resetCodeInput,
                                onValueChange = { resetCodeInput = it },
                                placeholder = { Text("6-Digit Reset Code", color = ManifestaMuted, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = ManifestaSecondaryText) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManifestaPrimary,
                                    unfocusedBorderColor = ManifestaGlassBorder,
                                    focusedTextColor = ManifestaText,
                                    unfocusedTextColor = ManifestaText
                                )
                            )

                            OutlinedTextField(
                                value = newPasswordInput,
                                onValueChange = { newPasswordInput = it },
                                placeholder = { Text("New Password (min 6 chars)", color = ManifestaMuted, fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = ManifestaSecondaryText) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManifestaPrimary,
                                    unfocusedBorderColor = ManifestaGlassBorder,
                                    focusedTextColor = ManifestaText,
                                    unfocusedTextColor = ManifestaText
                                )
                            )

                            ManifestaButton(
                                text = if (isLoading) "Updating Password..." else "Reset & Update Password",
                                onClick = {
                                    if (resetCodeInput.isBlank() || newPasswordInput.isBlank()) {
                                        errorMessage = "Please enter the reset code and your new password"
                                        return@ManifestaButton
                                    }
                                    isLoading = true
                                    errorMessage = null
                                    viewModel.resetPassword(emailInput, resetCodeInput, newPasswordInput) { success, msg ->
                                        isLoading = false
                                        if (success) {
                                            currentTab = AuthTab.LOGIN
                                            successMessage = "Password updated! You can now log in."
                                            resetCodeDispatched = null
                                        } else {
                                            errorMessage = msg
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "confirm_reset_password_button"
                            )
                        }

                        TextButton(
                            onClick = {
                                currentTab = AuthTab.LOGIN
                                errorMessage = null
                                successMessage = null
                            }
                        ) {
                            Text("Back to Log In", color = ManifestaSecondaryText, fontSize = 12.sp)
                        }
                    }
                }

                // Google Sign In (on Login & Signup views)
                if (currentTab != AuthTab.FORGOT_PASSWORD) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(ManifestaGlassBorder))
                        Text(
                            text = "OR",
                            color = ManifestaMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Box(modifier = Modifier.weight(1f).height(1.dp).background(ManifestaGlassBorder))
                    }

                    // Google One-Tap Sign In
                    ManifestaGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.signInWithGoogle("Aria Chen", "aria.manifesta@gmail.com") { _, _ -> }
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "G",
                                color = ManifestaSecondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Continue with Google",
                                color = ManifestaText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Continue as Guest",
                        color = ManifestaSecondaryText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * Section 9: Guest Mode Prompt
 * “Save your manifestation ✨”
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestSavePromptDialog(
    onDismiss: () -> Unit,
    onOpenAuth: () -> Unit,
    onSaveLocally: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("guest_save_prompt_dialog")
    ) {
        ManifestaGlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = ManifestaSecondaryBg,
            borderColor = ManifestaPrimary.copy(alpha = 0.4f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(ManifestaPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = ManifestaSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "Save your manifestation ✨",
                    style = MaterialTheme.typography.titleLarge,
                    color = ManifestaText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Create a free account so your affirmations, audio sessions, and streaks follow you everywhere.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ManifestaSecondaryText,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                ManifestaButton(
                    text = "Create Free Account",
                    onClick = onOpenAuth,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "create_account_from_prompt"
                )

                ManifestaButton(
                    text = "Keep in this device only",
                    onClick = onSaveLocally,
                    style = ManifestaButtonStyle.GLASS_OUTLINE,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "save_locally_as_guest"
                )
            }
        }
    }
}

/**
 * Section 17: SHARE CARDS
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareCardDialog(
    affirmation: AffirmationEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf("Instagram Story") }
    val formats = listOf("Instagram Story", "Instagram Post", "WhatsApp")

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("share_card_dialog")
    ) {
        ManifestaGlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = ManifestaSecondaryBg,
            borderColor = ManifestaGlassBorder
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share Affirmation",
                        style = MaterialTheme.typography.titleMedium,
                        color = ManifestaText,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = ManifestaSecondaryText)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    formats.forEach { fmt ->
                        val isSelected = selectedFormat == fmt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) ManifestaGlass else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) ManifestaGlassBorderActive else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedFormat = fmt }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = fmt,
                                color = if (isSelected) ManifestaText else ManifestaSecondaryText,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                val cardHeight = if (selectedFormat == "Instagram Story") 280.dp else 220.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(cardHeight)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ManifestaBackground,
                                    ManifestaSurface,
                                    ManifestaBackground
                                )
                            )
                        )
                        .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .align(Alignment.Center)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        ManifestaPrimary.copy(alpha = 0.25f),
                                        ManifestaSecondary.copy(alpha = 0.10f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ManifestaLogoIcon(size = 32.dp, animated = false)

                        Text(
                            text = "“${affirmation.primaryText}”",
                            color = ManifestaText,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "MANIFESTA",
                                color = ManifestaSecondaryText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "manifesta.app",
                                color = ManifestaMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                ManifestaButton(
                    text = "Share to $selectedFormat",
                    onClick = {
                        shareAffirmationIntent(context, affirmation.primaryText)
                        onDismiss()
                    },
                    icon = Icons.Outlined.Share,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "share_intent_button"
                )
            }
        }
    }
}

private fun shareAffirmationIntent(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "“$text”\n\n✨ Manifested with MANIFESTA\nThink it. Feel it. Become it.\nhttps://manifesta.app"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Affirmation")
    context.startActivity(shareIntent)
}
