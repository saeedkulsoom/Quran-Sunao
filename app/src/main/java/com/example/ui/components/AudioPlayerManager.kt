package com.example.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurahNumber: Int = 0,
    val currentSurahName: String = "",
    val currentAyahNumber: Int = 0,
    val currentAudioUrl: String = "",
    val error: String? = null
)

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    fun playAyah(surahNumber: Int, surahName: String, ayahNumber: Int, audioUrl: String) {
        try {
            if (_playbackState.value.isPlaying && _playbackState.value.currentAudioUrl == audioUrl) {
                pause()
                return
            }

            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            _playbackState.value = AudioPlaybackState(
                isPlaying = false,
                isLoading = true,
                currentSurahNumber = surahNumber,
                currentSurahName = surahName,
                currentAyahNumber = ayahNumber,
                currentAudioUrl = audioUrl
            )

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    mp.start()
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        isLoading = false,
                        error = null
                    )
                }
                setOnCompletionListener {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isLoading = false
                    )
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayerManager", "Playback error: $what, $extra")
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isLoading = false,
                        error = "Unable to stream audio"
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Exception playing audio", e)
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                isLoading = false,
                error = e.localizedMessage
            )
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playbackState.value = _playbackState.value.copy(isPlaying = false)
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            it.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error stopping player", e)
        }
        _playbackState.value = AudioPlaybackState()
    }

    fun release() {
        stop()
    }
}
