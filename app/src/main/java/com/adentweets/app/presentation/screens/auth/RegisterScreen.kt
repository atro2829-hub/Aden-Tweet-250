package com.adentweets.app.presentation.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.XButton
import com.adentweets.app.presentation.components.XTextField
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val isRegisterSuccess by viewModel.isRegisterSuccess.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isRegisterSuccess) {
        if (isRegisterSuccess) {
            kotlinx.coroutines.delay(1500)
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
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
                if (currentStep > 1) viewModel.previousStep()
                else navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع"
                )
            }
            Text(
                text = "إنشاء حساب",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Step Progress Dots
        StepProgressDots(currentStep = currentStep, totalSteps = 4)

        Spacer(modifier = Modifier.height(32.dp))

        // Step Content
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { if (targetState > initialState) it else -it },
                    animationSpec = tween(300)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { if (targetState > initialState) -it else it },
                    animationSpec = tween(300)
                )
            },
            label = "stepTransition"
        ) { step ->
            when (step) {
                1 -> Step1Content(viewModel = viewModel)
                2 -> Step2Content(viewModel = viewModel)
                3 -> Step3Content(viewModel = viewModel)
                4 -> Step4Success()
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation Buttons
        if (currentStep < 4) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                XButton(
                    text = if (currentStep < 3) "التالي" else "إنشاء حساب",
                    onClick = {
                        if (currentStep == 3) viewModel.register()
                        else viewModel.nextStep()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = true,
                    isLoading = isLoading && currentStep == 3
                )

                if (currentStep > 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    XButton(
                        text = "رجوع",
                        onClick = { viewModel.previousStep() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = true,
                        isLoading = false,
                        isOutlined = true
                    )
                }
            }
        }
    }
}

// --- Progress Dots ---
@Composable
private fun StepProgressDots(currentStep: Int, totalSteps: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 1..totalSteps) {
            val isSelected = i <= currentStep
            val isCurrent = i == currentStep
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (isCurrent) 32.dp else 10.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) AdenBlue
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}

// --- Step 1: Display Name ---
@Composable
private fun Step1Content(viewModel: RegisterViewModel) {
    val displayName by viewModel.displayName.collectAsState()
    val displayNameError by viewModel.displayNameError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "ما اسمك؟",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "أدخل اسمك الذي سيظهر على ملفك الشخصي",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        XTextField(
            value = displayName,
            onValueChange = viewModel::onDisplayNameChanged,
            placeholder = "الاسم الكامل",
            singleLine = true,
            isError = displayNameError != null,
            errorMessage = displayNameError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// --- Step 2: Username ---
@Composable
private fun Step2Content(viewModel: RegisterViewModel) {
    val username by viewModel.username.collectAsState()
    val usernameError by viewModel.usernameError.collectAsState()
    val isUsernameAvailable by viewModel.isUsernameAvailable.collectAsState()
    val isCheckingUsername by viewModel.isCheckingUsername.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "اختر اسم مستخدم",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "سيكون هذا رابط ملفك الشخصي: @username",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "@",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.size(8.dp))
            XTextField(
                value = username,
                onValueChange = viewModel::onUsernameChanged,
                placeholder = "اسم المستخدم",
                singleLine = true,
                isError = usernameError != null,
                errorMessage = usernameError,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        XButton(
            text = if (isCheckingUsername) "جارٍ التحقق..." else "التحقق من التوفر",
            onClick = { viewModel.checkUsernameAvailability() },
            modifier = Modifier.height(44.dp),
            enabled = username.isNotBlank() && !isCheckingUsername,
            isLoading = isCheckingUsername
        )

        if (isUsernameAvailable == true) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "اسم المستخدم متاح ✓",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// --- Step 3: Email + Password ---
@Composable
private fun Step3Content(viewModel: RegisterViewModel) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "أخيرًا، بيانات الحساب",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "أدخل بريدك الإلكتروني واختر كلمة مرور قوية",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        XTextField(
            value = email,
            onValueChange = viewModel::onEmailChanged,
            placeholder = "البريد الإلكتروني",
            singleLine = true,
            isError = emailError != null,
            errorMessage = emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        XTextField(
            value = password,
            onValueChange = viewModel::onPasswordChanged,
            placeholder = "كلمة المرور",
            isPassword = true,
            singleLine = true,
            isError = passwordError != null,
            errorMessage = passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        XTextField(
            value = confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChanged,
            placeholder = "تأكيد كلمة المرور",
            isPassword = true,
            singleLine = true,
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// --- Step 4: Success ---
@Composable
private fun Step4Success() {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600),
        label = "successScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AdenBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AdenBlue,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "حسابك جاهز!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "مرحبًا بك في عدن تويت",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}