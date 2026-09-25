package com.vaanipath.arx.ui

import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vaanipath.arx.R
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import com.vaanipath.arx.model.AppLanguage

@Composable
fun WelcomeScreen(
    language: AppLanguage,
    onStartClick: () -> Unit
){

    // =====================================================
    // DEVICE CONFIGURATION
    // =====================================================

    val configuration = LocalConfiguration.current

    val isLandscape =
        configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE

    val isTablet =
        configuration.screenWidthDp >= 600


    // =====================================================
    // BACKGROUND
    // =====================================================

    val backgroundRes = when {

        isTablet && isLandscape ->
            R.drawable.bg_welcome_tablet_landscape

        isTablet ->
            R.drawable.bg_welcome_tablet_portrait

        isLandscape ->
            R.drawable.bg_welcome_phone_landscape

        else ->
            R.drawable.bg_welcome_phone_portrait
    }


    // =====================================================
    // ENTRY ANIMATION
    // =====================================================

    var showContent by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        delay(100)

        showContent = true
    }


    // =====================================================
    // START BUTTON PRESS STATE
    // =====================================================

    var startPressed by remember {
        mutableStateOf(false)
    }


    // =====================================================
    // START BUTTON BOUNCE
    // =====================================================

    val startScale by animateFloatAsState(

        targetValue =
            if (startPressed) {
                0.90f
            } else {
                1.0f
            },

        animationSpec = spring(
            dampingRatio =
                Spring.DampingRatioMediumBouncy,

            stiffness =
                Spring.StiffnessLow
        ),

        label = "startButtonScale"
    )


    // =====================================================
    // NAVIGATION AFTER PRESS
    // =====================================================

    LaunchedEffect(startPressed) {

        if (startPressed) {

            delay(180)

            onStartClick()

            startPressed = false
        }
    }


    // =====================================================
    // SUBTLE WELCOME FLOAT
    // =====================================================

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "welcomeFloat"
        )

    val welcomeFloat by
    infiniteTransition.animateFloat(

        initialValue = -3f,

        targetValue = 3f,

        animationSpec =
            infiniteRepeatable(

                animation =
                    tween(
                        durationMillis = 1800,
                        easing =
                            FastOutSlowInEasing
                    ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = "welcomeFloatValue"
    )


    // =====================================================
    // ROOT
    // =====================================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        // =================================================
        // BACKGROUND
        // =================================================

        Image(

            painter =
                painterResource(
                    id = backgroundRes
                ),

            contentDescription = null,

            modifier =
                Modifier.fillMaxSize(),

            contentScale =
                ContentScale.Crop
        )


        // =================================================
        // MAIN CONTENT
        // =================================================

        if (showContent) {

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(

                            horizontal =
                                if (isTablet) {
                                    32.dp
                                } else {
                                    12.dp
                                },

                            vertical =
                                if (isLandscape) {
                                    8.dp
                                } else {
                                    16.dp
                                }
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {


                // =================================================
                // 1. VAANIPATH LOGO
                // =================================================

                Image(

                   painter =
                       painterResource(
                        id = when (language) {
                            AppLanguage.ENGLISH ->
                                R.drawable.logo_english

                            AppLanguage.HINDI ->
                                R.drawable.logo_hindi
                        }
                    ),

                    contentDescription =
                        "VaaniPath",

                    modifier =
                        Modifier
                            .offset(
                                y = if (isLandscape) {
                                    (-20).dp
                                } else {
                                    (-120).dp
                                }
                            )
                            .fillMaxWidth(

                                when {

                                    // Tablet Landscape
                                    isTablet &&
                                            isLandscape ->
                                        0.45f

                                    // Tablet Portrait
                                    isTablet ->
                                        0.72f

                                    // Phone Landscape
                                    isLandscape ->
                                        0.48f

                                    // Phone Portrait
                                    else ->
                                        0.88f
                                }
                            )

                            .heightIn(

                                max =
                                    when {

                                        isTablet &&
                                                isLandscape ->
                                            120.dp

                                        isTablet ->
                                            190.dp

                                        isLandscape ->
                                            105.dp

                                        else ->
                                            190.dp
                                    }
                            ),

                    contentScale =
                        ContentScale.Fit
                )


                // =================================================
                // 2. WELCOME PNG
                // =================================================

                Image(

                    painter =
                        painterResource(
                            id = when (language) {
                                AppLanguage.ENGLISH ->
                                    R.drawable.welcome_bubble_english

                                AppLanguage.HINDI ->
                                    R.drawable.welcome_bubble_hindi
                            }
                        ),

                    contentDescription = "Welcome",

                    modifier =
                        Modifier

                            // Subtle floating movement
                            .padding(
                                top =
                                    if (isLandscape) {
                                        2.dp
                                    } else {
                                        5.dp
                                    }
                            )

                            // Slight permanent zoom + floating animation
                            .scale(
                                1.15f +
                                        (welcomeFloat / 300f)
                            )

                            // Responsive width
                            .fillMaxWidth(
                                when {

                                    // Tablet Landscape
                                    isTablet &&
                                            isLandscape ->
                                        0.55f

                                    // Tablet Portrait
                                    isTablet ->
                                        0.78f

                                    // Phone Landscape
                                    isLandscape ->
                                        0.58f

                                    // Phone Portrait
                                    else ->
                                        0.92f
                                }
                            )

                            // Responsive maximum height
                            .heightIn(
                                max =
                                    when {

                                        // Tablet Landscape
                                        isTablet &&
                                                isLandscape ->
                                            140.dp

                                        // Tablet Portrait
                                        isTablet ->
                                            225.dp

                                        // Phone Landscape
                                        isLandscape ->
                                            135.dp

                                        // Phone Portrait
                                        else ->
                                            220.dp
                                    }
                            ),

                    contentScale = ContentScale.Fit
                )


                // =================================================
                // 3. START PNG
                // =================================================

                Image(

                    painter =
                        painterResource(
                            id = when (language) {
                                AppLanguage.ENGLISH ->
                                    R.drawable.button_start_english

                                AppLanguage.HINDI ->
                                    R.drawable.button_start_hindi
                            }
                        ),

                    contentDescription =
                        "Start",

                    modifier =
                        Modifier
                            .offset(
                                y = if (isLandscape) {
                                    0.dp
                                } else {
                                    (80).dp
                                }
                            )

                            .padding(

                                top =
                                    if (isLandscape) {
                                        5.dp
                                    } else {
                                        12.dp
                                    }
                            )

                            .scale(startScale)

                            .widthIn(

                                min =
                                    if (isLandscape) {
                                        180.dp
                                    } else {
                                        220.dp
                                    },

                                max =
                                    when {

                                        isTablet &&
                                                isLandscape ->
                                            280.dp

                                        isTablet ->
                                            340.dp

                                        isLandscape ->
                                            260.dp

                                        else ->
                                            320.dp
                                    }
                            )

                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                startPressed = true
                            },
                    contentScale =
                        ContentScale.Fit
                )
            }
        }
    }
}