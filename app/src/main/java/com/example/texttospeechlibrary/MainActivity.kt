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

    // Function to get content based on selected language
    fun getContentForLanguage(languageCode: String): String {
        return when (languageCode) {
            "en" -> "Life is a journey filled with countless moments, each offering an opportunity to learn, grow, and appreciate the world around us. Every interaction, whether with people, nature, or ideas, leaves a subtle mark that shapes who we are. A kind word can inspire hope, a small action can create change, and a curious mind can open doors to endless possibilities. Patience teaches us to wait without losing heart, and gratitude reminds us to cherish what we have while working toward what we dream of. Just as a river flows steadily toward the sea, we too can move forward, embracing challenges as stepping stones and celebrating victories, both big and small. In the rhythm of life, there is beauty in both the quiet moments and the grand adventures, and each heartbeat is a reminder that we are part of something greater than ourselves."

            "es" -> "La vida es un viaje lleno de innumerables momentos, cada uno ofreciendo una oportunidad para aprender, crecer y apreciar el mundo que nos rodea. Cada interacción, ya sea con personas, la naturaleza o las ideas, deja una marca sutil que forma lo que somos. Una palabra amable puede inspirar esperanza, una pequeña acción puede crear cambio, y una mente curiosa puede abrir puertas a posibilidades infinitas. La paciencia nos enseña a esperar sin perder el corazón, y la gratitud nos recuerda valorar lo que tenemos mientras trabajamos hacia lo que soñamos. Así como un río fluye constantemente hacia el mar, nosotros también podemos avanzar, abrazando los desafíos como piedras de paso y celebrando las victorias, tanto grandes como pequeñas."

            "fr" -> "La vie est un voyage rempli d'innombrables moments, chacun offrant une opportunité d'apprendre, de grandir et d'apprécier le monde qui nous entoure. Chaque interaction, que ce soit avec les gens, la nature ou les idées, laisse une marque subtile qui façonne ce que nous sommes. Un mot gentil peut inspirer l'espoir, une petite action peut créer le changement, et un esprit curieux peut ouvrir les portes à des possibilités infinies. La patience nous enseigne à attendre sans perdre cœur, et la gratitude nous rappelle de chérir ce que nous avons tout en travaillant vers ce dont nous rêvons. Tout comme une rivière coule régulièrement vers la mer, nous aussi pouvons avancer, embrassant les défis comme des tremplins et célébrant les victoires, grandes et petites."

            "de" -> "Das Leben ist eine Reise voller unzähliger Momente, die jeweils eine Gelegenheit bieten zu lernen, zu wachsen und die Welt um uns herum zu schätzen. Jede Interaktion, sei es mit Menschen, der Natur oder Ideen, hinterlässt eine subtile Spur, die formt, wer wir sind. Ein freundliches Wort kann Hoffnung inspirieren, eine kleine Handlung kann Veränderung bewirken, und ein neugieriger Geist kann Türen zu endlosen Möglichkeiten öffnen. Geduld lehrt uns zu warten, ohne das Herz zu verlieren, und Dankbarkeit erinnert uns daran, das zu schätzen, was wir haben, während wir auf das hinarbeiten, wovon wir träumen. So wie ein Fluss stetig zum Meer fließt, können auch wir vorankommen, Herausforderungen als Sprungbretter umarmend und Siege feiernd, sowohl große als auch kleine."

            "it" -> "La vita è un viaggio pieno di innumerevoli momenti, ognuno dei quali offre un'opportunità per imparare, crescere e apprezzare il mondo che ci circonda. Ogni interazione, che sia con le persone, la natura o le idee, lascia un segno sottile che plasma chi siamo. Una parola gentile può ispirare speranza, una piccola azione può creare cambiamento, e una mente curiosa può aprire porte a possibilità infinite. La pazienza ci insegna ad aspettare senza perdere il cuore, e la gratitudine ci ricorda di apprezzare quello che abbiamo mentre lavoriamo verso quello che sogniamo. Proprio come un fiume scorre costantemente verso il mare, anche noi possiamo andare avanti, abbracciando le sfide come pietre di passaggio e celebrando le vittorie, sia grandi che piccole."

            "pt" -> "A vida é uma jornada cheia de inúmeros momentos, cada um oferecendo uma oportunidade para aprender, crescer e apreciar o mundo ao nosso redor. Cada interação, seja com pessoas, natureza ou ideias, deixa uma marca sutil que molda quem somos. Uma palavra gentil pode inspirar esperança, uma pequena ação pode criar mudança, e uma mente curiosa pode abrir portas para possibilidades infinitas. A paciência nos ensina a esperar sem perder o coração, e a gratidão nos lembra de valorizar o que temos enquanto trabalhamos em direção ao que sonhamos. Assim como um rio flui constantemente em direção ao mar, nós também podemos seguir em frente, abraçando desafios como pedras de passagem e celebrando vitórias, tanto grandes quanto pequenas."

            else -> "Life is a journey filled with countless moments, each offering an opportunity to learn, grow, and appreciate the world around us."
        }
    }

    // Get content based on selected language
    val content = getContentForLanguage(selectedLanguage)

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