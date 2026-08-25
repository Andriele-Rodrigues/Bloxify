package com.example.brickbreaker.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamePreferences(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val PREFS_NAME = "bloxify_preferences"
        const val KEY_BRICK_COLOR = "pref_brick_color"
        const val KEY_BRICK_SIZE = "pref_brick_size"
        const val KEY_SOUND_ENABLED = "pref_sound_enabled"
        const val KEY_HIGH_SCORE = "pref_high_score"
        const val KEY_LAST_SCORE = "pref_last_score"

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

    private val _brickColorFlow = MutableStateFlow(loadColor())
    val brickColorFlow = _brickColorFlow.asStateFlow()

    private val _brickSizeFlow = MutableStateFlow(loadSize())
    val brickSizeFlow = _brickSizeFlow.asStateFlow()

    private val _soundEnabledFlow = MutableStateFlow(sharedPreferences.getBoolean(KEY_SOUND_ENABLED, DEFAULT_SOUND_ENABLED))
    val soundEnabledFlow = _soundEnabledFlow.asStateFlow()

    private val _highScoreFlow = MutableStateFlow(sharedPreferences.getInt(KEY_HIGH_SCORE, 0))
    val highScoreFlow = _highScoreFlow.asStateFlow()

    private val _lastScoreFlow = MutableStateFlow(sharedPreferences.getInt(KEY_LAST_SCORE, 0))
    val lastScoreFlow = _lastScoreFlow.asStateFlow()

    private fun loadColor(): BrickColorOption {
        val name = sharedPreferences.getString(KEY_BRICK_COLOR, DEFAULT_BRICK_COLOR.name)
        return try { BrickColorOption.valueOf(name!!) } catch (e: Exception) { DEFAULT_BRICK_COLOR }
    }

    private fun loadSize(): BrickSizeOption {
        val name = sharedPreferences.getString(KEY_BRICK_SIZE, DEFAULT_BRICK_SIZE.name)
        return try { BrickSizeOption.valueOf(name!!) } catch (e: Exception) { DEFAULT_BRICK_SIZE }
    }

    fun setBrickColor(option: BrickColorOption) {
        _brickColorFlow.value = option
        sharedPreferences.edit().putString(KEY_BRICK_COLOR, option.name).apply()
    }

    fun setBrickSize(option: BrickSizeOption) {
        _brickSizeFlow.value = option
        sharedPreferences.edit().putString(KEY_BRICK_SIZE, option.name).apply()
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabledFlow.value = enabled
        sharedPreferences.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun saveGameScore(score: Int) {
        val currentHigh = _highScoreFlow.value
        _lastScoreFlow.value = score
        sharedPreferences.edit().putInt(KEY_LAST_SCORE, score).apply()
        
        if (score > currentHigh) {
            _highScoreFlow.value = score
            sharedPreferences.edit().putInt(KEY_HIGH_SCORE, score).apply()
        }
    }

    fun resetToDefaults() {
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
