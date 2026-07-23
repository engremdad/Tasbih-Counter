package com.islamic.tasbihcounter.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

/**
 * Listens continuously and fires [onMatch] each time a phrase resembling the
 * target dhikr is heard. Uses on-device speech recognition; restarts itself
 * after each result to approximate continuous dictation.
 */
class DhikrVoiceRecognizer(
    private val context: Context,
    private val onMatch: () -> Unit,
    private val onError: (String) -> Unit
) {
    private var recognizer: SpeechRecognizer? = null
    private var listening = false
    private var keywords: List<String> = defaultKeywords

    val isAvailable: Boolean get() = SpeechRecognizer.isRecognitionAvailable(context)

    fun updateKeywords(transliteration: String) {
        // Break the transliteration into lowercased word fragments to match against.
        val words = transliteration.lowercase(Locale.ROOT)
            .replace("[^a-z ]".toRegex(), " ")
            .split(" ")
            .filter { it.length >= 3 }
        keywords = (words + defaultKeywords).distinct()
    }

    fun start() {
        if (listening || !isAvailable) return
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(listener)
        }
        listening = true
        listenOnce()
    }

    fun stop() {
        listening = false
        recognizer?.destroy()
        recognizer = null
    }

    private fun listenOnce() {
        if (!listening) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        runCatching { recognizer?.startListening(intent) }
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            // No-match / timeout are expected during pauses — just keep listening.
            if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                onError("Microphone permission is required for voice counting.")
                stop()
                return
            }
            if (listening) {
                recognizer?.cancel()
                listenOnce()
            }
        }

        override fun onResults(results: Bundle?) {
            handle(results)
            if (listening) listenOnce()
        }

        override fun onPartialResults(partialResults: Bundle?) {
            handle(partialResults)
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}

        private fun handle(results: Bundle?) {
            val spoken = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.joinToString(" ")?.lowercase(Locale.ROOT) ?: return
            if (keywords.any { spoken.contains(it) }) onMatch()
        }
    }

    private companion object {
        val defaultKeywords = listOf("subhan", "alhamd", "allahu", "akbar", "allah", "astaghfir", "ilaha")
    }
}
