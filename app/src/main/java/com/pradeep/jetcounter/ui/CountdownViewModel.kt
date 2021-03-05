/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pradeep.jetcounter.ui

import androidx.lifecycle.ViewModel
import com.pradeep.jetcounter.timer.TimerTask
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Collections.copy
import kotlinx.coroutines.flow.asStateFlow

class CountdownViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val timer = TimerTask(
        onTick = { minutes, seconds, progress ->
            _uiState.value = _uiState.value.run {
                copy(
                    minutes = minutes,
                    seconds = seconds,
                    progress = progress,
                )
            }
        },
        onFinish = {
            _uiState.value = _uiState.value.run {
                copy(
                    timerState = TimerState.READY,
                    minutes = 0,
                    seconds = 0,
                    progress = 0f,
                )
            }
        }
    )

    fun incrementMinutes() {
        if (_uiState.value.minutes >= 99) {
            return
        }
        _uiState.value = _uiState.value.run {
            copy(minutes = minutes + 1)
        }
    }

    fun decrementMinutes() {
        if (_uiState.value.minutes <= 0) {
            return
        }
        _uiState.value = _uiState.value.run {
            copy(minutes = minutes - 1)
        }
    }

    fun incrementSeconds() {
        if (_uiState.value.seconds >= 59) {
            return
        }
        _uiState.value = _uiState.value.run {
            copy(seconds = seconds + 1)
        }
    }

    fun decrementSeconds() {
        if (_uiState.value.seconds <= 0) {
            return
        }
        _uiState.value = _uiState.value.run {
            copy(seconds = seconds - 1)
        }
    }

    fun startTimer() {
        val millis = _uiState.value.run {
            ((minutes * 60 + seconds) * 1000).toLong()
        }

        _uiState.value = _uiState.value.run {
            copy(timerState = TimerState.RUNNING)
        }

        timer.start(millis)
    }

    fun pauseTimer() {
        timer.pause()
        _uiState.value = _uiState.value.run {
            copy(timerState = TimerState.PAUSE)
        }
    }

    fun resumeTimer() {
        timer.resume()
        _uiState.value = _uiState.value.run {
            copy(timerState = TimerState.RUNNING)
        }
    }

    fun resetTimer() {
        timer.reset()
        _uiState.value = _uiState.value.run {
            copy(
                timerState = TimerState.READY,
                minutes = 0,
                seconds = 0,
                progress = 0f,
            )
        }
    }
}
