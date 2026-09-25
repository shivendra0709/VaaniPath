package com.vaanipath.arx.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vaanipath.arx.R
import com.vaanipath.arx.model.AppLanguage
import kotlinx.coroutines.delay

@Composable
fun LanguageSelectionScreen(
    onBackClick: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit
) {

    // ============================================================
    // DEVICE CONFIGURATION
    // ============================================================

    val configuration = LocalConfiguration.current

    val isLandscape =
        configuration.orientation ==
                Configuration.ORIENTATION_LANDSCAPE

    val isTablet =
        configuration.screenWidthDp >= 600


    // ============================================================
    // SELECTED LANGUAGE
    // ============================================================

    var selectedLanguage by rememberSaveable {
        mutableStateOf<AppLanguage?>(null)
    }


    // ============================================================
    // NEXT BUTTON PULSE
    // ============================================================

    val nextTransition =
        rememberInfiniteTransition(
            label = "nextAttention"
        )

    val nextPulse by nextTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 900
                    ),
                repeatMode =
                    RepeatMode.Reverse
            ),
        label = "nextPulse"
    )


    // ============================================================
    // ROOT
    // ============================================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // ========================================================
        // BACKGROUND
        // ========================================================

        Image(
            painter =
                painterResource(
                    id = when {
                        isTablet && isLandscape ->
                            R.drawable.bg_welcome_tablet_landscape

                        isTablet ->
                            R.drawable.bg_welcome_tablet_portrait

                        isLandscape ->
                            R.drawable.bg_welcome_phone_landscape

                        else ->
                            R.drawable.bg_welcome_phone_portrait
                    }
                ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


        // ========================================================
        // HEADING
        // ========================================================

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top =
                            when {
                                isTablet && isLandscape -> 16.dp
                                isTablet -> 22.dp
                                isLandscape -> 8.dp
                                else -> 16.dp
                            }
                    ),
            contentAlignment = Alignment.TopCenter
        ) {

            AnimatedContent(
                targetState = selectedLanguage,

                transitionSpec = {

                    (
                            fadeIn(
                                animationSpec =
                                    tween(250)
                            ) +
                                    scaleIn(
                                        initialScale = 0.92f,
                                        animationSpec =
                                            tween(250)
                                    )
                            ) togetherWith
                            fadeOut(
                                animationSpec =
                                    tween(150)
                            )
                },

                label = "languageHeadingChange"

            ) { language ->

                Image(
                    painter =
                        painterResource(
                            id =
                                when (language) {

                                    AppLanguage.HINDI ->
                                        R.drawable.choose_language_hindi

                                    AppLanguage.ENGLISH ->
                                        R.drawable.choose_language_english

                                    null ->
                                        R.drawable.choose_language_english
                                }
                        ),

                    contentDescription =
                        "Choose language",

                    modifier =
                        Modifier
                            .offset(
                                y =
                                    if (isLandscape) {
                                        0.dp
                                    } else {
                                        100.dp
                                    }
                            )
                            .fillMaxWidth(
                                when {
                                    isTablet && isLandscape ->
                                        0.60f

                                    isTablet ->
                                        0.75f

                                    isLandscape ->
                                        0.60f

                                    else ->
                                        1f
                                }
                            )
                            .height(
                                when {
                                    isTablet && isLandscape ->
                                        105.dp

                                    isTablet ->
                                        145.dp

                                    isLandscape ->
                                        90.dp

                                    else ->
                                        135.dp
                                }
                            ),

                    contentScale = ContentScale.Fit
                )
            }
        }


        // ========================================================
        // LANGUAGE OPTIONS
        // ========================================================

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top =
                            when {
                                isTablet && isLandscape -> 145.dp
                                isTablet -> 185.dp
                                isLandscape -> 125.dp
                                else -> 205.dp
                            }
                    ),

            contentAlignment = Alignment.TopCenter
        ) {

            // ====================================================
            // PORTRAIT
            // ====================================================

            if (!isLandscape) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.spacedBy(
                            if (isTablet) 5.dp else 3.dp
                        )
                ) {

                    // ==================================================
                    // ENGLISH
                    // ==================================================

                    LanguageButton(
                        imageRes =
                            R.drawable.language_button_english,

                        selected =
                            selectedLanguage ==
                                    AppLanguage.ENGLISH,

                        modifier =
                            Modifier
                                .offset(
                                    y = 80.dp
                                )
                                .width(
                                    if (isTablet)
                                        245.dp
                                    else
                                        175.dp
                                )
                                .height(
                                    if (isTablet)
                                        145.dp
                                    else
                                        125.dp
                                ),

                        onClick = {

                            selectedLanguage =
                                AppLanguage.ENGLISH

                            onLanguageSelected(
                                AppLanguage.ENGLISH
                            )
                        }
                    )


                    // ==================================================
                    // HINDI
                    // ==================================================

                    LanguageButton(
                        imageRes =
                            R.drawable.language_button_hindi,

                        selected =
                            selectedLanguage ==
                                    AppLanguage.HINDI,

                        modifier =
                            Modifier
                                .offset(
                                    y = 100.dp
                                )
                                .width(
                                    if (isTablet)
                                        245.dp
                                    else
                                        175.dp
                                )
                                .height(
                                    if (isTablet)
                                        145.dp
                                    else
                                        125.dp
                                ),

                        onClick = {

                            selectedLanguage =
                                AppLanguage.HINDI

                            onLanguageSelected(
                                AppLanguage.HINDI
                            )
                        }
                    )
                }

            } else {

                // ====================================================
                // LANDSCAPE
                // ====================================================

                Row(
                    modifier =
                        Modifier.fillMaxWidth(
                            if (isTablet)
                                0.50f
                            else
                                0.54f
                        ),

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    // ==================================================
                    // HINDI
                    // ==================================================

                    LanguageButton(
                        imageRes =
                            R.drawable.language_button_hindi,

                        selected =
                            selectedLanguage ==
                                    AppLanguage.HINDI,

                        modifier =
                            Modifier
                                .width(
                                    if (isTablet)
                                        180.dp
                                    else
                                        170.dp
                                )
                                .height(
                                    if (isTablet)
                                        105.dp
                                    else
                                        95.dp
                                ),

                        onClick = {

                            selectedLanguage =
                                AppLanguage.HINDI

                            onLanguageSelected(
                                AppLanguage.HINDI
                            )
                        }
                    )


                    // ==================================================
                    // ENGLISH
                    // ==================================================

                    LanguageButton(
                        imageRes =
                            R.drawable.language_button_english,

                        selected =
                            selectedLanguage ==
                                    AppLanguage.ENGLISH,

                        modifier =
                            Modifier
                                .width(
                                    if (isTablet)
                                        180.dp
                                    else
                                        170.dp
                                )
                                .height(
                                    if (isTablet)
                                        105.dp
                                    else
                                        95.dp
                                ),

                        onClick = {

                            selectedLanguage =
                                AppLanguage.ENGLISH

                            onLanguageSelected(
                                AppLanguage.ENGLISH
                            )
                        }
                    )
                }
            }
        }


        // ========================================================
        // BOTTOM NAVIGATION
        // ========================================================

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom =
                            when {
                                isTablet && !isLandscape ->
                                    35.dp

                                !isLandscape ->
                                    30.dp

                                else ->
                                    10.dp
                            }
                    ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // ====================================================
            // NEXT BUTTON
            // ====================================================

            AnimatedContent(
                targetState = selectedLanguage,

                transitionSpec = {

                    (
                            fadeIn(
                                animationSpec =
                                    tween(300)
                            ) +
                                    scaleIn(
                                        initialScale = 0.75f,
                                        animationSpec =
                                            spring(
                                                dampingRatio =
                                                    Spring.DampingRatioMediumBouncy
                                            )
                                    )
                            ) togetherWith
                            fadeOut(
                                animationSpec =
                                    tween(150)
                            )
                },

                label = "nextButtonChange"

            ) { language ->

                if (language != null) {

                    NextButton(
                        language = language,

                        modifier =
                            Modifier
                                .width(
                                    when {
                                        isTablet && isLandscape ->
                                            150.dp

                                        isTablet ->
                                            205.dp

                                        isLandscape ->
                                            145.dp

                                        else ->
                                            165.dp
                                    }
                                )
                                .height(
                                    when {
                                        isTablet && !isLandscape ->
                                            80.dp

                                        isLandscape ->
                                            55.dp

                                        else ->
                                            75.dp
                                    }
                                )
                                .scale(nextPulse),

                        onClick = {

                            // NEXT = SELECTED LANGUAGE
                            onLanguageSelected(language)
                        }
                    )
                }
            }


            // ====================================================
            // GAP
            // ====================================================

            Spacer(
                modifier =
                    Modifier.height(
                        if (isLandscape) 7.dp else 9.dp
                    )
            )


            // ====================================================
            // BACK BUTTON
            // ====================================================

            BackButton(
                language =
                    selectedLanguage
                        ?: AppLanguage.ENGLISH,

                modifier =
                    Modifier
                        .width(
                            when {
                                isTablet && isLandscape ->
                                    125.dp

                                isTablet ->
                                    175.dp

                                isLandscape ->
                                    120.dp

                                else ->
                                    145.dp
                            }
                        )
                        .height(
                            when {
                                isTablet && !isLandscape ->
                                    70.dp

                                isLandscape ->
                                    48.dp

                                else ->
                                    65.dp
                            }
                        ),

                onClick = onBackClick
            )
        }
    }
}


