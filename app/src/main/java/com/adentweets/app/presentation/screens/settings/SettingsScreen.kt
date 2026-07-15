package com.adentweets.app.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Blocked
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.theme.AdenBlue
import com.adentweets.app.presentation.theme.ErrorRed

@Composable
fun SettingsScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("تسجيل الخروج", fontWeight = FontWeight.Bold) },
            text = { Text("هل تريد تسجيل الخروج من حسابك؟") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text("تسجيل الخروج", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف الحساب", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف حسابك؟ لا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    // Handle account deletion
                }) {
                    Text("حذف", color = ErrorRed, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // حسابك
            item {
                SettingsGroupHeader(label = "حسابك")
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Person,
                    label = "الملف الشخصي",
                    onClick = { navController.navigate(Screen.EditProfile.route) }
                )
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Key,
                    label = "تغيير كلمة المرور",
                    onClick = { navController.navigate(Screen.AccountSettings.route) }
                )
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Mail,
                    label = "البريد الإلكتروني",
                    onClick = { navController.navigate(Screen.AccountSettings.route) }
                )
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Phone,
                    label = "رقم الهاتف",
                    onClick = { navController.navigate(Screen.AccountSettings.route) }
                )
            }

            // الخصوصية والأمان
            item {
                HorizontalDivider()
                SettingsGroupHeader(label = "الخصوصية والأمان")
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Lock,
                    label = "الخصوصية",
                    onClick = { navController.navigate(Screen.PrivacySettings.route) }
                )
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Security,
                    label = "الأمان",
                    onClick = { navController.navigate(Screen.SecuritySettings.route) }
                )
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Blocked,
                    label = "الحسابات المحظورة",
                    onClick = { navController.navigate(Screen.BlockedAccounts.route) }
                )
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.VolumeOff,
                    label = "الحسابات المكتومة",
                    onClick = { navController.navigate(Screen.MutedAccounts.route) }
                )
            }

            // الإشعارات
            item {
                HorizontalDivider()
                SettingsGroupHeader(label = "الإشعارات")
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Report,
                    label = "تفضيلات الإشعارات",
                    onClick = { navController.navigate(Screen.NotificationSettings.route) }
                )
            }

            // المظهر
            item {
                HorizontalDivider()
                SettingsGroupHeader(label = "المظهر")
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Palette,
                    label = "المظهر والسطوع",
                    onClick = { navController.navigate(Screen.AppearanceSettings.route) }
                )
            }

            // عام
            item {
                HorizontalDivider()
                SettingsGroupHeader(label = "عام")
            }
            item {
                SettingsItemRowWithTrailing(
                    icon = Icons.Default.Translate,
                    label = "اللغة",
                    trailingText = "العربية",
                    onClick = { }
                )
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Palette,
                    label = "حجم الخط",
                    onClick = { navController.navigate(Screen.AppearanceSettings.route) }
                )
            }

            // الدعم
            item {
                HorizontalDivider()
                SettingsGroupHeader(label = "الدعم")
            }
            item {
                SettingsItemRow(
                    icon = Icons.Default.Report,
                    label = "الإبلاغ عن مشكلة",
                    onClick = { navController.navigate(Screen.Report.route) }
                )
            }

            // تسجيل الخروج
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLogoutDialog = true }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تسجيل الخروج",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ErrorRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // حذف الحساب
            item {
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDeleteDialog = true }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "حذف الحساب",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ErrorRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsGroupHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = AdenBlue,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsItemRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsItemRowWithTrailing(
    icon: ImageVector,
    label: String,
    trailingText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = trailingText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}