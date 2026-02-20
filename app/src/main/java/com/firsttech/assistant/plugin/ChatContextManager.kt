package com.firsttech.assistant.plugin
import androidx.room.*
import java.time.LocalDate
import java.time.LocalTime
@Entity(tableName = "chat_history")
data class ChatRecord(@PrimaryKey(autoGenerate = true) val id: Long = 0, val userInput: String, val assistantResponse: String, val intentType: String, val pluginName: String? = null, val date: String, val time: String, val createdAt: Long = System.currentTimeMillis())
@Entity(tableName = "chat_summaries")
data class ChatSummary(@PrimaryKey(autoGenerate = true) val id: Long = 0, val summary: String, val period: String, val periodType: String, val keyTopics: String, val userPreferences: String? = null, val messageCount: Int, val createdAt: Long = System.currentTimeMillis())
@Entity(tableName = "user_context")
data class UserContext(@PrimaryKey(autoGenerate = true) val id: Long = 0, val key: String, val value: String, val confidence: Float = 1.0f, val source: String, val updatedAt: Long = System.currentTimeMillis())
@Dao interface ChatHistoryDao {
    @Insert suspend fun insert(r: ChatRecord): Long
    @Query("SELECT * FROM chat_history ORDER BY createdAt DESC LIMIT :n") suspend fun getRecent(n: Int = 50): List<ChatRecord>
    @Query("SELECT * FROM chat_history WHERE userInput LIKE '%' || :k || '%' ORDER BY createdAt DESC") suspend fun searchByInput(k: String): List<ChatRecord>
    @Query("SELECT COUNT(*) FROM chat_history WHERE date = :d") suspend fun getCountByDate(d: String): Int
}
@Dao interface ChatSummaryDao {
    @Insert suspend fun insert(s: ChatSummary): Long
    @Query("SELECT * FROM chat_summaries WHERE periodType = 'daily' ORDER BY createdAt DESC LIMIT :n") suspend fun getRecentDailySummaries(n: Int = 7): List<ChatSummary>
}
@Dao interface UserContextDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(c: UserContext): Long
    @Query("SELECT * FROM user_context ORDER BY updatedAt DESC") suspend fun getAll(): List<UserContext>
    @Query("SELECT * FROM user_context WHERE `key` = :k LIMIT 1") suspend fun getByKey(k: String): UserContext?
}
class ChatContextManager(private val chatHistoryDao: ChatHistoryDao, private val chatSummaryDao: ChatSummaryDao, private val userContextDao: UserContextDao) {
    suspend fun recordChat(userInput: String, response: String, intentType: String) {
        val now = LocalDate.now(); val time = LocalTime.now()
        chatHistoryDao.insert(ChatRecord(userInput = userInput, assistantResponse = response, intentType = intentType, date = now.toString(), time = "%02d:%02d".format(time.hour, time.minute)))
    }
    suspend fun buildContext(): String {
        val sb = StringBuilder()
        userContextDao.getAll().forEach { sb.appendLine("- ${it.key}: ${it.value}") }
        chatHistoryDao.getRecent(5).reversed().forEach { sb.appendLine("사용자: ${it.userInput}") }
        return sb.toString()
    }
    suspend fun searchHistory(keyword: String): String {
        val r = chatHistoryDao.searchByInput(keyword); if (r.isEmpty()) return "관련 대화 기록이 없습니다."
        val sb = StringBuilder("🔍 검색 결과 ${r.size}건:\n")
        r.take(10).forEach { sb.appendLine("▸ ${it.date} ${it.time}: ${it.userInput.take(50)}") }
        return sb.toString()
    }
    suspend fun resolveReference(input: String): String? {
        if (!listOf("아까","그거","방금","이전").any { input.contains(it) }) return null
        val r = chatHistoryDao.getRecent(5).firstOrNull { it.intentType !in listOf("greeting","help") }
        return r?.let { "이전 대화: \"${it.userInput}\"" }
    }
}
