package com.denicrizz.chatnusa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

data class Message(val sender: String, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                Message("bot", "Hai, saya siap bantu kamu!"),
                Message("user", "carikan saya skripsi tentang TF-IDF"),
                Message(
                    "bot",
                    "Berikut adalah hasil yang mungkin bisa membantu anda:\n" +
                            "Analisis sentiment pengguna telegram menggunakan metode TF-IDF\n(link repository >)"
                ),
                Message("user", "Terimakasih!"),
                Message("bot", "Baik, senang bisa bantu kamu!")
            )
        )
    }

    val scrollState = rememberScrollState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text("Beranda") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Tentang Aplikasi") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Keluar") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                // Top App Bar dengan hamburger
                TopAppBar(
                    title = {
                        Text("ChatBot", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )

                // Chat messages
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .verticalScroll(scrollState)
                ) {
                    messages.forEach { message ->
                        ChatBubble(message)
                    }
                }

                // Auto-scroll ke bawah
                LaunchedEffect(messages.size) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }

                // Input area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tulis pertanyaanmu") },
                        maxLines = 2
                    )
                    IconButton(onClick = {
                        if (inputText.trim().isNotEmpty()) {
                            messages = messages + Message("user", inputText.trim())
                            inputText = ""
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Kirim")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.sender == "user"
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) Color(0xFFD1F5FF) else Color(0xFFEAEAEA)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = bubbleColor,
            shadowElevation = 2.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(10.dp),
                color = Color.Black
            )
        }
    }
}
