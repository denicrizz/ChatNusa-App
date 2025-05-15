package com.denicrizz.chatnusa

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denicrizz.chatnusa.ui.theme.ModernTypography
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Retrofit API Setup
data class Message(val sender: String, val text: String)
data class ChatRequest(val message: String)
data class ChatResponse(val reply: String)

interface ChatApiService {
    @POST("chat/") //endpoint API
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(context: Context) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var inputText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var messages by remember {
        mutableStateOf(
            listOf(
                Message("bot", "Hai, saya Chat Nusa siap bantu kamu!")
            )
        )
    }

    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            focusManager.clearFocus() // Turunkan keyboard
        }
    }

    fun clearChatHistory() {
        messages = listOf(Message("bot", "Hai, saya siap bantu kamu!"))
    }

    val scrollState = rememberScrollState()

    // Theme management
    val themeFlow = remember { ThemePreference.getTheme(context) }
    val isDarkTheme by themeFlow.collectAsState(initial = false)

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme(),
        typography = ModernTypography  // Pake ModernTypography di sini
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            "Menu",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        NavigationDrawerItem(
                            label = { Text("Beranda", style = MaterialTheme.typography.bodyLarge) },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } }
                        )
                        NavigationDrawerItem(
                            label = { Text("Tentang Aplikasi", style = MaterialTheme.typography.bodyLarge) },
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
                            Text(
                                text = if (isDarkTheme) "Mode Gelap" else "Mode Terang",
                                style = MaterialTheme.typography.bodyLarge
                            )
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
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    var expandedMenu by remember { mutableStateOf(false) }

                    TopAppBar(
                        title = {
                            Text(
                                "ChatNusa",
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            Box {
                                IconButton(onClick = { expandedMenu = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Opsi lainnya")
                                }

                                DropdownMenu(
                                    expanded = expandedMenu,
                                    onDismissRequest = { expandedMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Hapus Riwayat", style = MaterialTheme.typography.bodyLarge) },
                                        onClick = {
                                            clearChatHistory()
                                            expandedMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Simpan Riwayat", style = MaterialTheme.typography.bodyLarge) },
                                        onClick = {
                                            // Tambahkan logika simpan riwayat di sini
                                            expandedMenu = false
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
                                if (inputText.trim().isNotEmpty()) {
                                    val userMessage = inputText.trim()
                                    messages = messages + Message("user", userMessage)
                                    inputText = ""

                                    coroutineScope.launch {
                                        try {
                                            val response =
                                                RetrofitClient.api.sendMessage(ChatRequest(userMessage))
                                            messages = messages + Message("bot", response.reply)
                                        } catch (e: Exception) {
                                            messages = messages + Message(
                                                "bot",
                                                "Gagal mengambil jawaban: ${e.localizedMessage}"
                                            )
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

@Composable
fun ChatBubble(message: Message) {
    val isUser = message.sender == "user"
    val bubbleColor = if (isUser) Color(0xFF0050AC) else Color(0xFFEAEAEA)
    val textColor = if (isUser) Color.White else Color.Black

    val userBubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomEnd = 0.dp,   // lancip kanan bawah
        bottomStart = 16.dp
    )
    val botBubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomEnd = 16.dp,
        bottomStart = 0.dp   // lancip kiri bawah
    )
    val shape = if (isUser) userBubbleShape else botBubbleShape

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo hanya tampil di bot (kiri)
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = Color.LightGray,
                modifier = Modifier
                    .size(32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Bot Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = shape,
            color = bubbleColor,
            shadowElevation = 2.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(10.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }
}


