package com.firsttech.assistant.util
import com.firsttech.assistant.domain.model.UserIntent
import java.time.LocalDate
object KeywordParser {
    fun parse(input: String): UserIntent? {
        val today = LocalDate.now()
        return when {
            input.matches(Regex(".*(?:오늘|브리핑|요약).*")) -> UserIntent.DailySummary(today)
            input.matches(Regex(".*일정.*(?:알려|보여|확인).*")) -> UserIntent.ScheduleQuery(today.toString(), null)
            input.matches(Regex(".*일정.*(?:추가|등록|잡아).*")) -> UserIntent.ScheduleAdd(input, today.toString(), null, null)
            input.matches(Regex(".*지출.*(?:얼마|알려|보여).*")) -> UserIntent.ExpenseQuery(today.toString(), null)
            input.matches(Regex(".*고정지출.*")) -> UserIntent.FixedExpenseQuery
            input.matches(Regex(".*통화.*(?:기록|내역|알려).*")) -> UserIntent.CallQuery(null)
            input.matches(Regex(".*할.?일.*(?:알려|보여|뭐).*")) -> UserIntent.TodoQuery
            input.matches(Regex(".*할.?일.*(?:추가|등록).*")) -> UserIntent.TodoAdd(input, null)
            input.matches(Regex(".*메모.*(?:알려|보여|검색).*")) -> UserIntent.MemoQuery(null)
            input.matches(Regex(".*메모.*(?:해|저장|기록).*")) -> UserIntent.MemoAdd(input)
            input.matches(Regex(".*(?:도움|도와|뭐.*할.*수).*")) -> UserIntent.Help
            else -> null
        }
    }
}
fun formatCurrency(amount: Int): String = "%,d원".format(amount)
