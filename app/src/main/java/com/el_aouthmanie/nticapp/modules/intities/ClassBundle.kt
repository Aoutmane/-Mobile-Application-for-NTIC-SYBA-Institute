package com.el_aouthmanie.nticapp.modules.intities

import androidx.compose.ui.graphics.Color

data class ClassBundle(

    val name: String,
    val chapter: String,
    val room: String,
    val teacher: String,
    val bgColor: Color,

    val day: String,
    val startingHour: String,
    val endingHour: String
)