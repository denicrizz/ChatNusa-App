package com.denicrizz.chatnusa.model

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.denicrizz.chatnusa.R
import com.denicrizz.chatnusa.component.AnimatedTypingIndicator
import com.denicrizz.chatnusa.component.TypingIndicatorDefaults
import java.util.regex.Pattern

@Composable
fun ChatBubble(message: Message) {
    val context = LocalContext.current
    val isUser = message.sender == "user"
    val bubbleColor = if (isUser) Color(0xFF0050AC) else Color(0xFFEAEAEA)
    val textColor = if (isUser) Color.White else Color.Black

    val userShape =
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 0.dp, bottomStart = 16.dp)
    val botShape =
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 0.dp)
    val shape = if (isUser) userShape else botShape

    fun linkify(text: String): AnnotatedString {
        val urlPattern = Pattern.compile("(https?://\\S+)")
        val matcher = urlPattern.matcher(text)
        return buildAnnotatedString {
            var lastIndex = 0
            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                append(text.substring(lastIndex, start))
                pushStringAnnotation(tag = "URL", annotation = text.substring(start, end))
                withStyle(
                    SpanStyle(
                        color = Color(0xFF0645AD),
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(text.substring(start, end))
                }
                pop()
                lastIndex = end
            }
            append(text.substring(lastIndex))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Bot",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = shape,
            color = bubbleColor,
            shadowElevation = 2.dp
        ) {
            Box(modifier = Modifier.padding(10.dp)) {
                if (isUser) {
                    // User bubble
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    if (message.text == "typing") {
                        AnimatedTypingIndicator(
                            configuration = TypingIndicatorDefaults.Configuration(
                                activeColor = Color.Gray,
                                inactiveColor = Color.LightGray
                            )
                        )
                    } else {
                        // Bot bubble with clickable link only
                        SelectionContainer {
                            ClickableText(
                                text = linkify(message.text),
                                style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { offset ->
                                    val annotations = linkify(message.text).getStringAnnotations(
                                        "URL",
                                        offset,
                                        offset
                                    )
                                    if (annotations.isNotEmpty()) {
                                        val url = annotations[0].item
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    }
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}
