package com.mostafa.majiddelbandam.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.runtime.staticCompositionLocalOf
import com.mostafa.majiddelbandam.R
import com.mostafa.majiddelbandam.domain.PlayerProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class GameAudio(
    context: Context,
    progress: Flow<PlayerProgress>
) {
    private val app = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val enabled = AtomicBoolean(false)
    private val foreground = AtomicBoolean(false)

    private var musicA: MediaPlayer? = null
    private var musicB: MediaPlayer? = null
    private var resumeFirst = true
    private var musicVolume = MUSIC_VOL
    private val pool: SoundPool
    private val clickId: Int
    private val correctId: Int
    private val wrongId: Int
    private val winId: Int
    private val musicAttrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        pool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attrs)
            .build()
        clickId = pool.load(app, R.raw.sfx_click, 1)
        correctId = pool.load(app, R.raw.sfx_correct, 1)
        wrongId = pool.load(app, R.raw.sfx_wrong, 1)
        winId = pool.load(app, R.raw.sfx_win, 1)

        scope.launch {
            progress.map { it.soundEnabled }.distinctUntilChanged().collect { on ->
                setEnabled(on)
            }
        }
    }

    fun setEnabled(on: Boolean) {
        enabled.set(on)
        if (on) startMusic() else stopMusic()
    }

    fun enterForeground() {
        foreground.set(true)
        startMusic()
    }

    fun enterBackground() {
        foreground.set(false)
        pauseMusic()
    }

    fun click(soft: Boolean = false) = play(clickId, if (soft) 0.30f else 0.58f)

    fun correct() = play(correctId, 0.74f)

    fun wrong() = play(wrongId, 0.62f)

    fun win() {
        duck(0.10f)
        play(winId, 0.90f)
        scope.launch {
            delay(2400)
            duck(MUSIC_VOL)
        }
    }

    fun wrap(action: () -> Unit): () -> Unit = {
        click()
        action()
    }

    private fun play(id: Int, volume: Float) {
        if (!enabled.get() || id == 0) return
        runCatching { pool.play(id, volume, volume, 1, 0, 1f) }
    }

    private fun startMusic() {
        if (!enabled.get() || !foreground.get()) return
        try {
            if (musicA == null || musicB == null) {
                releaseMusic()
                musicA = buildMusicPlayer()
                musicB = buildMusicPlayer()
                armGapless(musicA!!, musicB!!)
                armGapless(musicB!!, musicA!!)
            }
            val player = listOfNotNull(musicA, musicB).firstOrNull { it.isPlaying }
                ?: (if (resumeFirst) musicA else musicB)
                ?: return
            if (!player.isPlaying) player.start()
        } catch (_: Exception) {
            releaseMusic()
        }
    }

    private fun buildMusicPlayer(): MediaPlayer {
        val player = MediaPlayer()
        app.resources.openRawResourceFd(R.raw.music_courtyard).use { fd ->
            player.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        }
        player.setAudioAttributes(musicAttrs)
        player.setVolume(musicVolume, musicVolume)
        player.isLooping = false
        player.prepare()
        return player
    }

    private fun armGapless(current: MediaPlayer, next: MediaPlayer) {
        current.setNextMediaPlayer(next)
        current.setOnCompletionListener {
            resumeFirst = next === musicA
            runCatching {
                current.reset()
                app.resources.openRawResourceFd(R.raw.music_courtyard).use { fd ->
                    current.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                }
                current.setAudioAttributes(musicAttrs)
                current.setVolume(musicVolume, musicVolume)
                current.prepare()
                next.setNextMediaPlayer(current)
            }
        }
    }

    private fun pauseMusic() {
        runCatching { if (musicA?.isPlaying == true) musicA?.pause() }
        runCatching { if (musicB?.isPlaying == true) musicB?.pause() }
    }

    private fun stopMusic() {
        pauseMusic()
        runCatching { musicA?.seekTo(0) }
        runCatching { musicB?.seekTo(0) }
        resumeFirst = true
    }

    private fun releaseMusic() {
        runCatching { musicA?.setOnCompletionListener(null) }
        runCatching { musicB?.setOnCompletionListener(null) }
        runCatching { musicA?.release() }
        runCatching { musicB?.release() }
        musicA = null
        musicB = null
    }

    private fun duck(vol: Float) {
        musicVolume = vol
        runCatching { musicA?.setVolume(vol, vol) }
        runCatching { musicB?.setVolume(vol, vol) }
    }

    private companion object {
        const val MUSIC_VOL = 0.42f
    }
}

val LocalGameAudio = staticCompositionLocalOf<GameAudio> {
    error("GameAudio not provided")
}
