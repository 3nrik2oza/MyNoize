/*
* Converted using https://composables.com/svg-to-compose
*/
package com.composables

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val icPause: ImageVector
    get() {
        if (_icPause != null) return _icPause!!
        
        _icPause = ImageVector.Builder(
            name = "icPause",
            defaultWidth = 800.dp,
            defaultHeight = 800.dp,
            viewportWidth = 478.125f,
            viewportHeight = 478.125f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color(0xFF000000))
                ) {
                }
            }
        }.build()
        
        return _icPause!!
    }

private var _icPause: ImageVector? = null

