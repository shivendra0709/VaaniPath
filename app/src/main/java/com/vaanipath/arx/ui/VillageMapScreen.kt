package com.vaanipath.arx.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.vaanipath.arx.model.AppLanguage
import com.vaanipath.arx.model.AssessmentLevel


@Composable
fun VillageMapScreen(

    language: AppLanguage,

    highestUnlockedLevel: Int,

    onBackClick: () -> Unit,

    onLevelClick: (AssessmentLevel) -> Unit
) {

    // =========================================
    // SELECTED LEVEL
    // =========================================

    var selectedLevel by remember {

        mutableStateOf(
            AssessmentLevel.LEVEL_1
        )
    }


    val isHindi =
        language == AppLanguage.HINDI


    // =========================================
    // LEVEL DATA
    // =========================================

    val levels = listOf(

        Triple(
            AssessmentLevel.LEVEL_1,
            "🌱",
            if (isHindi)
                "शुरुआत"
            else
                "Start"
        ),

        Triple(
            AssessmentLevel.LEVEL_2,
            "🌳",
            if (isHindi)
                "अभ्यास"
            else
                "Practice"
        ),

        Triple(
            AssessmentLevel.LEVEL_3,
            "🏆",
            if (isHindi)
                "चुनौती"
            else
                "Challenge"
        )
    )


    // =========================================
    // MAIN UI
    // =========================================

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {


        // =========================================
        // TITLE
        // =========================================

        Text(

            text =
                if (isHindi)
                    "वाणी गाँव"
                else
                    "Vaani Village",

            style =
                MaterialTheme.typography.headlineLarge,

            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(8.dp)
        )


        Text(

            text =
                if (isHindi)
                    "अपना सीखने का स्तर चुनें"
                else
                    "Choose your learning level",

            style =
                MaterialTheme.typography.bodyLarge
        )


        Spacer(
            modifier =
                Modifier.height(28.dp)
        )


        // =========================================
        // LEVEL CARDS
        // =========================================

        levels.forEachIndexed { index, item ->

            val level =
                item.first

            val emoji =
                item.second

            val title =
                item.third


            val levelNumber =
                index + 1


            // Is this level unlocked?
            val isUnlocked =
                levelNumber <= highestUnlockedLevel


            // Is this selected?
            val isSelected =
                selectedLevel == level


            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = isUnlocked
                    ) {

                        selectedLevel =
                            level
                    },

                shape =
                    RoundedCornerShape(20.dp),

                border =

                    if (
                        isSelected &&
                        isUnlocked
                    ) {

                        BorderStroke(
                            3.dp,
                            MaterialTheme
                                .colorScheme
                                .primary
                        )

                    } else {

                        null
                    }
            ) {


                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    // =========================================
                    // EMOJI
                    // =========================================

                    Text(

                        text =
                            emoji,

                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium
                    )


                    Spacer(
                        modifier =
                            Modifier.width(16.dp)
                    )


                    // =========================================
                    // TEXT
                    // =========================================

                    Column(

                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(

                            text =
                                if (isHindi)
                                    "स्तर $levelNumber"
                                else
                                    "Level $levelNumber",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Text(

                            text =
                                title,

                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium
                        )


                        // Locked message

                        if (!isUnlocked) {

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )


                            Text(

                                text =
                                    if (isHindi)
                                        "पिछला स्तर पूरा करें"
                                    else
                                        "Complete previous level",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }
                    }


                    // =========================================
                    // LOCK ICON
                    // =========================================

                    Text(

                        text =
                            if (isUnlocked)
                                "🔓"
                            else
                                "🔒",

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )
        }


        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        // =========================================
        // START BUTTON
        // =========================================

        val selectedLevelNumber =
            selectedLevel.ordinal + 1


        val selectedLevelUnlocked =
            selectedLevelNumber <=
                    highestUnlockedLevel


        Button(

            enabled =
                selectedLevelUnlocked,

            onClick = {

                onLevelClick(
                    selectedLevel
                )
            }
        ) {

            Text(

                text =
                    if (isHindi)
                        "शुरू करें"
                    else
                        "Start Level"
            )
        }


        Spacer(
            modifier =
                Modifier.height(12.dp)
        )


        // =========================================
        // BACK BUTTON
        // =========================================

        OutlinedButton(

            onClick =
                onBackClick
        ) {

            Text(

                text =
                    if (isHindi)
                        "वापस"
                    else
                        "Back"
            )
        }
    }
}