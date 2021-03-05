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
package com.pradeep.jetcounter.timer

import android.os.CountDownTimer
import kotlin.math.ceil

class TimerTask(
    private val onTick: (Int, Int, Float) -> Unit,
    private val onFinish: () -> Unit
) {
    private var timer: CountDownTimer? = null

    private var totalTime: Long = 0

    private var left: Long = 0

    fun start(count: Long) {
        totalTime = count
        startTimer(count)
        timer?.start()
    }

    fun pause() {
        timer?.cancel()
        timer = null
    }

    fun resume() {
        startTimer(left)
    }

    fun reset() {
        pause()
        left = 0
    }

    private fun startTimer(count: Long) {
        timer = object : CountDownTimer(count, 1) {
            override fun onTick(millisUntilFinished: Long) {
                val tickSeconds = ceil(millisUntilFinished / 1000.0).toInt()
                val minutes = tickSeconds / 60
                val seconds = tickSeconds - (minutes * 60)
                val progress = millisUntilFinished.toFloat() / totalTime.toFloat()
                onTick.invoke(minutes, seconds, progress)
                left = millisUntilFinished
            }

            override fun onFinish() {
                onFinish.invoke()
            }
        }.apply {
            start()
        }
    }
}
