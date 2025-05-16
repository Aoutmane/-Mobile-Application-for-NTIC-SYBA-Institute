package com.el_aouthmanie.nticapp.globals

import com.el_aouthmanie.nticapp.R

object CONSTANTS {

    object Screens {
        val LAUNCHING = "launch"
        val LOGING = "login"
        val HOME = "home"
        val SCHEDULE = "schedule"
        val ANNOUNCMENTS = "announcments"
        val ABOUT = "about"

    }

    object roles {
        val ADMIN = "admin"
        val TRAINEE = "trainee"
    }

    object launch {
        val values = listOf(
            Triple(
                "Welcome to ISTA NTIC SYBA",
                "ISTA NTIC (Institut Spécialisé en Technologies Appliquées – NTIC) is a leading institute dedicated to training future professionals in information and communication technologies.\n" +
                        "Gain practical skills, stay industry-ready, and shape your future with us.",
                R.drawable.hello_rafiki
            ), Triple(
                "Your All-in-One App",
                "Your smart companion for managing everything in one place — simple, fast, and efficient.\n" +
                        "\n",
                R.drawable.digital_tools_rafiki
            ), Triple(
                "Let’s Get Started",
                "Sign in and take control of your ISTA experience. Everything you need is just a tap away.",
                R.drawable.stay_positive_rafiki
            )
        )
    }
}