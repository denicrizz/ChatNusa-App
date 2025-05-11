package com.denicrizz.chatnusa
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.res.fontResource

class SplashActivity : ComponentActivity() {
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
// Tambahkan di bagian atas
val ModernFont = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold)
)


@Composable
fun SplashScreen() {
    // Background gambar
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
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo ChatNusa",
                modifier = Modifier.size(150.dp)
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
    }
}








