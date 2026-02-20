package com.firsttech.assistant.plugin
import android.content.Context
import com.firsttech.assistant.util.CalendarHelper
import org.json.JSONArray
class PluginExecutor(private val context: Context, private val calendarHelper: CalendarHelper) {
    suspend fun execute(plugin: Plugin): String {
        return "📋 ${plugin.name} 실행 완료"
    }
}
