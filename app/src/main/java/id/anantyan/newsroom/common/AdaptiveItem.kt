package id.anantyan.newsroom.common

import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager

fun Int.dpToPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()
}

fun WindowManager.calculateSpanCount(): Int {
    val displayMetrics = DisplayMetrics()
    @Suppress("DEPRECATION") defaultDisplay.getMetrics(displayMetrics)
    val screenWidth = displayMetrics.widthPixels

    return when {
        screenWidth >= 1400.dpToPx() -> 6
        screenWidth >= 1200.dpToPx() -> 5
        screenWidth >= 840.dpToPx() -> 4
        screenWidth >= 600.dpToPx() -> 3
        else -> 2
    }
}