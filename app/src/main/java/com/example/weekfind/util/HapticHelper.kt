package com.example.weekfind.util

import android.view.HapticFeedbackConstants
import android.view.View

object HapticHelper {

    fun performLightTick(view: View?, enabled: Boolean) {
        if (!enabled || view == null) return
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
}
