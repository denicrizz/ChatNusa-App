package com.denicrizz.chatnusa
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.denicrizz.chatnusa.ui.theme.ChatNusaTheme
import com.denicrizz.chatnusa.screen.ChatScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatNusaTheme {
                ChatScreen()
            }
        }
    }
}
