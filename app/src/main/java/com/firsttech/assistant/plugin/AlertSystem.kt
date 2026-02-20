package com.firsttech.assistant.plugin
import androidx.room.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
@Entity(tableName = "alert_channels")
data class AlertChannel(@PrimaryKey(autoGenerate = true) val id: Long = 0, val type: String, val name: String, val config: String, val isActive: Boolean = true, val isDefault: Boolean = false, val createdAt: Long = System.currentTimeMillis())
@Entity(tableName = "alert_rules")
data class AlertRule(@PrimaryKey(autoGenerate = true) val id: Long = 0, val trigger: String, val channelId: Long, val condition: String? = null, val isActive: Boolean = true)
@Dao interface AlertChannelDao {
    @Insert suspend fun insert(c: AlertChannel): Long
    @Update suspend fun update(c: AlertChannel)
    @Query("SELECT * FROM alert_channels WHERE isActive = 1") suspend fun getAll(): List<AlertChannel>
    @Query("SELECT * FROM alert_channels WHERE type = :t AND isActive = 1 LIMIT 1") suspend fun getByType(t: String): AlertChannel?
    @Query("SELECT * FROM alert_channels WHERE isDefault = 1 AND isActive = 1 LIMIT 1") suspend fun getDefault(): AlertChannel?
}
@Dao interface AlertRuleDao {
    @Insert suspend fun insert(r: AlertRule): Long
    @Query("SELECT * FROM alert_rules WHERE trigger = :t AND isActive = 1") suspend fun getByTrigger(t: String): List<AlertRule>
    @Query("SELECT * FROM alert_rules WHERE isActive = 1") suspend fun getAll(): List<AlertRule>
}
class AlertSender(private val channelDao: AlertChannelDao, private val ruleDao: AlertRuleDao) {
    suspend fun send(trigger: String, title: String, message: String) { }
}
class AlertChannelManager(private val channelDao: AlertChannelDao, private val ruleDao: AlertRuleDao) {
    fun getRequiredConfig(type: String): List<Pair<String,String>> = when(type) {
        "telegram" -> listOf("bot_token" to "텔레그램 봇 토큰:", "chat_id" to "채팅 ID:")
        "slack" -> listOf("webhook_url" to "Slack Webhook URL:")
        else -> emptyList()
    }
    suspend fun registerChannel(type: String, config: Map<String, Any>, setAsDefault: Boolean = false): AlertChannel {
        val ch = AlertChannel(type=type, name=type, config=JSONObject(config).toString(), isDefault=setAsDefault)
        val id = channelDao.insert(ch); return ch.copy(id=id)
    }
    suspend fun getSummary(): String {
        val ch = channelDao.getAll(); if (ch.isEmpty()) return "🔔 등록된 알림 채널 없음\n\"알림 텔레그램으로 보내줘\"로 설정하세요"
        return "🔔 알림 채널:\n" + ch.joinToString("\n") { "  ${it.type} ${if(it.isDefault) "⭐" else ""}" }
    }
    fun detectChannelType(input: String): String? = when {
        input.contains("텔레그램") -> "telegram"; input.contains("슬랙") -> "slack"
        input.contains("디스코드") -> "discord"; input.contains("푸시") -> "push"; else -> null
    }
}
