package com.denicrizz.chatnusa.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denicrizz.chatnusa.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tentang Aplikasi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo ChatNusa",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "ChatNusa",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Aplikasi chatbot yang membantu mahasiswa dan calon mahasiswa Universitas Nusantara PGRI Kediri mendapatkan informasi secara cepat dan efisien.",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 16.dp),
                    lineHeight = 22.sp
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text("Versi lainnya:", fontWeight = FontWeight.Medium)

                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        val telegramIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/chatnusa_bot"))
                        context.startActivity(telegramIntent)
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Telegram")
                    }

                    IconButton(onClick = {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://chat-nusa-react.vercel.app"))
                        context.startActivity(webIntent)
                    }) {
                        Icon(Icons.Default.Language, contentDescription = "Website")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Dibuat oleh Deni Kristanto",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
    )
}
