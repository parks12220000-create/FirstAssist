package com.firsttech.assistant.data.local
import androidx.room.*
import com.firsttech.assistant.data.local.dao.*
import com.firsttech.assistant.data.local.entity.*
import com.firsttech.assistant.plugin.*
@Database(entities = [Expense::class,FixedExpense::class,Schedule::class,Todo::class,Memo::class,Plugin::class,ChatRecord::class,ChatSummary::class,UserContext::class,AlertChannel::class,AlertRule::class,Attachment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun fixedExpenseDao(): FixedExpenseDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun todoDao(): TodoDao
    abstract fun memoDao(): MemoDao
    abstract fun pluginDao(): PluginDao
    abstract fun chatHistoryDao(): ChatHistoryDao
    abstract fun chatSummaryDao(): ChatSummaryDao
    abstract fun userContextDao(): UserContextDao
    abstract fun alertChannelDao(): AlertChannelDao
    abstract fun alertRuleDao(): AlertRuleDao
    abstract fun attachmentDao(): AttachmentDao
}
