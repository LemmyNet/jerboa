package com.jerboa.ui.theme

import androidx.compose.material.ContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.jerboa.colorShade

val Blue200 = Color(0xFF3498db)
val Blue500 = Color(0xFF21608B)
val Blue700 = Color(0xFF1A4A69)
val Green200 = Color(0xFF00bc8c)
val Green500 = Color(0xFF018564)
val Green700 = Color(0xFF004634)
val Yellow40 = Color(0xff7d5700)

val DarkSurfaceBlue = Color(0xFF0e1d29)
val DarkBackgroundBlue = colorShade(DarkSurfaceBlue, 1f)

val Color.muted @Composable get() = this.copy(alpha = ContentAlpha.medium)
