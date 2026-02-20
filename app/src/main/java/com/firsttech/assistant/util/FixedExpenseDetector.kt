package com.firsttech.assistant.util
import com.firsttech.assistant.data.local.dao.FixedExpenseDao
class FixedExpenseDetector(private val dao: FixedExpenseDao, private val cal: CalendarHelper, private val noti: NotificationHelper) {
    suspend fun checkUpcoming(daysAhead: Int = 7): String {
        val all = dao.getAll(); if (all.isEmpty()) return ""
        return all.joinToString("\n") { "💳 ${it.name} ${it.amount}원 (매월 ${it.paymentDay}일)" }
    }
}
