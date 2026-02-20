package com.firsttech.assistant.domain.usecase
import com.firsttech.assistant.data.local.dao.*
import com.firsttech.assistant.data.local.entity.*
import com.firsttech.assistant.domain.model.UserIntent
import com.firsttech.assistant.plugin.*
import com.firsttech.assistant.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
data class AssistantResponse(val type: String, val text: String)
class ProcessInputUseCase @Inject constructor(
    private val expenseDao: ExpenseDao, private val fixedExpenseDao: FixedExpenseDao,
    private val scheduleDao: ScheduleDao, private val todoDao: TodoDao, private val memoDao: MemoDao,
    private val calendarHelper: CalendarHelper, private val callLogHelper: CallLogHelper,
    private val fixedExpenseDetector: FixedExpenseDetector, private val pluginManager: PluginManager,
    private val pluginExecutor: PluginExecutor, private val conversationManager: ConversationManager,
    private val chatContextManager: ChatContextManager, private val alertChannelManager: AlertChannelManager,
    private val alertSender: AlertSender, private val attachmentManager: AttachmentManager,
) {
    private val today = LocalDate.now()
    suspend fun process(input: String): AssistantResponse {
        if (conversationManager.hasActiveSession()) {
            val (done, msg) = conversationManager.processAnswer(input)
            if (done) { conversationManager.endSession(); return AssistantResponse("plugin", "✅ 완료") }
            return AssistantResponse("question", msg)
        }
        val ch = alertChannelManager.detectChannelType(input)
        if (ch != null && input.contains("알림")) return AssistantResponse("alert", "✅ ${ch} 알림 설정!")
        if (input.matches(Regex(".*알림.*(?:설정|보여|확인).*"))) return AssistantResponse("alert", alertChannelManager.getSummary())
        val ref = chatContextManager.resolveReference(input)
        if (ref != null) return AssistantResponse("context", "🔄 $ref")
        if (input.matches(Regex(".*(?:저번|이전).*(?:대화|질문).*"))) return AssistantResponse("history", chatContextManager.searchHistory(input))
        if (input.matches(Regex(".*(?:기능|뭐.*할.*수).*"))) return AssistantResponse("plugin", pluginManager.listPlugins())
        if (input.matches(Regex(".*첨부.*(?:보여|찾아|목록).*"))) return AssistantResponse("attachment", attachmentManager.listRecent())
        if (input.matches(Regex(".*분석.*결과.*"))) return AssistantResponse("attachment", attachmentManager.getAnalysis(input))
        if (input.matches(Regex(".*(?:검색|클로드|인터넷|웹).*(?:해줘|에서|찾아).*"))) {
            val q = input.replace(Regex("(검색해줘|클로드에서|찾아줘)"), "").trim()
            return AssistantResponse("ai_search", "🔍 \"$q\" 검색 결과\n(Claude API 키 설정 후 활성화)\n\n────────\n🔑 Claude API | Sonnet 4.5")
        }
        val intent = KeywordParser.parse(input)
        if (intent == null) {
            val plugin = pluginManager.findPlugin(input)
            if (plugin != null) return AssistantResponse("plugin", pluginExecutor.execute(plugin))
            val np = pluginManager.generatePlugin(input)
            return AssistantResponse("ai_chat", "🤖 \"$input\"\n(Claude API 키 설정 후 활성화)\n\n────────\n🔑 Claude API | Sonnet 4.5")
        }
        return when (intent) {
            is UserIntent.DailySummary -> { val s = scheduleDao.getByDate(today.toString()); val e = expenseDao.getByDate(today.toString()); val t = todoDao.getActive(); val f = fixedExpenseDetector.checkUpcoming()
                AssistantResponse("summary", "📋 오늘의 브리핑\n\n📅 일정 ${s.size}건\n${s.joinToString("\n"){"  • ${it.title}"}}\n\n💰 지출 ${e.size}건 총 ${formatCurrency(e.sumOf{it.amount})}\n\n📋 할일 ${t.size}건\n${t.joinToString("\n"){"  • ${it.title}"}}\n\n${if(f.isNotEmpty()) "⚠️ 고정지출\n$f" else ""}") }
            is UserIntent.ScheduleQuery -> { val s = scheduleDao.getByDate(intent.date); AssistantResponse("schedule", if(s.isEmpty()) "📅 일정이 없습니다" else "📅 일정 ${s.size}건:\n${s.joinToString("\n"){"  • ${it.title} ${it.time?:""}"}}" ) }
            is UserIntent.ScheduleAdd -> { scheduleDao.insert(Schedule(title=intent.title?:intent.raw, date=intent.date, time=intent.time)); AssistantResponse("schedule_add", "✅ 일정 등록 완료!") }
            is UserIntent.ExpenseQuery -> { val e = expenseDao.getByDate(intent.date); AssistantResponse("expense", "💰 지출 ${e.size}건\n총 ${formatCurrency(e.sumOf{it.amount})}") }
            is UserIntent.FixedExpenseQuery -> { val f = fixedExpenseDao.getAll(); AssistantResponse("fixed_expense", if(f.isEmpty()) "고정지출 없음" else "💳 고정지출:\n${f.joinToString("\n"){"  • ${it.name} ${formatCurrency(it.amount)} (${it.paymentDay}일)"}}") }
            is UserIntent.FixedExpenseAdd -> { fixedExpenseDao.insert(FixedExpense(name=intent.name, amount=intent.amount, paymentDay=intent.day)); AssistantResponse("fixed_expense", "✅ 고정지출 등록!") }
            is UserIntent.CallQuery -> { val c = if(intent.name!=null) callLogHelper.searchByName(intent.name) else callLogHelper.getRecent(); AssistantResponse("call", "📞 통화기록 ${c.size}건") }
            is UserIntent.TodoQuery -> { val t = todoDao.getActive(); AssistantResponse("todo", if(t.isEmpty()) "📋 할일 없음" else "📋 할일:\n${t.joinToString("\n"){"  • ${it.title}"}}") }
            is UserIntent.TodoAdd -> { todoDao.insert(Todo(title=intent.title, dueDate=intent.dueDate)); AssistantResponse("todo_add", "✅ 할일 추가!") }
            is UserIntent.MemoQuery -> { val m = if(intent.keyword!=null) memoDao.search(intent.keyword) else memoDao.getRecent(); AssistantResponse("memo", "📝 메모 ${m.size}건:\n${m.joinToString("\n"){"  • ${it.content.take(50)}"}}") }
            is UserIntent.MemoAdd -> { memoDao.insert(Memo(content=intent.content)); AssistantResponse("memo_add", "✅ 메모 저장!") }
            is UserIntent.GeneralChat -> AssistantResponse("ai_chat", "🤖 \"${intent.query}\"\n(Claude API 키 설정 후 활성화)\n\n────────\n🔑 Claude API | Sonnet 4.5")
            is UserIntent.Help -> AssistantResponse("help", "💡 사용법:\n• 오늘 브리핑 해줘\n• 일정 알려줘 / 추가해줘\n• 이번달 지출 얼마\n• 고정지출 보여줘\n• 통화기록 알려줘\n• 할일 / 메모\n• 검색해줘\n• 파일 첨부 + 분석해줘\n• 알림 텔레그램으로 보내줘")
        }
    }
}
