package com.omidnini1.voicecloning

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.omidnini1.voicecloning.ui.theme.VoiceCloningOmidnini1Theme
import com.omidnini1.voicecloning.viewmodel.VoiceCloningViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.util.*

class MainActivity : ComponentActivity() {
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(this, "تمام مجوزها برای عملکرد صحیح برنامه لازم است", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        checkAndRequestPermissions()
        
        setContent {
            VoiceCloningOmidnini1Theme {
                VoiceCloningApp()
            }
        }
    }
    
    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCloningApp() {
    val viewModel: VoiceCloningViewModel = viewModel()
    val context = LocalContext.current
    var isDarkTheme by remember { mutableStateOf(false) }
    
    val colorScheme = if (isDarkTheme) {
        darkColorScheme(
            primary = Color(0xFF6200EA),
            secondary = Color(0xFF03DAC5),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.White,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6200EA),
            secondary = Color(0xFF03DAC5),
            background = Color(0xFFFFFBFE),
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        )
    }
    
    MaterialTheme(colorScheme = colorScheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with logo and theme toggle
                HeaderSection(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Main content
                VoiceCloningContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HeaderSection(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo section
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo placeholder (you can replace with actual logo)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF6200EA),
                                Color(0xFF3700B3)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = "Voice Cloning",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "omidnini1",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
        
        // Theme toggle
        IconButton(
            onClick = onThemeToggle,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Toggle Theme",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCloningContent(viewModel: VoiceCloningViewModel) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var selectedEmotion by remember { mutableStateOf("معمولی") }
    var isRecording by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val maxCharacters = 100000
    
    val emotions = listOf("معمولی", "خوشحال", "غمگین", "عصبانی", "هیجان‌زده", "آرام")
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Voice Recording Section
            VoiceRecordingCard(
                isRecording = isRecording,
                onRecordingToggle = { 
                    isRecording = !isRecording
                    if (isRecording) {
                        viewModel.startRecording(context)
                    } else {
                        viewModel.stopRecording()
                    }
                }
            )
        }
        
        item {
            // Emotion Selection
            EmotionSelectionCard(
                selectedEmotion = selectedEmotion,
                emotions = emotions,
                onEmotionSelected = { selectedEmotion = it }
            )
        }
        
        item {
            // Text Input Section
            TextInputCard(
                inputText = inputText,
                maxCharacters = maxCharacters,
                onTextChange = { if (it.length <= maxCharacters) inputText = it }
            )
        }
        
        item {
            // Generate Voice Button
            GenerateVoiceButton(
                isProcessing = isProcessing,
                enabled = inputText.isNotBlank() && !isRecording,
                onClick = {
                    isProcessing = true
                    viewModel.generateVoice(
                        text = inputText,
                        emotion = selectedEmotion,
                        context = context,
                        onComplete = { isProcessing = false }
                    )
                }
            )
        }
        
        item {
            // Action Buttons (Download & Share)
            ActionButtonsCard(
                onDownload = { viewModel.downloadAudio(context) },
                onShare = { viewModel.shareAudio(context) }
            )
        }
    }
}

@Composable
fun VoiceRecordingCard(
    isRecording: Boolean,
    onRecordingToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRecording) 
                Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ضبط صدای شما",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recording Button with Animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRecording) Color(0xFFFF5252) else Color(0xFF6200EA)
                    )
                    .clickable { onRecordingToggle() },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isRecording,
                    transitionSpec = {
                        fadeIn() + scaleIn() with fadeOut() + scaleOut()
                    }
                ) { recording ->
                    Icon(
                        imageVector = if (recording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (recording) "توقف ضبط" else "شروع ضبط",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isRecording) "در حال ضبط..." else "برای ضبط صدا کلیک کنید",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            if (isRecording) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFF5252)
                )
            }
        }
    }
}

@Composable
fun EmotionSelectionCard(
    selectedEmotion: String,
    emotions: List<String>,
    onEmotionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "انتخاب لحن صدا",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(emotions) { emotion ->
                    FilterChip(
                        onClick = { onEmotionSelected(emotion) },
                        label = { Text(emotion) },
                        selected = selectedEmotion == emotion,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6200EA),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputCard(
    inputText: String,
    maxCharacters: Int,
    onTextChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "متن مورد نظر",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = inputText,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { 
                    Text(
                        "متن خود را اینجا وارد کنید...\nپشتیبانی از زبان فارسی و ۹۹٪ زبان‌های دنیا",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                supportingText = {
                    Text(
                        text = "${inputText.length} / $maxCharacters کاراکتر",
                        color = if (inputText.length > maxCharacters * 0.9) 
                            Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                isError = inputText.length > maxCharacters,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6200EA),
                    cursorColor = Color(0xFF6200EA)
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun GenerateVoiceButton(
    isProcessing: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onClick,
                enabled = enabled && !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EA),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (isProcessing) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "در حال پردازش...",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "تولید صدا",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtonsCard(
    onDownload: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "عملیات",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Download Button
                OutlinedButton(
                    onClick = onDownload,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF6200EA)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF6200EA)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "دانلود",
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Share Button
                Button(
                    onClick = onShare,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF03DAC5),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "اشتراک",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}