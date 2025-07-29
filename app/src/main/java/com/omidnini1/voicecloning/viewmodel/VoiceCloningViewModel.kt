package com.omidnini1.voicecloning.viewmodel

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

class VoiceCloningViewModel : ViewModel() {
    
    private var mediaRecorder: MediaRecorder? = null
    private var textToSpeech: TextToSpeech? = null
    private var recordingFile: File? = null
    private var generatedAudioFile: File? = null
    private var isRecording = false
    
    fun startRecording(context: Context) {
        try {
            val fileName = "voice_sample_${System.currentTimeMillis()}.3gp"
            recordingFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
            
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(recordingFile?.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                
                prepare()
                start()
                isRecording = true
            }
            
            Toast.makeText(context, "شروع ضبط صدا", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(context, "خطا در ضبط صدا: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
        } catch (e: RuntimeException) {
            // Handle recording stop error
        }
    }
    
    fun generateVoice(
        text: String,
        emotion: String,
        context: Context,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    initializeTextToSpeech(context) { success ->
                        if (success) {
                            generateSpeechWithEmotion(text, emotion, context, onComplete)
                        } else {
                            onComplete()
                            Toast.makeText(context, "خطا در راه‌اندازی موتور تولید صدا", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onComplete()
                        Toast.makeText(context, "خطا در تولید صدا: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    private fun initializeTextToSpeech(context: Context, callback: (Boolean) -> Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("fa", "IR"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to English if Persian is not available
                    textToSpeech?.setLanguage(Locale.US)
                }
                callback(true)
            } else {
                callback(false)
            }
        }
    }
    
    private fun generateSpeechWithEmotion(
        text: String,
        emotion: String,
        context: Context,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val fileName = "generated_voice_${System.currentTimeMillis()}.wav"
                generatedAudioFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
                
                // Configure TTS parameters based on emotion
                textToSpeech?.let { tts ->
                    when (emotion) {
                        "خوشحال" -> {
                            tts.setSpeechRate(1.2f)
                            tts.setPitch(1.3f)
                        }
                        "غمگین" -> {
                            tts.setSpeechRate(0.8f)
                            tts.setPitch(0.7f)
                        }
                        "عصبانی" -> {
                            tts.setSpeechRate(1.3f)
                            tts.setPitch(1.1f)
                        }
                        "هیجان‌زده" -> {
                            tts.setSpeechRate(1.4f)
                            tts.setPitch(1.4f)
                        }
                        "آرام" -> {
                            tts.setSpeechRate(0.9f)
                            tts.setPitch(0.9f)
                        }
                        else -> { // معمولی
                            tts.setSpeechRate(1.0f)
                            tts.setPitch(1.0f)
                        }
                    }
                    
                    // Set utterance progress listener
                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            // Speech started
                        }
                        
                        override fun onDone(utteranceId: String?) {
                            viewModelScope.launch(Dispatchers.Main) {
                                onComplete()
                                Toast.makeText(context, "صدا با موفقیت تولید شد", Toast.LENGTH_SHORT).show()
                            }
                        }
                        
                        override fun onError(utteranceId: String?) {
                            viewModelScope.launch(Dispatchers.Main) {
                                onComplete()
                                Toast.makeText(context, "خطا در تولید صدا", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    
                    // Generate speech to file
                    val params = HashMap<String, String>()
                    params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "tts_${System.currentTimeMillis()}"
                    
                    val result = tts.synthesizeToFile(text, params, generatedAudioFile?.absolutePath)
                    
                    if (result != TextToSpeech.SUCCESS) {
                        onComplete()
                        Toast.makeText(context, "خطا در ذخیره فایل صوتی", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                onComplete()
                Toast.makeText(context, "خطا در تولید صدا: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    fun downloadAudio(context: Context) {
        generatedAudioFile?.let { file ->
            if (file.exists()) {
                try {
                    // Copy to Music directory
                    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    val voiceCloningDir = File(musicDir, "VoiceCloning")
                    if (!voiceCloningDir.exists()) {
                        voiceCloningDir.mkdirs()
                    }
                    
                    val targetFile = File(voiceCloningDir, "voice_cloned_${System.currentTimeMillis()}.wav")
                    file.copyTo(targetFile, overwrite = true)
                    
                    // Notify media scanner
                    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    intent.data = android.net.Uri.fromFile(targetFile)
                    context.sendBroadcast(intent)
                    
                    Toast.makeText(context, "فایل در پوشه موزیک ذخیره شد", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "خطا در ذخیره فایل: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "ابتدا صدا را تولید کنید", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "فایل صوتی یافت نشد", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun shareAudio(context: Context) {
        generatedAudioFile?.let { file ->
            if (file.exists()) {
                try {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "com.omidnini1.voicecloning.fileprovider",
                        file
                    )
                    
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "audio/*"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_TEXT, "صدای تولید شده با Voice Cloning omidnini1")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    
                    context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری صدا"))
                } catch (e: Exception) {
                    Toast.makeText(context, "خطا در اشتراک‌گذاری: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "ابتدا صدا را تولید کنید", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "فایل صوتی یافت نشد", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        mediaRecorder?.release()
    }
}