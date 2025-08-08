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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 10.dp, vertical = 30.dp)
                    ) {


                        item {
                            Greeting("")
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
        TextToSpeech(context.applicationContext, { status ->
            ttsInitialized = status == TextToSpeech.SUCCESS
        }, "com.google.android.tts")
    }
    var availableVoices by remember { mutableStateOf<List<Voice>>(emptyList()) }
    var selectedVoice by remember { mutableStateOf<Voice?>(null) }
    var availableLanguages by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedLanguage by remember { mutableStateOf("en") }

    // Function to get language display name
    fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            "en" -> "English"
            "es" -> "Spanish"
            "fr" -> "French"
            "de" -> "German"
            "it" -> "Italian"
            "pt" -> "Portuguese"
            "ru" -> "Russian"
            "ja" -> "Japanese"
            "ko" -> "Korean"
            "zh" -> "Chinese"
            "ar" -> "Arabic"
            "hi" -> "Hindi"
            "nl" -> "Dutch"
            "sv" -> "Swedish"
            "da" -> "Danish"
            "no" -> "Norwegian"
            "fi" -> "Finnish"
            "pl" -> "Polish"
            "tr" -> "Turkish"
            "th" -> "Thai"
            "vi" -> "Vietnamese"
            "cs" -> "Czech"
            "hu" -> "Hungarian"
            "ro" -> "Romanian"
            "sk" -> "Slovak"
            "bg" -> "Bulgarian"
            "hr" -> "Croatian"
            "sl" -> "Slovenian"
            "et" -> "Estonian"
            "lv" -> "Latvian"
            "lt" -> "Lithuanian"
            "el" -> "Greek"
            "he" -> "Hebrew"
            "uk" -> "Ukrainian"
            else -> languageCode.uppercase()
        }
    }

    // Function to format voice display name
    fun formatVoiceName(voice: Voice): String {
        val languageName = getLanguageDisplayName(voice.locale.language)
        val countryName = voice.locale.displayCountry

        // Extract gender from voice name or features
        val gender = when {
            voice.name.contains("male", ignoreCase = true) && !voice.name.contains(
                "female",
                ignoreCase = true
            ) -> "Male"
            voice.name.contains("female", ignoreCase = true) -> "Female"
            voice.features?.contains("maleVoice") == true -> "Male"
            voice.features?.contains("femaleVoice") == true -> "Female"
            else -> "Voice"
        }

        return "$languageName - $gender ($countryName)"
    }

    // Initialize TTS and load voices
    LaunchedEffect(ttsInitialized) {
        if (ttsInitialized) {
            textToSpeech.language = Locale.US
            val voices = textToSpeech.voices
            if (voices != null) {
                // Only include specific languages
                val targetLanguages = listOf("en", "es", "it", "de", "fr", "pt")
                availableLanguages = voices.map { it.locale.language }
                    .distinct()
                    .filter { it in targetLanguages }
                    .sorted()

                // Filter voices by selected language
                availableVoices = voices.filter { it.locale.language == selectedLanguage }
                selectedVoice = availableVoices.firstOrNull()
                selectedVoice?.let { textToSpeech.voice = it }
            }
        }
    }

    // Update voices when language selection changes
    LaunchedEffect(selectedLanguage, ttsInitialized) {
        if (ttsInitialized) {
            val voices = textToSpeech.voices
            if (voices != null) {
                availableVoices = voices.filter { it.locale.language == selectedLanguage }
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
        "Life is a journey filled with countless moments, each offering an opportunity to learn, grow, and appreciate the world around us. Every interaction, whether with people, nature, or ideas, leaves a subtle mark that shapes who we are. A kind word can inspire hope, a small action can create change, and a curious mind can open doors to endless possibilities. Patience teaches us to wait without losing heart, and gratitude reminds us to cherish what we have while working toward what we dream of. Just as a river flows steadily toward the sea, we too can move forward, embracing challenges as stepping stones and celebrating victories, both big and small. In the rhythm of life, there is beauty in both the quiet moments and the grand adventures, and each heartbeat is a reminder that we are part of something greater than ourselves."
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Text(
            text = content,
            modifier = modifier
        )
        Spacer(modifier = Modifier.height(28.dp))
        // Language dropdown
        var languageExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { languageExpanded = true },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Language: ${getLanguageDisplayName(selectedLanguage)}")
            }
            DropdownMenu(
                modifier = Modifier.fillMaxWidth(0.8f),
                expanded = languageExpanded,
                onDismissRequest = { languageExpanded = false }) {
                availableLanguages.forEach { language ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                getLanguageDisplayName(language), textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(0.6f))
                        },
                        onClick = {
                            selectedLanguage = language
                            languageExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Dropdown to select voice
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(selectedVoice?.let { formatVoiceName(it) } ?: "Select Voice")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                availableVoices.forEach { voice ->
                    DropdownMenuItem(
                        text = {
                            Text(formatVoiceName(voice))
                        },
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