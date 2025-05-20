package com.denicrizz.chatnusa.screen
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denicrizz.chatnusa.R
import com.denicrizz.chatnusa.screen.TutorialActivity

class SplashActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Durasi splash screen (3 detik)
        Handler(Looper.getMainLooper()).postDelayed({
            // Pindah ke tutorial setelah splash selesai
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
            finish() // Tutup activity splash agar tidak bisa diakses dengan tombol "back"
        }, 3000) // 3 detik delay

        // Set composable content untuk splash screen
        setContent {
            SplashScreen()
        }
    }
}

val ModernFont = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold)
)


@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "Background UNP Kediri",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo dengan radius bundar
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo ChatNusa",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(24.dp)) // radius bundar
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Hai, Selamat Datang di",
                fontSize = 18.sp,
                color = Color.White,
                fontFamily = ModernFont,
                fontWeight = FontWeight.Normal
            )

            Text(
                text = "ChatNusa",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = ModernFont,
                color = Color.White
            )
        }

        // Teks created by di bottom tengah
        Text(
            text = "Version 1.0",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.7f),
            fontFamily = ModernFont,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            textAlign = TextAlign.Center
        )
    }
}