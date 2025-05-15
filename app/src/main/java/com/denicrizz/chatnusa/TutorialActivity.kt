@file:OptIn(ExperimentalFoundationApi::class)
package com.denicrizz.chatnusa
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme
import com.denicrizz.chatnusa.ui.theme.ModernTypography


@ExperimentalMaterial3Api
class TutorialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengecek apakah tutorial sudah pernah dilihat
        val sharedPreferences = getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
        val isTutorialViewed = sharedPreferences.getBoolean("isTutorialViewed", false)

        if (isTutorialViewed) {
            // Jika tutorial sudah dilihat, langsung buka MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Tutup TutorialActivity
        } else {
            // Jika belum, tampilkan Tutorial Screen
            setContent {
                TutorialScreen()
            }
        }
    }
}

val Font = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen() {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })

    val tutorialItems = listOf(
        TutorialPage("Asisten Digital", "Hai butuh bantuan? Tenang, Aku siap bantu.", R.drawable.tutorial_image_1),
        TutorialPage("Sistem Informasi Unp Kedirii", "Kamu bisa akses info seputar Unp Kediri.", R.drawable.bg),
        TutorialPage("Akses Skripsi Kating Unp Kediri", "Aku juga bisa bantu kamu mencarikan skripsi yang kamu mau.", R.drawable.bg)
    )

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    MaterialTheme(typography = ModernTypography,) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF674636))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val item = tutorialItems[page]
                TutorialItem(item.title, item.description, item.imageRes)
            }

            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .background(color)
                    )
                }
            }

            Button(
                onClick = {
                    val sharedPreferences = context.getSharedPreferences("tutorial_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("isTutorialViewed", true).apply()

                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Mulai", color = Color.White)
            }
        }
    }
}

@Composable
fun TutorialItem(title: String, description: String, imageRes: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = description,
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}


data class TutorialPage(
    val title: String,
    val description: String,
    val imageRes: Int
)
