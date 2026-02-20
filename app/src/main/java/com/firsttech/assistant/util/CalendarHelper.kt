package com.firsttech.assistant.util
import android.content.Context
import java.time.LocalDate
data class CalEvent(val id: Long, val title: String, val startTime: Long, val location: String?)
class CalendarHelper(private val context: Context) {
    fun getEventsForDate(date: LocalDate): List<CalEvent> = emptyList()
    fun getEventsForRange(start: LocalDate, end: LocalDate): List<CalEvent> = emptyList()
    fun addEvent(title: String, date: LocalDate, time: String?) {}
}
