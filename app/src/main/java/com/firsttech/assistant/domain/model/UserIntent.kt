package com.firsttech.assistant.domain.model
import java.time.LocalDate
sealed class UserIntent {
    data class DailySummary(val date: LocalDate) : UserIntent()
    data class ScheduleQuery(val date: String, val person: String?) : UserIntent()
    data class ScheduleAdd(val raw: String, val date: String, val time: String?, val title: String?) : UserIntent()
    data class ExpenseQuery(val date: String, val category: String?) : UserIntent()
    object FixedExpenseQuery : UserIntent()
    data class FixedExpenseAdd(val name: String, val amount: Int, val day: Int) : UserIntent()
    data class CallQuery(val name: String?) : UserIntent()
    object TodoQuery : UserIntent()
    data class TodoAdd(val title: String, val dueDate: String?) : UserIntent()
    data class MemoQuery(val keyword: String?) : UserIntent()
    data class MemoAdd(val content: String) : UserIntent()
    data class GeneralChat(val query: String) : UserIntent()
    object Help : UserIntent()
}
