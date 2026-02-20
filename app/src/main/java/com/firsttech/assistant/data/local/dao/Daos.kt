package com.firsttech.assistant.data.local.dao
import androidx.room.*
import com.firsttech.assistant.data.local.entity.*
@Dao interface ExpenseDao {
    @Insert suspend fun insert(e: Expense): Long
    @Query("SELECT * FROM expenses WHERE date = :date") suspend fun getByDate(date: String): List<Expense>
    @Query("SELECT * FROM expenses WHERE date BETWEEN :s AND :e") suspend fun getByRange(s: String, e: String): List<Expense>
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC LIMIT :n") suspend fun getRecent(n: Int = 20): List<Expense>
}
@Dao interface FixedExpenseDao {
    @Insert suspend fun insert(e: FixedExpense): Long
    @Update suspend fun update(e: FixedExpense)
    @Query("SELECT * FROM fixed_expenses WHERE isActive = 1") suspend fun getAll(): List<FixedExpense>
    @Query("SELECT * FROM fixed_expenses WHERE name LIKE '%' || :k || '%'") suspend fun search(k: String): List<FixedExpense>
}
@Dao interface ScheduleDao {
    @Insert suspend fun insert(s: Schedule): Long
    @Query("SELECT * FROM schedules WHERE date = :date") suspend fun getByDate(date: String): List<Schedule>
    @Query("SELECT * FROM schedules WHERE date BETWEEN :s AND :e") suspend fun getByRange(s: String, e: String): List<Schedule>
}
@Dao interface TodoDao {
    @Insert suspend fun insert(t: Todo): Long
    @Update suspend fun update(t: Todo)
    @Query("SELECT * FROM todos WHERE isDone = 0 ORDER BY createdAt DESC") suspend fun getActive(): List<Todo>
    @Query("SELECT * FROM todos WHERE dueDate = :date") suspend fun getByDate(date: String): List<Todo>
}
@Dao interface MemoDao {
    @Insert suspend fun insert(m: Memo): Long
    @Query("SELECT * FROM memos ORDER BY createdAt DESC LIMIT :n") suspend fun getRecent(n: Int = 20): List<Memo>
    @Query("SELECT * FROM memos WHERE content LIKE '%' || :k || '%'") suspend fun search(k: String): List<Memo>
}
