package com.islamic.tasbihcounter.util

import android.content.Context
import android.media.MediaPlayer
import com.islamic.tasbihcounter.data.AsmaAudioResources

/**
 * Plays the bundled 99-Names recitations one at a time. Reusing a single
 * MediaPlayer means starting a new name stops the previous one cleanly.
 */
class AsmaPlayer(private val context: Context) {
    private var player: MediaPlayer? = null

    fun play(number: Int) {
        release()
        player = MediaPlayer.create(context, AsmaAudioResources.byNumber(number))?.apply {
            setOnCompletionListener { release() }
            start()
        }
    }

    fun release() {
        player?.let {
            runCatching { it.stop() }
            it.release()
        }
        player = null
    }
}
