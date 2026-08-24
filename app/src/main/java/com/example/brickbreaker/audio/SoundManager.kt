package com.example.brickbreaker.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.annotation.RawRes
import com.example.brickbreaker.R

/**
 * Gerenciador de efeitos sonoros para o jogo Brick Breaker (Bloxify).
 *
 * Utiliza [SoundPool] com [AudioAttributes] configurados para baixa latência e
 * tipo de som de jogo ([AudioAttributes.USAGE_GAME] e [AudioAttributes.CONTENT_TYPE_SONIFICATION]).
 *
 * Suporta:
 * - Pré-carregamento dos áudios de início de fase (stage_start) e colisão no paddle (paddle_hit).
 * - Controle de volume geral (masterVolume) e multiplicador por efeito.
 * - Alternador de ativação/desativação de som (isSoundEnabled).
 * - Liberação determinística de recursos nativos via [release].
 */
class SoundManager(
    context: Context? = null,
    private val soundPoolOverride: SoundPool? = null
) {
    private val appContext: Context? = context?.applicationContext

    companion object {
        const val USAGE = AudioAttributes.USAGE_GAME
        const val CONTENT_TYPE = AudioAttributes.CONTENT_TYPE_SONIFICATION
        const val MAX_STREAMS = 6
        val STAGE_START_RES_ID = R.raw.stage_start
        val PADDLE_HIT_RES_ID = R.raw.paddle_hit
    }

    private val soundPool: SoundPool?
    private val loadedSoundIds = mutableSetOf<Int>()

    // Sound sample IDs returned by SoundPool
    var stageStartSoundId: Int = 0
        private set
    var paddleHitSoundId: Int = 0
        private set

    /** Alternador para ativar ou desativar os efeitos sonoros (integrado às configurações). */
    var isSoundEnabled: Boolean = true

    /** Volume geral do áudio, restrito entre 0.0f e 1.0f. */
    var masterVolume: Float = 1.0f
        set(value) {
            field = value.coerceIn(0.0f, 1.0f)
        }

    /** Indica se o SoundManager já foi liberado. */
    var isReleased: Boolean = false
        private set

    init {
        soundPool = soundPoolOverride ?: try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(USAGE)
                .setContentType(CONTENT_TYPE)
                .build()

            SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build()
        } catch (e: Throwable) {
            null
        }

        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                loadedSoundIds.add(sampleId)
            }
        }

        preloadSounds()
    }

    private fun preloadSounds() {
        if (appContext != null && soundPool != null) {
            stageStartSoundId = loadSound(STAGE_START_RES_ID)
            paddleHitSoundId = loadSound(PADDLE_HIT_RES_ID)
        }
    }

    /**
     * Carrega um recurso de áudio bruto no SoundPool.
     */
    fun loadSound(@RawRes resId: Int): Int {
        val ctx = appContext ?: return 0
        val pool = soundPool ?: return 0
        return try {
            pool.load(ctx, resId, 1)
        } catch (e: Throwable) {
            0
        }
    }

    /**
     * Toca o efeito sonoro de início/reinício de fase.
     *
     * @param volumeMultiplier multiplicador de volume opcional (padrão 1.0f).
     * @return Stream ID retornado pelo SoundPool (0 se não reproduzido).
     */
    fun playStageStart(volumeMultiplier: Float = 1.0f): Int {
        return playSound(stageStartSoundId, volumeMultiplier)
    }

    /**
     * Toca o efeito sonoro de colisão da bola com o paddle.
     *
     * @param volumeMultiplier multiplicador de volume opcional (padrão 1.0f).
     * @return Stream ID retornado pelo SoundPool (0 se não reproduzido).
     */
    fun playPaddleHit(volumeMultiplier: Float = 1.0f): Int {
        return playSound(paddleHitSoundId, volumeMultiplier)
    }

    /**
     * Reproduz uma amostra de áudio específica caso o som esteja ativo e a amostra carregada.
     */
    fun playSound(soundId: Int, volumeMultiplier: Float = 1.0f): Int {
        if (!isSoundEnabled || isReleased || soundId == 0) return 0
        val pool = soundPool ?: return 0

        if (loadedSoundIds.isEmpty() || loadedSoundIds.contains(soundId)) {
            val volume = (masterVolume * volumeMultiplier).coerceIn(0.0f, 1.0f)
            return try {
                pool.play(
                    soundId,
                    volume,  // leftVolume
                    volume,  // rightVolume
                    1,       // priority
                    0,       // loop
                    1.0f     // rate
                )
            } catch (e: Throwable) {
                0
            }
        }
        return 0
    }

    /**
     * Registra manualmente uma amostra como carregada (útil para testes ou notificações de carregamento).
     */
    fun markSampleLoaded(sampleId: Int) {
        loadedSoundIds.add(sampleId)
    }

    /**
     * Verifica se uma amostra está registrada como carregada.
     */
    fun isSampleLoaded(sampleId: Int): Boolean {
        return loadedSoundIds.contains(sampleId)
    }

    /**
     * Libera todos os recursos nativos alocados pelo SoundPool.
     */
    fun release() {
        if (isReleased) return
        isReleased = true
        try {
            soundPool?.release()
        } catch (e: Throwable) {
            // Safe cleanup
        }
        loadedSoundIds.clear()
    }
}
