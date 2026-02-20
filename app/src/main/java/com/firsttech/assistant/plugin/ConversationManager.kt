package com.firsttech.assistant.plugin
class ConversationManager {
    private var active: Pair<Long, List<Pair<String,String>>>? = null
    private var collected = mutableMapOf<String, Any>()
    private var idx = 0
    fun hasActiveSession(): Boolean = active != null
    fun startSession(plugin: Plugin): String { active = plugin.id to emptyList(); return "" }
    fun processAnswer(input: String): Pair<Boolean, String> { active = null; return Pair(true, "✅ 완료"); }
    fun getCollectedParams(): Map<String, Any> = collected
    fun getActivePluginId(): Long? = active?.first
    fun endSession() { active = null; collected.clear(); idx = 0 }
}
