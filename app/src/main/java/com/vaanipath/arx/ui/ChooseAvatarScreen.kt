package com.vaanipath.arx.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaanipath.arx.model.AppLanguage
import com.vaanipath.arx.model.Avatar


@Composable
fun ChooseAvatarScreen(
    language: AppLanguage,
    initialAvatar: Avatar?,
    onBackClick: () -> Unit,
    onAvatarSelected: (Avatar) -> Unit,
    onNextClick: () -> Unit
) {

    // =========================================================
    // SELECTED AVATAR
    // =========================================================

    var selectedAvatar by rememberSaveable {

        mutableStateOf(
            initialAvatar
        )
    }


    // =========================================================
    // LANGUAGE
    // =========================================================

    val isHindi =
        language == AppLanguage.HINDI


    // =========================================================
    // MAIN SCREEN
    // =========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize(),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {


            // =====================================================
            // TOP SPACE
            // =====================================================

            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // =====================================================
            // HEADING
            // =====================================================

            Text(
                text =
                    if (isHindi) {
                        "अपना अवतार चुनें"
                    } else {
                        "Choose Your Avatar"
                    },

                fontSize = 30.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color(0xFF5D351E)
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // =====================================================
            // SUB HEADING
            // =====================================================

            Text(
                text =
                    if (isHindi) {
                        "अपनी पसंद का पात्र चुनें"
                    } else {
                        "Choose a character you like"
                    },

                fontSize = 18.sp,

                color =
                    Color(0xFF704B32)
            )


            Spacer(
                modifier = Modifier.height(40.dp)
            )


            // =====================================================
            // AVATAR ROW
            // =====================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),

                horizontalArrangement =
                    Arrangement.Center,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                // =================================================
                // BOY
                // =================================================

                AvatarCard(
                    selected =
                        selectedAvatar == Avatar.BOY,

                    name =
                        if (isHindi) {
                            "लड़का"
                        } else {
                            "Boy"
                        },

                    emoji = "👦",

                    onClick = {

                        selectedAvatar =
                            Avatar.BOY

                        onAvatarSelected(
                            Avatar.BOY
                        )
                    }
                )


                Spacer(
                    modifier = Modifier.width(35.dp)
                )


                // =================================================
                // GIRL
                // =================================================

                AvatarCard(
                    selected =
                        selectedAvatar == Avatar.GIRL,

                    name =
                        if (isHindi) {
                            "लड़की"
                        } else {
                            "Girl"
                        },

                    emoji = "👧",

                    onClick = {

                        selectedAvatar =
                            Avatar.GIRL

                        onAvatarSelected(
                            Avatar.GIRL
                        )
                    }
                )
            }


            // =====================================================
            // FLEXIBLE SPACE
            // =====================================================

            Spacer(
                modifier = Modifier.weight(1f)
            )


            // =====================================================
            // NEXT BUTTON
            // =====================================================

            Button(
                onClick = {

                    if (selectedAvatar != null) {
                        onNextClick()
                    }
                },

                enabled =
                    selectedAvatar != null,

                modifier = Modifier
                    .width(180.dp)
                    .height(58.dp),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            Color(0xFF4CAF50),

                        disabledContainerColor =
                            Color(0xFFBDBDBD)
                    )
            ) {

                Text(
                    text =
                        if (isHindi) {
                            "आगे बढ़ें →"
                        } else {
                            "Next →"
                        },

                    fontSize = 19.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            // =====================================================
            // BACK BUTTON
            // =====================================================

            Button(
                onClick =
                    onBackClick,

                modifier = Modifier
                    .width(160.dp)
                    .height(52.dp),

                shape =
                    RoundedCornerShape(16.dp),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            Color(0xFFE7A84B)
                    )
            ) {

                Text(
                    text =
                        if (isHindi) {
                            "← पीछे जाएँ"
                        } else {
                            "← Back"
                        },

                    fontSize = 17.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(0xFF4A2A16)
                )
            }


            Spacer(
                modifier = Modifier.height(15.dp)
            )
        }
    }
}


// =================================================================
// AVATAR CARD
// =================================================================

@Composable
private fun AvatarCard(
    selected: Boolean,
    name: String,
    emoji: String,
    onClick: () -> Unit
) {


    // =============================================================
    // SCALE ANIMATION
    // =============================================================

    val scale by animateFloatAsState(

        targetValue =
            if (selected) {
                1.08f
            } else {
                1f
            },

        animationSpec =
            spring(
                dampingRatio = 0.6f,
                stiffness = 500f
            ),

        label = "avatarScale"
    )


    // =============================================================
    // CARD
    // =============================================================

    Card(

        modifier = Modifier
            .size(150.dp)
            .scale(scale)

            .clickable(

                indication = null,

                interactionSource =
                    remember {
                        MutableInteractionSource()
                    }

            ) {

                onClick()
            },


        shape =
            RoundedCornerShape(28.dp),


        border =
            if (selected) {

                BorderStroke(
                    width = 5.dp,

                    color =
                        Color(0xFF00C853)
                )

            } else {

                BorderStroke(
                    width = 2.dp,

                    color =
                        Color(0xFFE0B46B)
                )
            },


        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (selected) {

                        Color(0xFFFFF1C7)

                    } else {

                        Color(0xFFFFF7E4)
                    }
            ),


        elevation =
            CardDefaults.cardElevation(

                defaultElevation =
                    if (selected) {
                        12.dp
                    } else {
                        5.dp
                    }
            )
    ) {


        Column(

            modifier =
                Modifier.fillMaxSize(),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {


            // =====================================================
            // AVATAR
            // =====================================================

            Text(
                text = emoji,

                fontSize = 72.sp
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            // =====================================================
            // NAME
            // =====================================================

            Text(
                text = name,

                fontSize = 19.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color(0xFF5D351E)
            )


            // =====================================================
            // SELECTED MARK
            // =====================================================

            if (selected) {

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text = "✓",

                    fontSize = 22.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(0xFF00A83B)
                )
            }
        }
    }
}