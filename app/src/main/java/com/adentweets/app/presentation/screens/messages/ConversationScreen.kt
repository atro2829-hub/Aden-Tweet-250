package com.adentweets.app.presentation.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.domain.model.Message
import com.adentweets.app.domain.model.MediaType
import com.adentweets.app.presentation.components.common.Base64ImageView
import com.adentweets.app.presentation.screens.messages.viewmodel.ConversationViewModel
import com.adentweets.app.presentation.theme.AdenBlue
import com.adentweets.app.presentation.theme.DarkInputBg
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    navController: NavController,
    conversationId: String,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val currentUid = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "المحادثة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "متصل الآن",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "المزيد")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            MessageInputBar(
                messageText = messageText,
                onMessageTextChanged = { viewModel.updateMessageText(it) },
                onSend = { viewModel.sendMessage(conversationId) },
                onAttachImage = { /* Pick image */ }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = listState,
            reverseLayout = true,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages, key = { it.messageId }) { message ->
                MessageBubble(
                    message = message,
                    isFromCurrentUser = message.senderId == currentUid
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isFromCurrentUser: Boolean
) {
    val isImageMessage = message.mediaBase64 != null &&
            (message.mediaType == MediaType.IMAGE || message.mediaType == MediaType.GIF)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp
                ),
                color = if (isFromCurrentUser) AdenBlue
                else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Image content
                    if (isImageMessage) {
                        message.mediaBase64?.let { base64 ->
                            Base64ImageView(
                                base64 = base64,
                                modifier = Modifier.size(200.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.FillWidth
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    // Text content
                    message.content?.let { text ->
                        if (text.isNotBlank()) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isFromCurrentUser) Color.White
                                else MaterialTheme.colorScheme.onSurface,
                                textAlign = if (isFromCurrentUser) TextAlign.End else TextAlign.Start
                            )
                        }
                    }

                    // Time and read receipt
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatMessageTime(message.sentAt),
                            fontSize = 11.sp,
                            color = if (isFromCurrentUser) Color.White.copy(alpha = 0.7f)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (isFromCurrentUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (message.readBy.size >= 2) Color.White
                                else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageInputBar(
    messageText: String,
    onMessageTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onAttachImage: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .imePadding(),
            verticalAlignment = Alignment.Bottom
        ) {
            // Attach image button
            IconButton(onClick = onAttachImage) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "إرفاق صورة",
                    tint = AdenBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Text field
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChanged,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp, max = 120.dp),
                placeholder = {
                    Text(
                        text = "اكتب رسالة...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = AdenBlue,
                    cursorColor = AdenBlue
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send button
            IconButton(
                onClick = onSend,
                enabled = messageText.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "إرسال",
                    tint = if (messageText.isNotBlank()) AdenBlue
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun formatMessageTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / 60_000
    val hours = minutes / 60

    val javaTimestamp = java.text.SimpleDateFormat("HH:mm", java.util.Locale("ar"))
        .format(java.util.Date(timestamp))

    return when {
        minutes < 1 -> "الآن"
        hours < 24 -> javaTimestamp
        else -> {
            val dayFormat = java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale("ar"))
            dayFormat.format(java.util.Date(timestamp))
        }
    }
}