// =================================================================
// LANGUAGE BUTTON
// =================================================================

@Composable
private fun LanguageButton(
    imageRes: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    var pressed by remember {
        mutableStateOf(false)
    }


    // =============================================================
    // SCALE ANIMATION
    // =============================================================

    val scale by animateFloatAsState(

        targetValue =
            when {
                pressed -> 0.92f
                selected -> 1.08f
                else -> 0.96f
            },

        animationSpec =
            spring(
                dampingRatio =
                    Spring.DampingRatioMediumBouncy,

                stiffness =
                    Spring.StiffnessMedium
            ),

        label = "languageSelectionScale"
    )


    // =============================================================
    // ALPHA ANIMATION
    // =============================================================

    val alpha by animateFloatAsState(

        targetValue =
            if (
                selected ||
                pressed
            ) {
                1f
            } else {
                0.72f
            },

        animationSpec =
            tween(200),

        label = "languageSelectionAlpha"
    )


    // =============================================================
    // BUTTON
    // =============================================================

    Box(
        modifier =
            modifier
                .scale(scale)
                .alpha(alpha)
                .clickable(
                    indication = null,

                    interactionSource =
                        remember {
                            MutableInteractionSource()
                        }
                ) {

                    pressed = true

                    onClick()
                },

        contentAlignment =
            Alignment.Center
    ) {

        Image(
            painter =
                painterResource(
                    id = imageRes
                ),

            contentDescription = null,

            modifier =
                Modifier.fillMaxSize(),

            contentScale =
                ContentScale.Fit
        )
    }


    // =============================================================
    // PRESS RESET
    // =============================================================

    LaunchedEffect(pressed) {

        if (pressed) {

            delay(160)

            pressed = false
        }
    }
}


