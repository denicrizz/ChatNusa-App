@file:OptIn(ExperimentalFoundationApi::class)
package com.denicrizz.chatnusa.screen
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denicrizz.chatnusa.MainActivity
import com.denicrizz.chatnusa.R
import com.denicrizz.chatnusa.ui.theme.ModernTypography
import kotlinx.coroutines.delay

@ExperimentalMaterial3Api
class TutorialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("tutorial_prefs", MODE_PRIVATE)
        val isTutorialViewed = sharedPreferences.getBoolean("isTutorialViewed", false)

        if (isTutorialViewed) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            setContent {
                TutorialScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen() {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })

    val tutorialItems = listOf(
        TutorialPage("Asisten Digital ChatNusa", "Hai! Butuh bantuan? Tenang, ChatNusa siap membantumu.", R.drawable.tutorial_image_1),
        TutorialPage("Informasi UNP Kediri", "Dapatkan info seputar kampus, layanan, dan pembiayaan di Universitas Nusantara PGRI Kediri.", R.drawable.tutorial_image_2),
        TutorialPage("Akses Skripsi Mahasiswa", "ChatNusa bisa bantu cari skripsi dari repository UNP Kediri dengan mudah.", R.drawable.tutorial_image_3)
    )

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    MaterialTheme(typography = ModernTypography) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Background samar dari logo/chatnusa
            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = 0.04f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF003B8E),
                                Color(0xFF0057B8)
                            )
                        )
                    )
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
                        val color = if (pagerState.currentPage == iteration) Color.White else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(8.dp)
                                .background(color, shape = MaterialTheme.shapes.small)
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Mulai", color = Color(0xFF003B8E))
                }
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
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center
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
