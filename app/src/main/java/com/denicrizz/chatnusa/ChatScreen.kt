package com.denicrizz.chatnusa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// --- Retrofit API Setup ---

data class Message(val sender: String, val text: String)
data class ChatRequest(val message: String)
data class ChatResponse(val reply: String)

interface ChatApiService {
    @POST("chat") // Ganti dengan endpoint API kamu
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://your-api-url.com/" // Ganti dengan base URL API kamu

    val api: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }
    var isDarkTheme by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var messages by remember {
        mutableStateOf(
            listOf(
                Message("bot", "Hai, saya Chat Nusa siap bantu kamu!")
            )
        )
    }

    fun clearChatHistory() {
        messages = listOf(Message("bot", "Hai, saya siap bantu kamu!"))
    }

    val scrollState = rememberScrollState()

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Menu",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        NavigationDrawerItem(
                            label = { Text("Beranda") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Gemini") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Tentang Aplikasi") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } }
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = if (isDarkTheme) "Mode Gelap" else "Mode Terang")
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { isDarkTheme = it }
                            )
                        }
                    }
                }
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    TopAppBar(
                        title = {
                            Text("ChatNusa", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                clearChatHistory()
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Hapus Riwayat Chat")
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
                                val userMessage = inputText.trim()
                                messages = messages + Message("user", userMessage)
                                inputText = ""

                                coroutineScope.launch {
                                    try {
                                        val response = RetrofitClient.api.sendMessage(ChatRequest(userMessage))
                                        messages = messages + Message("bot", response.reply)
                                    } catch (e: Exception) {
                                        messages = messages + Message("bot", "Gagal mengambil jawaban: ${e.localizedMessage}")
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Send, contentDescription = "Kirim")
                        }
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
