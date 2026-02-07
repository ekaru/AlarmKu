package com.heikal.alarmku.alarm

import android.content.Context
import android.media.MediaPlayer

object AlarmPlayer {
    var mediaPlayer: MediaPlayer? = null

    fun play(context: Context, resId: Int) {
        if (mediaPlayer?.isPlaying == true) return
        stop()
        mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}