package com.denicrizz.chatnusa.screen
import com.denicrizz.chatnusa.api.RetrofitClient
import com.denicrizz.chatnusa.model.Message
import com.denicrizz.chatnusa.model.ChatRequest
import com.denicrizz.chatnusa.utils.ChatHistoryManager
import com.denicrizz.chatnusa.model.ChatBubble
import com.denicrizz.chatnusa.utils.ThemePreference
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.denicrizz.chatnusa.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }  // FocusRequester baru
    val scrollState = rememberScrollState()
    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)
    val backgroundImage = if (isDarkTheme) R.drawable.bg_dark else R.drawable.bg_light

    var inputText by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            ChatHistoryManager.loadHistory(context).ifEmpty {
                listOf(Message("bot", "Hai, saya ChatNusa siap bantu kamu!"))
            }
        )
    }

    LaunchedEffect(messages) {
        ChatHistoryManager.saveHistory(context, messages)
    }

    // Fokus saat drawer dibuka/tutup
    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            focusManager.clearFocus() // Hilangkan fokus, keyboard turun
        } else {
            // Setelah drawer ditutup, fokus ke input dan keyboard muncul
            focusRequester.requestFocus()
        }
    }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = backgroundImage),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Menu", style = MaterialTheme.typography.titleLarge)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NavigationDrawerItem(
                                        label = { Text("Beranda") },
                                        selected = false,
                                        onClick = {}
                                    )
                                    NavigationDrawerItem(
                                        label = { Text("Tentang Aplikasi") },
                                        selected = false,
                                        onClick = {}
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = {
                                        messages = listOf(Message("bot", "Hai, saya siap bantu kamu!"))
                                        ChatHistoryManager.clearHistory(context)
                                        Toast.makeText(
                                            context,
                                            "Riwayat chat dihapus",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }) {
                                        Text("Hapus Riwayat")
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Mode Gelap", style = MaterialTheme.typography.bodyLarge)
                                    Switch(
                                        checked = isDarkTheme,
                                        onCheckedChange = {
                                            scope.launch {
                                                ThemePreference.setTheme(context, it)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            TopAppBar(
                                title = { Text("ChatNusa") },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch { drawerState.open() }
                                    }) {
                                        Icon(Icons.Filled.Menu, contentDescription = "Menu")
                                    }
                                },
                                actions = {
                                    var expanded by remember { mutableStateOf(false) }
                                    Box {
                                        IconButton(onClick = {
                                            expanded = true
                                            focusManager.clearFocus() // Hilangkan fokus saat dropdown dibuka
                                        }) {
                                            Icon(Icons.Filled.MoreVert, contentDescription = "Lainnya")
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = {
                                                expanded = false
                                                // Fokus kembali ke input saat dropdown tutup
                                                focusRequester.requestFocus()
                                            }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Riwayat Pencarian") },
                                                onClick = {
                                                    Toast.makeText(context, "Riwayat chat disimpan", Toast.LENGTH_SHORT).show()
                                                    expanded = false
                                                    focusRequester.requestFocus()
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
                                        modifier = Modifier
                                            .weight(1f)
                                            .focusRequester(focusRequester),
                                        placeholder = {
                                            Text(
                                                text = "Tulis pertanyaanmu",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        },
                                        maxLines = 2,
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = false
                                    )
                                    IconButton(
                                        onClick = {
                                            if (inputText.isNotBlank()) {
                                                val userText = inputText.trim()
                                                messages = messages + Message("user", userText)
                                                inputText = ""
                                                scope.launch {
                                                    try {
                                                        val response = RetrofitClient.api.sendMessage(
                                                            ChatRequest(userText)
                                                        )
                                                        val reply = when (response.type) {
                                                            "repository" -> response.results?.joinToString("\n") {
                                                                "- ${it.title}\nLink: ${it.link}\nSkor: ${it.score}"
                                                            } ?: "Tidak ada hasil relevan."

                                                            "info_UNP" -> response.jawaban
                                                                ?: "Tidak ada jawaban tersedia."

                                                            else -> "Jenis respons tidak dikenali."
                                                        }
                                                        messages = messages + Message("bot", reply)
                                                    } catch (e: Exception) {
                                                        messages = messages + Message(
                                                            "bot",
                                                            "Terjadi kesalahan: ${e.localizedMessage}"
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.White, RoundedCornerShape(12.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Filled.Send,
                                                contentDescription = "Kirim",
                                                tint = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

