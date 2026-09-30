package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ManifestaButton
import com.example.ui.components.ManifestaButtonStyle
import com.example.ui.components.ManifestaGlassCard
import com.example.ui.theme.ManifestaAccent
import com.example.ui.theme.ManifestaBackground
import com.example.ui.theme.ManifestaGlass
import com.example.ui.theme.ManifestaGlassBorder
import com.example.ui.theme.ManifestaGlassBorderActive
import com.example.ui.theme.ManifestaPrimary
import com.example.ui.theme.ManifestaSecondary
import com.example.ui.theme.ManifestaSecondaryBg
import com.example.ui.theme.ManifestaSecondaryText
import com.example.ui.theme.ManifestaSurface
import com.example.ui.theme.ManifestaText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copiedToClipboard by remember { mutableStateOf(false) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
            .padding(16.dp)
            .testTag("privacy_policy_dialog")
    ) {
        ManifestaGlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = ManifestaGlassBorderActive
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ManifestaPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PrivacyTip,
                                contentDescription = null,
                                tint = ManifestaAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Privacy Policy",
                                color = ManifestaText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Updated Sept 30, 2026 • Version 2.0",
                                color = ManifestaSecondaryText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close Privacy Policy",
                            tint = ManifestaSecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = ManifestaGlassBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick Summary Badge Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ManifestaSecondaryBg)
                            .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "YOUR SANCTUARY IS SAFE & PRIVATE",
                                color = ManifestaAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "We never sell, rent, or monetize your personal thoughts, goals, or affirmations. Your data belongs exclusively to you.",
                                color = ManifestaSecondaryText,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    PolicySectionCard(
                        icon = Icons.Outlined.Security,
                        title = "1. Information We Collect",
                        content = "• Account Data: Optional name, email address, and Firebase UID if you sign in.\n" +
                                "• Manifestation Content: Goals, affirmations, vision cards, and journal notes you create.\n" +
                                "• Preferences: Ambient audio selections, reminders, and voice settings stored locally on your device.\n" +
                                "• Diagnostics: Non-identifying crash and performance reports to ensure app stability."
                    )

                    PolicySectionCard(
                        icon = Icons.Outlined.CloudDone,
                        title = "2. Cloud Sync & AI Processing",
                        content = "• Firebase Authentication & Cloud Firestore: Encrypted cloud backup for multi-device sync, keyed strictly to your authenticated UID (users/{uid}).\n" +
                                "• Google Gemini AI: Powers personalized affirmation creation. Your prompts are processed securely and never used to train public models.\n" +
                                "• Zero Tracking: We do not use third-party advertising SDKs or data brokers."
                    )

                    PolicySectionCard(
                        icon = Icons.Outlined.Lock,
                        title = "3. Storage & Cryptographic Security",
                        content = "• On-device Room database protected by Android's sandbox security.\n" +
                                "• End-to-end TLS encryption for all network requests to Firebase and Google APIs.\n" +
                                "• Strict Firestore security rules (request.auth.uid == userId) ensuring no other user can access your data."
                    )

                    PolicySectionCard(
                        icon = Icons.Outlined.DeleteOutline,
                        title = "4. Your Data Rights & Deletion",
                        content = "• Full control to view, edit, or delete any affirmation or goal directly in the app.\n" +
                                "• You can request complete permanent deletion of your account and cloud data anytime by contacting ashutoshinkar@gmail.com.\n" +
                                "• Deleting or uninstalling the app purges all local cache immediately."
                    )

                    PolicySectionCard(
                        icon = Icons.Outlined.Mail,
                        title = "5. Contact & Inquiries",
                        content = "For privacy questions, data requests, or support, please email:\nashutoshinkar@gmail.com\nApplication: MANIFESTA (com.aistudio.manifesta.qwmzp)"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = ManifestaGlassBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ManifestaButton(
                        text = if (copiedToClipboard) "Copied!" else "Copy Email",
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Contact Email", "ashutoshinkar@gmail.com"))
                            copiedToClipboard = true
                        },
                        style = ManifestaButtonStyle.GLASS_OUTLINE,
                        modifier = Modifier.weight(1f),
                        testTag = "copy_privacy_contact_button"
                    )

                    ManifestaButton(
                        text = "I Understand",
                        onClick = onDismiss,
                        style = ManifestaButtonStyle.PRIMARY_GRADIENT,
                        modifier = Modifier.weight(1f),
                        testTag = "close_privacy_policy_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun PolicySectionCard(
    icon: ImageVector,
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ManifestaGlass)
            .border(1.dp, ManifestaGlassBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ManifestaAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    color = ManifestaText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = content,
                color = ManifestaSecondaryText,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}
