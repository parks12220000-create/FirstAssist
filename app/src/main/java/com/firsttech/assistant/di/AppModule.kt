package com.firsttech.assistant.di
import android.content.Context
import androidx.room.Room
import com.firsttech.assistant.data.local.AppDatabase
import com.firsttech.assistant.data.local.dao.*
import com.firsttech.assistant.plugin.*
import com.firsttech.assistant.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun provideDb(@ApplicationContext c: Context): AppDatabase = Room.databaseBuilder(c, AppDatabase::class.java, "firstassist.db").fallbackToDestructiveMigration().build()
    @Provides fun expenseDao(db: AppDatabase) = db.expenseDao()
    @Provides fun fixedExpenseDao(db: AppDatabase) = db.fixedExpenseDao()
    @Provides fun scheduleDao(db: AppDatabase) = db.scheduleDao()
    @Provides fun todoDao(db: AppDatabase) = db.todoDao()
    @Provides fun memoDao(db: AppDatabase) = db.memoDao()
    @Provides fun pluginDao(db: AppDatabase) = db.pluginDao()
    @Provides fun chatHistoryDao(db: AppDatabase) = db.chatHistoryDao()
    @Provides fun chatSummaryDao(db: AppDatabase) = db.chatSummaryDao()
    @Provides fun userContextDao(db: AppDatabase) = db.userContextDao()
    @Provides fun alertChannelDao(db: AppDatabase) = db.alertChannelDao()
    @Provides fun alertRuleDao(db: AppDatabase) = db.alertRuleDao()
    @Provides fun attachmentDao(db: AppDatabase) = db.attachmentDao()
    @Provides @Singleton fun calendarHelper(@ApplicationContext c: Context) = CalendarHelper(c)
    @Provides @Singleton fun callLogHelper(@ApplicationContext c: Context) = CallLogHelper(c)
    @Provides @Singleton fun notificationHelper(@ApplicationContext c: Context) = NotificationHelper(c)
    @Provides @Singleton fun fixedExpenseDetector(d: FixedExpenseDao, c: CalendarHelper, n: NotificationHelper) = FixedExpenseDetector(d, c, n)
    @Provides @Singleton fun pluginManager(d: PluginDao) = PluginManager(d)
    @Provides @Singleton fun pluginExecutor(@ApplicationContext c: Context, ch: CalendarHelper) = PluginExecutor(c, ch)
    @Provides @Singleton fun conversationManager() = ConversationManager()
    @Provides @Singleton fun chatContextManager(a: ChatHistoryDao, b: ChatSummaryDao, c: UserContextDao) = ChatContextManager(a, b, c)
    @Provides @Singleton fun alertSender(a: AlertChannelDao, b: AlertRuleDao) = AlertSender(a, b)
    @Provides @Singleton fun alertChannelManager(a: AlertChannelDao, b: AlertRuleDao) = AlertChannelManager(a, b)
    @Provides @Singleton fun fileManager(@ApplicationContext c: Context) = FileManager(c)
    @Provides @Singleton fun fileAnalyzer(@ApplicationContext c: Context) = FileAnalyzer(c)
    @Provides @Singleton fun attachmentManager(d: AttachmentDao, f: FileManager, a: FileAnalyzer) = AttachmentManager(d, f, a)
}
