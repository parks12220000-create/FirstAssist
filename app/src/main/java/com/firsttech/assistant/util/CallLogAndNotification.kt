package com.firsttech.assistant.util
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.CallLog
data class CallRecord(val name: String, val number: String, val type: String, val date: Long, val duration: Int)
class CallLogHelper(private val context: Context) {
    fun getRecent(limit: Int = 20): List<CallRecord> = emptyList()
    fun searchByName(name: String): List<CallRecord> = emptyList()
}
class NotificationHelper(private val context: Context) {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel("firstassist", "FirstAssist", NotificationManager.IMPORTANCE_DEFAULT)
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
        }
    }
    fun show(title: String, message: String) {}
}
