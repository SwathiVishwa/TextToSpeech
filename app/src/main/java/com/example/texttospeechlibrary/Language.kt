package com.example.texttospeechlibrary
/*

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RadioButton
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



// Function to check if TTS data is installed and prompt for installation if needed
@Composable
fun CheckTTSDataInstalled() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    LaunchedEffect(Unit) {
        val checkIntent = Intent().apply {
            action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        }

        activity?.startActivityForResult(checkIntent, CHECK_TTS_DATA)
    }
}

// Function to install TTS data if missing
fun installTTSData(activity: ComponentActivity) {
    val installIntent = Intent().apply {
        action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
    }
    activity.startActivity(installIntent)
}

@Composable
fun CheckLanguage(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var ttsInitialized by remember { mutableStateOf(false) }
    var ttsError by remember { mutableStateOf<String?>(null) }
    var needTTSInstall by remember { mutableStateOf(false) }

    val textToSpeech = remember {
        TextToSpeech(context.applicationContext) { status ->
            ttsInitialized = status == TextToSpeech.SUCCESS
            if (status != TextToSpeech.SUCCESS) {
                ttsError = "TTS initialization failed with status: $status"
                if (status == TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL) {
                    needTTSInstall = true
                }
            }
        }
    }
    var availableVoices by remember { mutableStateOf<List<Voice>>(emptyList()) }
    var selectedVoice by remember { mutableStateOf<Voice?>(null) }
    var isLoadingVoices by remember { mutableStateOf(false) }
    var availableEngines by remember { mutableStateOf<List<TextToSpeech.EngineInfo>>(emptyList()) }
    var selectedEngine by remember { mutableStateOf("") }
    var combinedVoices by remember { mutableStateOf<Map<String, List<Voice>>>(emptyMap()) }

    // Check if TTS data is installed
    CheckTTSDataInstalled()

    // Show dialog to install TTS data if needed
    if (needTTSInstall && activity != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { needTTSInstall = false },
            title = { Text("Text-to-Speech Data Required") },
            text = { Text("Additional Text-to-Speech data needs to be installed for this app to work properly.") },
            confirmButton = {
                Button(
                    onClick = {
                        installTTSData(activity)
                        needTTSInstall = false
                    }
                ) {
                    Text("Install")
                }
            },
            dismissButton = {
                Button(onClick = { needTTSInstall = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Initialize TTS and load voices from all available engines
    LaunchedEffect(ttsInitialized) {
        if (ttsInitialized) {
            isLoadingVoices = true
            
            try {
                // Get all available engines
                availableEngines = textToSpeech.engines
                selectedEngine = textToSpeech.defaultEngine
                
                // Collect voices from all engines
                val allEngineVoices = mutableMapOf<String, List<Voice>>()
                
                // First get voices from default engine
                textToSpeech.language = Locale.US
                val defaultEngineVoices = textToSpeech.voices?.toList() ?: emptyList()
                if (defaultEngineVoices.isNotEmpty()) {
                    allEngineVoices[selectedEngine] = defaultEngineVoices
                    availableVoices = defaultEngineVoices
                    selectedVoice = defaultEngineVoices.firstOrNull()
                    selectedVoice?.let { textToSpeech.voice = it }
                }
                
                // Try to get voices from all other engines
                for (engine in availableEngines) {
                    if (engine.name != selectedEngine) {
                        try {
                            // Create a temporary TTS instance for this engine
                            val tempTTS = TextToSpeech(context.applicationContext) { tempStatus ->
                                if (tempStatus == TextToSpeech.SUCCESS) {
                                    tempTTS.setEngineByPackageName(engine.name)
                                    val engineVoices = tempTTS.voices?.toList() ?: emptyList()
                                    if (engineVoices.isNotEmpty()) {
                                        allEngineVoices[engine.name] = engineVoices
                                    }
                                    tempTTS.shutdown()
                                }
                            }
                        } catch (e: Exception) {
                            // Just skip this engine if there's an error
                        }
                    }
                }
                
                // If we couldn't get voices from any engine, try different locales
                if (allEngineVoices.isEmpty()) {
                    val locales = listOf(
                        Locale.US, Locale.UK, Locale.CANADA, 
                        Locale.GERMANY, Locale.FRANCE, Locale.ITALY, 
                        Locale.JAPAN, Locale.KOREA, Locale.CHINA,
                        Locale("hi", "IN"), // Hindi
                        Locale("es", "ES"), // Spanish
                        Locale("ar", "SA"), // Arabic
                        Locale("ru", "RU"), // Russian
                        Locale("pt", "BR"), // Portuguese
                        Locale("id", "ID"), // Indonesian
                        Locale("th", "TH")  // Thai
                    )
                    
                    for (locale in locales) {
                        val result = textToSpeech.setLanguage(locale)
                        if (result != TextToSpeech.LANG_MISSING_DATA && 
                            result != TextToSpeech.LANG_NOT_SUPPORTED) {
                            val localeVoices = textToSpeech.voices?.toList() ?: emptyList()
                            if (localeVoices.isNotEmpty()) {
                                // Add these voices to the default engine
                                val existingVoices = allEngineVoices[selectedEngine] ?: emptyList()
                                allEngineVoices[selectedEngine] = (existingVoices + localeVoices).distinctBy { it.name }
                            }
                        }
                    }
                }
                
                // Update the combined voices map
                combinedVoices = allEngineVoices
                
                // If we have voices, update the available voices list
                if (allEngineVoices.isNotEmpty()) {
                    // Use voices from the selected engine
                    availableVoices = allEngineVoices[selectedEngine] ?: emptyList()
                    
                    // If no voices for selected engine, use the first engine with voices
                    if (availableVoices.isEmpty() && allEngineVoices.isNotEmpty()) {
                        val firstEngine = allEngineVoices.keys.first()
                        selectedEngine = firstEngine
                        availableVoices = allEngineVoices[firstEngine] ?: emptyList()
                    }
                    
                    // Set a default voice
                    selectedVoice = availableVoices.firstOrNull()
                    selectedVoice?.let { textToSpeech.voice = it }
                } else {
                    // Last resort - create a dummy voice with the default
                    val dummyVoice = textToSpeech.defaultVoice
                    if (dummyVoice != null) {
                        availableVoices = listOf(dummyVoice)
                        selectedVoice = dummyVoice
                    } else {
                        ttsError = "No voices available. Please install a Text-to-Speech engine with voice support."
                    }
                }
            } catch (e: Exception) {
                ttsError = "Error loading voices: ${e.message}"
            } finally {
                isLoadingVoices = false
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
        "Knowing how to write a paragraph is incredibly important. It's a basic aspect of writing, and it is something that everyone should know how to do. There is a specific structure that you have to follow when you're writing a paragraph. This structure helps make it easier for the reader to understand what is going on. Through writing good paragraphs, a person can communicate a lot better through their writing.\n" +
                "\n" +
                "When you want to write a paragraph, most of the time you should start off by coming up with an idea. After you have your idea or topic, you can start thinking about different things you can do to expand upon that idea. You should only finish the paragraph when you've finished covering everything you want about that idea."
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Hello $name!",
            modifier = modifier,
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = content,
            modifier = modifier
        )

        // Show error message if there is one
        ttsError?.let {
            Text(
                text = it,
                color = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // TTS Engine selection
        if (availableEngines.size > 1) {
            Text(
                text = "TTS Engine:",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            
            var engineExpanded by remember { mutableStateOf(false) }
            Box {
                Button(
                    onClick = { engineExpanded = true },
                    enabled = !isLoadingVoices && availableEngines.isNotEmpty()
                ) {
                    Text(
                        if (isLoadingVoices) "Loading engines..."
                        else availableEngines.find { it.name == selectedEngine }?.label ?: "Select Engine"
                    )
                }
                DropdownMenu(
                    expanded = engineExpanded, 
                    onDismissRequest = { engineExpanded = false },
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    availableEngines.forEach { engine ->
                        val engineVoices = combinedVoices[engine.name] ?: emptyList()
                        DropdownMenuItem(
                            text = { 
                                Row {
                                    Text(engine.label)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "(${engineVoices.size} voices)",
                                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                                    )
                                }
                            },
                            onClick = {
                                if (engine.name != selectedEngine) {
                                    selectedEngine = engine.name
                                    textToSpeech.setEngineByPackageName(engine.name)
                                    
                                    // Update available voices for this engine
                                    val newVoices = combinedVoices[engine.name] ?: emptyList()
                                    if (newVoices.isNotEmpty()) {
                                        availableVoices = newVoices
                                        selectedVoice = newVoices.firstOrNull()
                                        selectedVoice?.let { textToSpeech.voice = it }
                                    }
                                }
                                engineExpanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Voice selection
        Text(
            text = "Voice Options:",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium
        )
        
        if (isLoadingVoices) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Text("Loading voices, please wait...")
        } else if (availableVoices.isEmpty()) {
            Text(
                "No voices available for the selected engine",
                color = androidx.compose.ui.graphics.Color.Red
            )
        } else {
            // Group voices by language for better organization
            val voicesByLanguage = availableVoices.groupBy { it.locale.language }
            
            // Create a LazyColumn for the voices to handle many options
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .border(1.dp, androidx.compose.ui.graphics.Color.LightGray, androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                voicesByLanguage.forEach { (language, voices) ->
                    item {
                        Text(
                            text = Locale(language).displayLanguage,
                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    items(voices) { voice ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedVoice = voice
                                    textToSpeech.voice = voice
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp)
                                .background(
                                    if (selectedVoice?.name == voice.name)
                                        androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.3f)
                                    else
                                        androidx.compose.ui.graphics.Color.Transparent
                                ),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedVoice?.name == voice.name,
                                onClick = {
                                    selectedVoice = voice
                                    textToSpeech.voice = voice
                                }
                            )
                            
                            Column(
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(voice.name)
                                Text(
                                    "${voice.locale.displayLanguage} (${voice.locale.displayCountry})",
                                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                                )
                                
                                // Show voice quality if available
                                if (voice.quality > 0) {
                                    val qualityText = when (voice.quality) {
                                        Voice.QUALITY_VERY_HIGH -> "Very High Quality"
                                        Voice.QUALITY_HIGH -> "High Quality"
                                        Voice.QUALITY_NORMAL -> "Normal Quality"
                                        Voice.QUALITY_LOW -> "Low Quality"
                                        Voice.QUALITY_VERY_LOW -> "Very Low Quality"
                                        else -> "Quality: ${voice.quality}"
                                    }
                                    Text(
                                        qualityText,
                                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                        color = androidx.compose.ui.graphics.Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Row for speak and stop buttons
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, null)
                },
                enabled = ttsInitialized && selectedVoice != null
            ) {
                Text("Speak")
            }

            Button(
                onClick = {
                    textToSpeech.stop()
                },
                enabled = ttsInitialized
            ) {
                Text("Stop")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguagePreview() {
    TextToSpeechLibraryTheme {
        CheckLanguage("Android")
    }
}

// Constants
private const val CHECK_TTS_DATA = 1001*/
