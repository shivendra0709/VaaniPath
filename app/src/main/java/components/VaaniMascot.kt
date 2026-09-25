package com.vaanipath.arx.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.vaanipath.arx.R
import kotlinx.coroutines.delay


@Composable
fun VaaniMascot(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    // ------------------------------------------------
    // TOUCH STATE
    // ------------------------------------------------

    var isPressed by remember {
        mutableStateOf(false)
    }


    // ------------------------------------------------
    // SCALE ANIMATION
    // ------------------------------------------------

    val scale by animateFloatAsState(

        targetValue =
            if (isPressed) {
                1.15f
            } else {
                1.0f
            },

        animationSpec = spring(
            dampingRatio =
                Spring.DampingRatioMediumBouncy,

            stiffness =
                Spring.StiffnessLow
        ),

        label = "vaaniTouchScale"
    )


    // ------------------------------------------------
    // VAANI IMAGE
    // ------------------------------------------------

    Image(

        painter = painterResource(
            id = R.drawable.vaani_idle
        ),

        contentDescription =
            "Vaani mascot",

        modifier = modifier
            .scale(scale)
            .clickable {

                isPressed = true

                onClick()
            }
    )


    // ------------------------------------------------
    // RESET AFTER ANIMATION
    // ------------------------------------------------

    LaunchedEffect(isPressed) {

        if (isPressed) {

            delay(220)

            isPressed = false
        }
    }
}