package com.el_aouthmanie.nticapp.modules.intities
import android.os.Build
import androidx.annotation.RequiresApi
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import io.realm.kotlin.types.annotations.Index
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale
open class Seance : RealmObject {
    @PrimaryKey
    var id: String = "N/A"

    var codeSeance: String = ""

    var dayName : String = "N/A"
        set(value) {
            field = value.lowercase()
        }

    @Index // Faster querying
    var nomMode: String = ""


    var classRoom: String = "--"
    var teacher: String = "Unknown"

    var intitule: String = ""
    var moduleDetails: String = "No details"

    @Index
    var startingTime: String = "00:00"
    var endingTime: String = "00:00"
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStatus(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.FRANCE)

        val now = LocalTime.now()
        val today = LocalDate.now().dayOfWeek

        // Convert French day name to DayOfWeek
        val frenchToEnglishDays = mapOf(
            "Lundi" to DayOfWeek.MONDAY,
            "Mardi" to DayOfWeek.TUESDAY,
            "Mercredi" to DayOfWeek.WEDNESDAY,
            "Jeudi" to DayOfWeek.THURSDAY,
            "Vendredi" to DayOfWeek.FRIDAY,
            "Samedi" to DayOfWeek.SATURDAY,
            "dimanche" to DayOfWeek.SUNDAY
        )

        val seanceDay = frenchToEnglishDays[dayName]
            ?: return "Jour invalide"

        if (seanceDay != today) return "Pas aujourd'hui"

        val start = LocalTime.parse(startingTime, formatter)
        val end = LocalTime.parse(endingTime, formatter)

        return when {
            now.isBefore(start) -> "up coming"
            now.isAfter(end) -> "passed"
            now.isAfter(start) && now.isBefore(end) -> "on going"
            else -> ""
        }
    }
}