// =================================================================
// NEXT BUTTON
// =================================================================

@Composable
private fun NextButton(
    language: AppLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier =
            modifier.clickable(
                indication = null,

                interactionSource =
                    remember {
                        MutableInteractionSource()
                    }
            ) {
                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        Image(
            painter =
                painterResource(
                    id =
                        when (language) {

                            AppLanguage.HINDI ->
                                R.drawable.next_hindi

                            AppLanguage.ENGLISH ->
                                R.drawable.next_english
                        }
                ),

            contentDescription =
                if (language == AppLanguage.HINDI)
                    "आगे बढ़ें"
                else
                    "Next",

            modifier =
                Modifier.fillMaxSize(),

            contentScale =
                ContentScale.Fit
        )
    }
}


// =================================================================
// BACK BUTTON
// =================================================================

@Composable
private fun BackButton(
    language: AppLanguage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier =
            modifier.clickable(
                indication = null,

                interactionSource =
                    remember {
                        MutableInteractionSource()
                    }
            ) {
                onClick()
            },

        contentAlignment =
            Alignment.Center
    ) {

        Image(
            painter =
                painterResource(
                    id =
                        when (language) {

                            AppLanguage.HINDI ->
                                R.drawable.back_hindi

                            AppLanguage.ENGLISH ->
                                R.drawable.back_english
                        }
                ),

            contentDescription =
                if (language == AppLanguage.HINDI)
                    "पीछे जाएँ"
                else
                    "Back",

            modifier =
                Modifier.fillMaxSize(),

            contentScale =
                ContentScale.Fit
        )
    }
}