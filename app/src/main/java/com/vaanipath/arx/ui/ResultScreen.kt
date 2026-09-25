package com.vaanipath.arx.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaanipath.arx.model.AppLanguage


@Composable
fun ResultScreen(
    language: AppLanguage,
    onContinueClick: () -> Unit,
    onBackClick: () -> Unit
) {

    val isHindi =
        language == AppLanguage.HINDI


    // =========================================================
    // TEMPORARY RESULT
    // =========================================================

    // Abhi dummy result.
    // Later Speech Analyzer se dynamically aayega.

    val score = 80

    val stars = 4


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


            // =================================================
            // TOP SPACE
            // =================================================

            Spacer(
                modifier = Modifier.height(25.dp)
            )


            // =================================================
            // TITLE
            // =================================================

            Text(
                text =
                    if (isHindi) {
                        "🎉 बहुत बढ़िया!"
                    } else {
                        "🎉 Great Job!"
                    },

                fontSize = 34.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color(0xFF5D351E),

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Text(
                text =
                    if (isHindi) {
                        "आपने गतिविधि पूरी कर ली!"
                    } else {
                        "You completed the activity!"
                    },

                fontSize = 19.sp,

                color =
                    Color(0xFF704B32),

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier = Modifier.height(35.dp)
            )


            // =================================================
            // SCORE CARD
            // =================================================

            Card(

                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(230.dp),

                shape =
                    RoundedCornerShape(30.dp),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            Color(0xFFFFF7E4)
                    ),

                border =
                    BorderStroke(
                        3.dp,
                        Color(0xFFE0B46B)
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 8.dp
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


                    // -----------------------------------------
                    // STARS
                    // -----------------------------------------

                    Text(
                        text =
                            "⭐".repeat(stars),

                        fontSize = 38.sp
                    )


                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )


                    // -----------------------------------------
                    // SCORE
                    // -----------------------------------------

                    Text(
                        text =
                            "$score%",

                        fontSize = 48.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF4CAF50)
                    )


                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )


                    Text(
                        text =
                            if (isHindi) {
                                "आपका स्कोर"
                            } else {
                                "Your Score"
                            },

                        fontSize = 18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            Color(0xFF704B32)
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(30.dp)
            )


            // =================================================
            // FEEDBACK
            // =================================================

            Card(

                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(105.dp),

                shape =
                    RoundedCornerShape(24.dp),

                colors =
                    CardDefaults.cardColors(

                        containerColor =
                            Color(0xFFE8F5E9)
                    )
            ) {

                Box(

                    modifier =
                        Modifier.fillMaxSize(),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            if (isHindi) {

                                "🌟 शानदार! ऐसे ही अभ्यास करते रहें।"

                            } else {

                                "🌟 Excellent! Keep practicing like this."
                            },

                        fontSize = 17.sp,

                        fontWeight =
                            FontWeight.Medium,

                        color =
                            Color(0xFF2E7D32),

                        textAlign =
                            TextAlign.Center,

                        modifier =
                            Modifier.padding(16.dp)
                    )
                }
            }


            // =================================================
            // FLEXIBLE SPACE
            // =================================================

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            // =================================================
            // CONTINUE
            // =================================================

            Button(

                onClick =
                    onContinueClick,

                modifier = Modifier
                    .width(190.dp)
                    .height(58.dp),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    ButtonDefaults.buttonColors(

                        containerColor =
                            Color(0xFF4CAF50)
                    )
            ) {

                Text(
                    text =
                        if (isHindi) {
                            "आगे बढ़ें →"
                        } else {
                            "Continue →"
                        },

                    fontSize = 19.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            // =================================================
            // BACK
            // =================================================

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
                modifier =
                    Modifier.height(15.dp)
            )
        }
    }
}