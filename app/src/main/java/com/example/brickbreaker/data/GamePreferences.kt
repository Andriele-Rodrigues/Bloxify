package com.example.brickbreaker.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing and persisting game settings (brick color palette, brick size, sound toggle).
 * Uses Android [SharedPreferences] for persistent storage and exposes reactive [StateFlow]s
 * to enable real-time UI and gameplay updates.
 */
class GamePreferences(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val PREFS_NAME = "bloxify_preferences"
        const val KEY_BRICK_COLOR = "pref_brick_color"
        const val KEY_BRICK_SIZE = "pref_brick_size"
        const val KEY_SOUND_ENABLED = "pref_sound_enabled"

        val DEFAULT_BRICK_COLOR = BrickColorOption.ROXO_BLOXIFY
        val DEFAULT_BRICK_SIZE = BrickSizeOption.MEDIO
        const val DEFAULT_SOUND_ENABLED = true

        @Volatile
        private var instance: GamePreferences? = null

        fun getInstance(context: Context): GamePreferences {
            return instance ?: synchronized(this) {
                instance ?: GamePreferences(context).also { instance = it }
            }
        }
    }

    constructor(context: Context) : this(
        (context.applicationContext ?: context).getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    private val _brickColorFlow: MutableStateFlow<BrickColorOption>
    val brickColorFlow: StateFlow<BrickColorOption>

    private val _brickSizeFlow: MutableStateFlow<BrickSizeOption>
    val brickSizeFlow: StateFlow<BrickSizeOption>

    private val _soundEnabledFlow: MutableStateFlow<Boolean>
    val soundEnabledFlow: StateFlow<Boolean>

    init {
        val savedColorName = sharedPreferences.getString(KEY_BRICK_COLOR, DEFAULT_BRICK_COLOR.name)
        val initialColor = try {
            if (savedColorName != null) BrickColorOption.valueOf(savedColorName) else DEFAULT_BRICK_COLOR
        } catch (_: IllegalArgumentException) {
            DEFAULT_BRICK_COLOR
        }
        _brickColorFlow = MutableStateFlow(initialColor)
        brickColorFlow = _brickColorFlow.asStateFlow()

        val savedSizeName = sharedPreferences.getString(KEY_BRICK_SIZE, DEFAULT_BRICK_SIZE.name)
        val initialSize = try {
            if (savedSizeName != null) BrickSizeOption.valueOf(savedSizeName) else DEFAULT_BRICK_SIZE
        } catch (_: IllegalArgumentException) {
            DEFAULT_BRICK_SIZE
        }
        _brickSizeFlow = MutableStateFlow(initialSize)
        brickSizeFlow = _brickSizeFlow.asStateFlow()

        val initialSound = sharedPreferences.getBoolean(KEY_SOUND_ENABLED, DEFAULT_SOUND_ENABLED)
        _soundEnabledFlow = MutableStateFlow(initialSound)
        soundEnabledFlow = _soundEnabledFlow.asStateFlow()
    }

    fun getBrickColor(): BrickColorOption = _brickColorFlow.value

    fun setBrickColor(option: BrickColorOption) {
        synchronized(this) {
            _brickColorFlow.value = option
            sharedPreferences.edit().putString(KEY_BRICK_COLOR, option.name).apply()
        }
    }

    fun getBrickSize(): BrickSizeOption = _brickSizeFlow.value

    fun setBrickSize(option: BrickSizeOption) {
        synchronized(this) {
            _brickSizeFlow.value = option
            sharedPreferences.edit().putString(KEY_BRICK_SIZE, option.name).apply()
        }
    }

    fun isSoundEnabled(): Boolean = _soundEnabledFlow.value

    fun setSoundEnabled(enabled: Boolean) {
        synchronized(this) {
            _soundEnabledFlow.value = enabled
            sharedPreferences.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
        }
    }

    fun resetToDefaults() {
        synchronized(this) {
            _brickColorFlow.value = DEFAULT_BRICK_COLOR
            _brickSizeFlow.value = DEFAULT_BRICK_SIZE
            _soundEnabledFlow.value = DEFAULT_SOUND_ENABLED
            sharedPreferences.edit()
                .putString(KEY_BRICK_COLOR, DEFAULT_BRICK_COLOR.name)
                .putString(KEY_BRICK_SIZE, DEFAULT_BRICK_SIZE.name)
                .putBoolean(KEY_SOUND_ENABLED, DEFAULT_SOUND_ENABLED)
                .apply()
        }
    }
}
