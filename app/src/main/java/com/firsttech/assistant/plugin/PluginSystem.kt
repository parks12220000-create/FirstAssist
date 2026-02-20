package com.firsttech.assistant.plugin
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Entity(tableName = "plugins")
data class Plugin(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val description: String, val keywords: String,
    val scriptType: String, val scriptCode: String, val category: String,
    val usageCount: Int = 0, val isActive: Boolean = true, val createdAt: Long = System.currentTimeMillis()
)
@Dao interface PluginDao {
    @Insert suspend fun insert(p: Plugin): Long
    @Update suspend fun update(p: Plugin)
    @Query("SELECT * FROM plugins WHERE isActive = 1 ORDER BY usageCount DESC") suspend fun getAll(): List<Plugin>
    @Query("SELECT * FROM plugins WHERE scriptType = 'builtin' AND isActive = 1") suspend fun getBuiltins(): List<Plugin>
    @Query("SELECT * FROM plugins WHERE keywords LIKE '%' || :k || '%' AND isActive = 1") suspend fun searchByKeyword(k: String): List<Plugin>
    @Query("SELECT * FROM plugins WHERE name = :n AND isActive = 1 LIMIT 1") suspend fun findByName(n: String): Plugin?
    @Query("UPDATE plugins SET usageCount = usageCount + 1 WHERE id = :id") suspend fun incrementUsage(id: Long)
}
data class PluginAction(val action: String, val params: Map<String, Any>)
class PluginManager(private val pluginDao: PluginDao) {
    suspend fun findPlugin(input: String): Plugin? {
        val words = input.replace(Regex("[^가-힣a-zA-Z0-9\\s]"), "").split("\\s+".toRegex())
        for (w in words) {
            if (w.length < 2) continue
            val r = pluginDao.searchByKeyword(w)
            if (r.isNotEmpty()) { val best = r.maxByOrNull { p -> words.count { p.keywords.contains(it) } }; if (best != null) { pluginDao.incrementUsage(best.id); return best } }
        }
        return null
    }
    suspend fun generatePlugin(input: String): Plugin {
        val p = Plugin(name = input.replace(Regex("(해줘|보여줘|알려줘)"), "").trim().take(20), description = input, keywords = input.split("\\s+".toRegex()).filter { it.length >= 2 }.joinToString(","), scriptType = "generated", scriptCode = "[]", category = "기타")
        val id = pluginDao.insert(p); return p.copy(id = id)
    }
    suspend fun listPlugins(): String {
        val all = pluginDao.getAll(); if (all.isEmpty()) return "등록된 기능이 없습니다."
        val sb = StringBuilder("🔧 등록된 기능 총 ${all.size}개\n")
        all.filter { it.scriptType == "builtin" }.forEach { sb.appendLine("  📦 ${it.name} - ${it.description}") }
        all.filter { it.scriptType == "generated" }.forEach { sb.appendLine("  🆕 ${it.name} (사용 ${it.usageCount}회)") }
        return sb.toString()
    }
}
