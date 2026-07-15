package com.adentweets.app.presentation.screens.settings

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.navigation.Screen

@Composable
fun AccountSettingsScreen(navController: NavController) {
    var showEmailDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            title = {
                Text(
                    "تغيير البريد الإلكتروني",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("البريد الإلكتروني الجديد") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.adentweets.app.presentation.theme.AdenBlue,
                        focusedLabelColor = com.adentweets.app.presentation.theme.AdenBlue
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showEmailDialog = false },
                    enabled = emailInput.isNotBlank()
                ) {
                    Text("حفظ", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmailDialog = false }) {
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
                        text = "إعدادات الحساب",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // معلومات الحساب
            AccountInfoSection(
                label = "اسم العرض",
                value = "المستخدم",
                trailingIcon = Icons.Default.Edit,
                onClick = { navController.navigate(Screen.EditProfile.route) }
            )

            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))

            AccountInfoSection(
                label = "اسم المستخدم",
                value = "@username",
                trailingIcon = Icons.Default.Edit,
                onClick = { navController.navigate(Screen.EditProfile.route) }
            )

            HorizontalDivider()

            // البريد الإلكتروني
            SettingsGroupHeader(label = "البريد الإلكتروني")
            AccountInfoSection(
                label = "البريد الحالي",
                value = "u***@gmail.com",
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                onClick = { showEmailDialog = true }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                XButton(
                    text = "تغيير البريد الإلكتروني",
                    onClick = { showEmailDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    isSmall = true
                )
            }

            HorizontalDivider()

            // رقم الهاتف
            SettingsGroupHeader(label = "رقم الهاتف")
            AccountInfoSection(
                label = "الرقم الحالي",
                value = "+967 *** *** 789",
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                onClick = { navController.navigate(Screen.PhoneVerify.route) }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                XButton(
                    text = "تغيير رقم الهاتف",
                    onClick = { navController.navigate(Screen.PhoneVerify.route) },
                    modifier = Modifier.fillMaxWidth(),
                    isSmall = true
                )
            }

            HorizontalDivider()

            // تغيير كلمة المرور
            SettingsGroupHeader(label = "تغيير كلمة المرور")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("كلمة المرور الحالية") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.adentweets.app.presentation.theme.AdenBlue,
                        focusedLabelColor = com.adentweets.app.presentation.theme.AdenBlue
                    )
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("كلمة المرور الجديدة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.adentweets.app.presentation.theme.AdenBlue,
                        focusedLabelColor = com.adentweets.app.presentation.theme.AdenBlue
                    )
                )
                OutlinedTextField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    label = { Text("تأكيد كلمة المرور الجديدة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.adentweets.app.presentation.theme.AdenBlue,
                        focusedLabelColor = com.adentweets.app.presentation.theme.AdenBlue
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                XButton(
                    text = "تحديث كلمة المرور",
                    onClick = { /* Handle password change */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = currentPassword.isNotBlank() &&
                            newPassword.isNotBlank() &&
                            confirmNewPassword.isNotBlank() &&
                            newPassword == confirmNewPassword
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AccountInfoSection(
    label: String,
    value: String,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector,
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsGroupHeader(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = com.adentweets.app.presentation.theme.AdenBlue,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}