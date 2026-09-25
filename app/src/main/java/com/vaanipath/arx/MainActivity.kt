package com.vaanipath.arx

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable

import com.vaanipath.arx.model.AppLanguage
import com.vaanipath.arx.model.AssessmentLevel
import com.vaanipath.arx.model.Avatar

import com.vaanipath.arx.ui.AssessmentScreen
import com.vaanipath.arx.ui.ChooseAvatarScreen
import com.vaanipath.arx.ui.LanguageSelectionScreen
import com.vaanipath.arx.ui.ResultScreen
import com.vaanipath.arx.ui.VillageMapScreen
import com.vaanipath.arx.ui.WelcomeScreen

import com.vaanipath.arx.theme.VaaniPathTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        // =========================================================
        // SHARED PREFERENCES
        // =========================================================

        val prefs =
            getSharedPreferences(
                "VaaniPathPrefs",
                Context.MODE_PRIVATE
            )


        // =========================================================
        // SAVED DATA
        // =========================================================

        val savedLanguage =
            prefs.getString(
                "selected_language",
                null
            )

        val savedAvatar =
            prefs.getString(
                "selected_avatar",
                null
            )

        val savedHighestLevel =
            prefs.getInt(
                "highest_unlocked_level",
                1
            )


        // =========================================================
        // COMPOSE
        // =========================================================

        setContent {

            // =====================================================
            // CURRENT SCREEN
            // =====================================================

            var currentScreen by rememberSaveable {
                mutableStateOf(
                    "welcome"
                )
            }


            // =====================================================
            // LANGUAGE
            // =====================================================

            var selectedLanguage by rememberSaveable {

                mutableStateOf(

                    when (savedLanguage) {

                        "HINDI" ->
                            AppLanguage.HINDI

                        else ->
                            AppLanguage.ENGLISH
                    }
                )
            }


            // =====================================================
            // AVATAR
            // =====================================================

            var selectedAvatar by rememberSaveable {

                mutableStateOf(

                    when (savedAvatar) {

                        "GIRL" ->
                            Avatar.GIRL

                        else ->
                            Avatar.BOY
                    }
                )
            }


            // =====================================================
            // SELECTED LEVEL
            // =====================================================

            var selectedLevel by rememberSaveable {

                mutableStateOf(
                    AssessmentLevel.LEVEL_1
                )
            }


            // =====================================================
            // HIGHEST UNLOCKED LEVEL
            // =====================================================

            var highestUnlockedLevel by rememberSaveable {

                mutableStateOf(
                    savedHighestLevel
                )
            }


            // =====================================================
            // THEME
            // =====================================================

            VaaniPathTheme {

                when (currentScreen) {


                    // =================================================
                    // WELCOME
                    // =================================================

                    "welcome" -> {

                        BackHandler {
                            finish()
                        }

                        WelcomeScreen(
                            language = selectedLanguage,
                            onStartClick = {
                                currentScreen = "language"
                            }
                        )
                    }


                    // =================================================
                    // LANGUAGE
                    // =================================================

                    "language" -> {

                        BackHandler {

                            currentScreen =
                                "welcome"
                        }


                        LanguageSelectionScreen(

                            onBackClick = {

                                currentScreen =
                                    "welcome"
                            },


                            onLanguageSelected = {
                                    language ->

                                selectedLanguage =
                                    language


                                prefs.edit()
                                    .putString(
                                        "selected_language",
                                        language.name
                                    )
                                    .apply()


                                currentScreen =
                                    "avatar"
                            }
                        )
                    }


                    // =================================================
                    // AVATAR
                    // =================================================

                    "avatar" -> {

                        BackHandler {

                            currentScreen =
                                "language"
                        }


                        ChooseAvatarScreen(

                            language =
                                selectedLanguage,


                            initialAvatar =
                                selectedAvatar,


                            onBackClick = {

                                currentScreen =
                                    "language"
                            },


                            onAvatarSelected = {
                                    avatar ->

                                selectedAvatar =
                                    avatar


                                prefs.edit()
                                    .putString(
                                        "selected_avatar",
                                        avatar.name
                                    )
                                    .apply()
                            },


                            onNextClick = {

                                currentScreen =
                                    "village"
                            }
                        )
                    }


                    // =================================================
                    // VILLAGE
                    // =================================================

                    "village" -> {

                        BackHandler {

                            currentScreen =
                                "avatar"
                        }


                        VillageMapScreen(

                            language =
                                selectedLanguage,


                            highestUnlockedLevel =
                                highestUnlockedLevel,


                            onBackClick = {

                                currentScreen =
                                    "avatar"
                            },


                            onLevelClick = {
                                    level ->

                                selectedLevel =
                                    level


                                currentScreen =
                                    "assessment"
                            }
                        )
                    }


                    // =================================================
                    // ASSESSMENT
                    // =================================================

                    "assessment" -> {

                        BackHandler {

                            currentScreen =
                                "village"
                        }


                        AssessmentScreen(

                            language =
                                selectedLanguage,


                            level =
                                selectedLevel,


                            onBackClick = {

                                currentScreen =
                                    "village"
                            },


                            onComplete = {

                                // =====================================
                                // COMPLETED LEVEL
                                // =====================================

                                val completedLevel =
                                    selectedLevel.ordinal + 1


                                // =====================================
                                // NEXT LEVEL
                                // =====================================

                                val nextLevel =
                                    completedLevel + 1


                                // =====================================
                                // UNLOCK NEXT LEVEL
                                // =====================================

                                if (
                                    nextLevel >
                                    highestUnlockedLevel &&

                                    nextLevel <= 3
                                ) {

                                    highestUnlockedLevel =
                                        nextLevel


                                    prefs.edit()
                                        .putInt(
                                            "highest_unlocked_level",
                                            nextLevel
                                        )
                                        .apply()
                                }


                                // =====================================
                                // RESULT
                                // =====================================

                                currentScreen =
                                    "result"
                            }
                        )
                    }


                    // =================================================
                    // RESULT
                    // =================================================

                    "result" -> {

                        BackHandler {

                            currentScreen =
                                "village"
                        }


                        ResultScreen(

                            language =
                                selectedLanguage,


                            onContinueClick = {

                                currentScreen =
                                    "village"
                            },


                            onBackClick = {

                                currentScreen =
                                    "assessment"
                            }
                        )
                    }
                }
            }
        }
    }
}