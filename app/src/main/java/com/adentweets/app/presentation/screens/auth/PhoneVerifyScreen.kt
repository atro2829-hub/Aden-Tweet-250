package com.adentweets.app.presentation.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.XButton
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.theme.AdenBlue

private val countryCodes = listOf(
    "+967" to "اليمن 🇾🇪",
    "+966" to "السعودية 🇸🇦",
    "+971" to "الإمارات 🇦🇪",
    "+968" to "عُمان 🇴🇲",
    "+974" to "قطر 🇶🇦",
    "+965" to "الكويت 🇰🇼",
    "+973" to "البحرين 🇧🇭",
    "+20" to "مصر 🇪🇬",
    "+212" to "المغرب 🇲🇦",
    "+216" to "تونس 🇹🇳",
    "+213" to "الجزائر 🇩🇿",
    "+1" to "أمريكا 🇺🇸"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerifyScreen(
    navController: NavController,
    viewModel: PhoneVerifyViewModel = hiltViewModel()
) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val countryCode by viewModel.countryCode.collectAsState()
    val phoneError by viewModel.phoneError.collectAsState()
    val isCodeSent by viewModel.isCodeSent.collectAsState()
    val otp by viewModel.otp.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val countdownSeconds by viewModel.countdownSeconds.collectAsState()
    val isVerifySuccess by viewModel.isVerifySuccess.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isVerifySuccess) {
        if (isVerifySuccess) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.PhoneVerify.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage!!)
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 16.dp)
        ) {
            IconButton(onClick = {
                if (isCodeSent) {
                    // Reset to phone entry
                } else {
                    navController.popBackStack()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع"
                )
            }
            Text(
                text = "تسجيل الدخول بالهاتف",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(
            targetState = isCodeSent,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            },
            label = "phoneVerifyState"
        ) { codeSent ->
            if (codeSent) {
                // OTP Entry
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "أدخل رمز التحقق",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "تم إرسال رمز مكون من ٦ أرقام إلى $countryCode $phoneNumber",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // OTP Boxes
                    OtpInputFields(
                        otp = otp,
                        onOtpChanged = viewModel::onOtpChanged,
                        isError = otpError != null
                    )

                    if (otpError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = otpError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Verify Button
                    XButton(
                        text = "تأكيد",
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.verifyOtp()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = otp.all { it.isNotBlank() },
                        isLoading = isLoading
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resend
                    if (countdownSeconds > 0) {
                        Text(
                            text = "إعادة الإرسال بعد ${countdownSeconds} ثانية",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "لم تستلم الرمز؟",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "إعادة إرسال الرمز",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AdenBlue,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline,
                            onClick = { viewModel.resendOtp() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "تغيير رقم الهاتف",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AdenBlue,
                        fontWeight = FontWeight.Medium,
                        textDecoration = TextDecoration.Underline,
                        onClick = {
                            // Go back to phone entry by resetting state
                        }
                    )
                }
            } else {
                // Phone Entry
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "أدخل رقم هاتفك",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "سنرسل لك رمز تحقق عبر الرسائل القصيرة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // Country Code Dropdown
                    var expanded by remember { mutableStateOf(false) }
                    val selectedLabel = countryCodes.find { it.first == countryCode }?.second ?: countryCode

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("رمز الدولة") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            countryCodes.forEach { (code, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        viewModel.onCountryCodeChanged(code)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = viewModel::onPhoneNumberChanged,
                        placeholder = { Text("رقم الهاتف") },
                        singleLine = true,
                        isError = phoneError != null,
                        supportingText = if (phoneError != null) {{ Text(phoneError!!) }} else null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AdenBlue,
                            focusedLabelColor = AdenBlue,
                            cursorColor = AdenBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Send OTP Button
                    XButton(
                        text = "إرسال رمز التحقق",
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.sendOtp()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = phoneNumber.length >= 9,
                        isLoading = isLoading
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

// --- OTP Input Fields ---
@Composable
private fun OtpInputFields(
    otp: List<String>,
    onOtpChanged: (Int, String) -> Unit,
    isError: Boolean
) {
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        otp.forEachIndexed { index, digit ->
            val borderColor = if (isError) {
                MaterialTheme.colorScheme.error
            } else if (digit.isNotBlank()) {
                AdenBlue
            } else {
                MaterialTheme.colorScheme.outline
            }

            BasicTextField(
                value = digit,
                onValueChange = { value ->
                    if (value.length <= 1) {
                        onOtpChanged(index, value)
                        // Auto-advance to next field
                        if (value.isNotBlank() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .size(width = 48.dp, height = 56.dp)
                    .border(
                        width = 2.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .focusRequester(focusRequesters[index]),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(AdenBlue),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == 5) ImeAction.Done else ImeAction.Next
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (digit.isBlank()) {
                            Text(
                                text = "•",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                fontSize = 24.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}