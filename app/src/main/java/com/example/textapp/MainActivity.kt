package com.example.textapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.textapp.ui.theme.TextAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TextAppTheme {
                ChatsScreen()
            }
        }
    }
}

data class ChatPreview(
    val name: String,
    val initials: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int
)

data class ChatMessage(
    val sender: String,
    val text: String,
    val fromMe: Boolean
)

private val sampleChats = listOf(
    ChatPreview("Amina", "AM", "Are we still meeting today?", "9:42", 2),
    ChatPreview("Daniel", "DK", "Thanks, I received the file.", "Yesterday", 0),
    ChatPreview("Mum", "M", "Call me when you are free.", "Yesterday", 1),
    ChatPreview("Study Group", "SG", "Brian: I added the notes.", "Monday", 0)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatsScreen() {
    var selectedChat by remember {
        mutableStateOf<ChatPreview?>(null)
    }

    val conversations = remember {
        mutableStateMapOf<String, SnapshotStateList<ChatMessage>>().apply {
            sampleChats.forEach { chat ->
                put(
                    chat.name,
                    mutableStateListOf(
                        ChatMessage(chat.name, chat.lastMessage, false),
                        ChatMessage("You", "Thanks for your message.", true)
                    )
                )
            }
        }
    }

    val openChat = selectedChat

    if (openChat == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Pulse",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(sampleChats) { chat ->
                    ChatRow(
                        chat = chat,
                        onClick = { selectedChat = chat }
                    )
                    HorizontalDivider()
                }
            }
        }
    } else {
        ConversationScreen(
            chat = openChat,
            messages = conversations.getValue(openChat.name),
            onBack = { selectedChat = null }
        )
    }
}

@Composable
private fun ChatRow(
    chat: ChatPreview,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = chat.initials,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = chat.name,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = chat.lastMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = chat.time,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            if (chat.unreadCount > 0) {
                Text(
                    text = "${chat.unreadCount} unread",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationScreen(
    chat: ChatPreview,
    messages: SnapshotStateList<ChatMessage>,
    onBack: () -> Unit
) {
    var draft by rememberSaveable(chat.name) {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back to chats"
                        )
                    }
                },
                title = {
                    Column {
                        Text(text = chat.name)
                        Text(
                            text = "online",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    enabled = draft.isNotBlank(),
                    onClick = {
                        val messageText = draft.trim()

                        if (messageText.isNotEmpty()) {
                            messages.add(
                                ChatMessage(
                                    sender = "You",
                                    text = messageText,
                                    fromMe = true
                                )
                            )
                            draft = ""
                        }
                    }
                ) {
                    Text("Send")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromMe) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (message.fromMe) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.sender,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = message.text)
            }
        }
    }
}