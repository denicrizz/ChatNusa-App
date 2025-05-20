package com.denicrizz.chatnusa.screen
import com.denicrizz.chatnusa.api.RetrofitClient
import com.denicrizz.chatnusa.model.Message
import com.denicrizz.chatnusa.model.ChatRequest
import com.denicrizz.chatnusa.utils.ChatHistoryManager
import com.denicrizz.chatnusa.model.ChatBubble
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


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