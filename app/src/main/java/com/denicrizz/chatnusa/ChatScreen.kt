package com.denicrizz.chatnusa

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.text.SimpleDateFormat
import java.util.*

// ========== DATA CLASSES ==========

data class Message(val sender: String, val text: String, val timestamp: Long = System.currentTimeMillis())
data class ChatRequest(val message: String)
data class ChatResponse(val type: String, val results: List<ResearchPaper>?, val jawaban: String?)
data class ResearchPaper(val title: String, val link: String, val score: Double)

// ========== RETROFIT ==========

interface ChatApiService {
    @POST("chat/")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://sncnjktofsia.ap-southeast-1.clawcloudrun.com/api/"
    val api: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}

// ========== CHAT HISTORY MANAGER ==========

object ChatHistoryManager {
    private const val PREF_NAME = "chat_history_prefs"
    private const val KEY_MESSAGES = "messages"

    fun saveHistory(context: Context, messages: List<Message>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(messages)
        prefs.edit().putString(KEY_MESSAGES, json).apply()
    }

    fun loadHistory(context: Context): List<Message> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_MESSAGES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Message>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearHistory(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_MESSAGES).apply()
    }

    fun exportHistory(messages: List<Message>): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return messages.joinToString("\n\n") {
            val sender = if (it.sender == "user") "Saya" else "ChatNusa"
            val date = dateFormat.format(Date(it.timestamp))
            "[$date] $sender: ${it.text}"
        }
    }
}

// ========== CHAT BUBBLE UI ==========

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.sender == "user"
    val bubbleColor = if (isUser) Color(0xFF0050AC) else Color(0xFFEAEAEA)
    val textColor = if (isUser) Color.White else Color.Black

    val userShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 16.dp)
    val botShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 0.dp)
    val shape = if (isUser) userShape else botShape

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Bot",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(shape = shape, color = bubbleColor, shadowElevation = 2.dp) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

// ========== CHAT SCREEN UI ==========

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var inputText by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            ChatHistoryManager.loadHistory(context).ifEmpty {
                listOf(Message("bot", "Hai, saya ChatNusa siap bantu kamu!"))
            }
        )
    }

    // Save history on message update
    LaunchedEffect(messages) {
        ChatHistoryManager.saveHistory(context, messages)
    }

    fun clearHistory() {
        messages = listOf(Message("bot", "Hai, saya siap bantu kamu!"))
        ChatHistoryManager.clearHistory(context)
    }

    fun exportChat() {
        Toast.makeText(context, "Riwayat chat disimpan", Toast.LENGTH_SHORT).show()
    }

    MaterialTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Menu", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        NavigationDrawerItem(label = { Text("Beranda") }, selected = false, onClick = {})
                        NavigationDrawerItem(label = { Text("Tentang Aplikasi") }, selected = false, onClick = {})
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        Button(onClick = {
                            clearHistory()
                            Toast.makeText(context, "Riwayat chat dihapus", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Hapus Riwayat")
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("ChatNusa") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = "Lainnya")
                                }
                                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                    DropdownMenuItem(
                                        text = { Text("Simpan Riwayat") },
                                        onClick = {
                                            exportChat()
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    )
                },
             content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .imePadding(),
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = {
                                Text(
                                    text = "Tulis pertanyaanmu",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            maxLines = 2,
                            shape = RoundedCornerShape(12.dp), // opsional: buat sudut lebih halus
                            singleLine = false // jika ingin multiline
                        )
                        IconButton(onClick = {
                            if (inputText.isNotBlank()) {
                                val userText = inputText.trim()
                                messages = messages + Message("user", userText)
                                inputText = ""
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.api.sendMessage(ChatRequest(userText))
                                        val reply = when (response.type) {
                                            "repository" -> response.results?.joinToString("\n") {
                                                "- ${it.title}\nLink: ${it.link}\nSkor: ${it.score}"
                                            } ?: "Tidak ada hasil relevan."
                                            "info_UNP" -> response.jawaban ?: "Tidak ada jawaban tersedia."
                                            else -> "Jenis respons tidak dikenali."
                                        }
                                        messages = messages + Message("bot", reply)
                                    } catch (e: Exception) {
                                        messages = messages + Message("bot", "Terjadi kesalahan: ${e.localizedMessage}")
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Send, contentDescription = "Kirim")
                        }
                    }
                }
             }
            )
        }
    }
}


