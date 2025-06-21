package com.denicrizz.chatnusa.screen
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denicrizz.chatnusa.R
import com.denicrizz.chatnusa.model.ChatBubble
import com.denicrizz.chatnusa.model.Message
import com.denicrizz.chatnusa.utils.ChatHistoryManager
import com.denicrizz.chatnusa.utils.ThemePreference
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isDarkTheme by ThemePreference.getTheme(context).collectAsState(initial = false)
    val backgroundImage = if (isDarkTheme) R.drawable.bg_dark else R.drawable.bg_light

    var messages by remember {
        mutableStateOf(ChatHistoryManager.loadHistory(context))
    }

    fun formatChat(messages: List<Message>): String {
        return messages.joinToString("\n\n") { msg ->
            val sender = if (msg.sender == "user") "Anda" else "ChatNusa"
            "$sender:\n${msg.text}"
        }
    }

    fun shareToWhatsApp(content: String) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, content)
                type = "text/plain"
                setPackage("com.whatsapp")
            }
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "WhatsApp tidak ditemukan di perangkat ini.",
                Toast.LENGTH_SHORT
            ).show()
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

                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        TopAppBar(
                            title = { Text("Riwayat Chat") },
                            navigationIcon = {
                                IconButton(onClick = { onBack() }) {
                                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    val content = formatChat(messages)
                                    shareToWhatsApp(content)
                                }) {
                                    Icon(Icons.Filled.Share, contentDescription = "Bagikan ke WhatsApp")
                                }
                                IconButton(onClick = {
                                    ChatHistoryManager.clearHistory(context)
                                    messages = emptyList()
                                    Toast.makeText(
                                        context,
                                        "Riwayat berhasil dihapus",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Hapus Riwayat")
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
                            if (messages.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Tidak ada riwayat chat.")
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .verticalScroll(scrollState)
                                ) {
                                    messages.forEach { message ->
                                        ChatBubble(message)
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
