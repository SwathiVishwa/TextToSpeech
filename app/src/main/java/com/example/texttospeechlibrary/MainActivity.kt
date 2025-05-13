package com.example.texttospeechlibrary

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.texttospeechlibrary.ui.theme.TextToSpeechLibraryTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TextToSpeechLibraryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn {
                        item {
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var ttsInitialized by remember { mutableStateOf(false) }
    val textToSpeech = remember {
        TextToSpeech(context.applicationContext) { status ->
            ttsInitialized = status == TextToSpeech.SUCCESS
        }
    }
    var availableVoices by remember { mutableStateOf<List<Voice>>(emptyList()) }
    var selectedVoice by remember { mutableStateOf<Voice?>(null) }

    // Initialize TTS and load voices
    LaunchedEffect(ttsInitialized) {
        if (ttsInitialized) {
            textToSpeech.language = Locale.US
            val voices = textToSpeech.voices
            if (voices != null) {
                availableVoices = voices.filter { it.locale.language == "en" }
                selectedVoice = availableVoices.firstOrNull()
                selectedVoice?.let { textToSpeech.voice = it }
            }
        }
    }

    // Clean up TTS when the Composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    val content =
        "Knowing how to write a paragraph is incredibly important. It’s a basic aspect of writing, and it is something that everyone should know how to do. There is a specific structure that you have to follow when you’re writing a paragraph. This structure helps make it easier for the reader to understand what is going on. Through writing good paragraphs, a person can communicate a lot better through their writing.\n" +
                "\n" +
                "When you want to write a paragraph, most of the time you should start off by coming up with an idea. After you have your idea or topic, you can start thinking about different things you can do to expand upon that idea. You should only finish the paragraph when you’ve finished covering everything you want about that idea."
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Text(
            text = content,
            modifier = modifier
        )

        // Dropdown to select voice
        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedVoice?.name ?: "Select Voice")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                availableVoices.forEach { voice ->
                    DropdownMenuItem(
                        text = { Text(voice.name) },
                        onClick = {
                            selectedVoice = voice
                            textToSpeech.voice = voice
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(Modifier.height(50.dp))

        // Row for speak and stop buttons
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            ) {
                Text("Speak")
            }

            Button(
                onClick = {
                    textToSpeech.stop()
                }
            ) {
                Text("Stop")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TextToSpeechLibraryTheme {
        Greeting("Android")
    }
}