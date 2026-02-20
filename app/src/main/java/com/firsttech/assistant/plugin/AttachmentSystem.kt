package com.firsttech.assistant.plugin
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.room.*
import java.io.File
import java.io.FileOutputStream
@Entity(tableName = "attachments")
data class Attachment(@PrimaryKey(autoGenerate = true) val id: Long = 0, val fileName: String, val fileType: String, val mimeType: String, val fileSize: Long, val localPath: String, val analysis: String? = null, val summary: String? = null, val tags: String? = null, val createdAt: Long = System.currentTimeMillis())
@Dao interface AttachmentDao {
    @Insert suspend fun insert(a: Attachment): Long
    @Query("SELECT * FROM attachments ORDER BY createdAt DESC LIMIT :n") suspend fun getRecent(n: Int = 50): List<Attachment>
    @Query("SELECT * FROM attachments WHERE fileName LIKE '%' || :k || '%' OR tags LIKE '%' || :k || '%' OR analysis LIKE '%' || :k || '%'") suspend fun search(k: String): List<Attachment>
}
class FileManager(private val context: Context) {
    fun copyToInternal(uri: Uri): Triple<String,String,Long>? {
        return try {
            var name = "file"; context.contentResolver.query(uri, null, null, null, null)?.use { c -> val i = c.getColumnIndex(OpenableColumns.DISPLAY_NAME); if (c.moveToFirst() && i >= 0) name = c.getString(i) }
            val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
            val dir = File(context.filesDir, "attachments").also { it.mkdirs() }
            val dest = File(dir, "${System.currentTimeMillis()}_$name")
            context.contentResolver.openInputStream(uri)?.use { i -> FileOutputStream(dest).use { o -> i.copyTo(o) } }
            Triple(name, mime, dest.length())
        } catch (e: Exception) { null }
    }
}
class FileAnalyzer(private val context: Context) {
    fun analyze(name: String, mime: String, size: Long): String {
        val type = when { mime.startsWith("image/") -> "📸 이미지"; mime.contains("pdf") -> "📄 PDF"; mime.contains("sheet") || name.endsWith(".csv") -> "📊 스프레드시트"; else -> "📎 파일" }
        return "$type 분석\n파일: $name\n크기: ${size/1024}KB\n타입: $mime"
    }
}
class AttachmentManager(private val dao: AttachmentDao, private val fm: FileManager, private val fa: FileAnalyzer) {
    suspend fun attachAndAnalyze(uri: Uri, req: String? = null): String {
        val info = fm.copyToInternal(uri) ?: return "⚠️ 파일을 읽을 수 없습니다."
        val (name, mime, size) = info
        val analysis = fa.analyze(name, mime, size) + if (req != null) "\n\n요청: $req\n→ Claude API로 상세 분석 예정" else ""
        dao.insert(Attachment(fileName=name, fileType=mime.split("/")[0], mimeType=mime, fileSize=size, localPath="", analysis=analysis, tags=name))
        return analysis
    }
    suspend fun searchAttachments(k: String): String {
        val r = dao.search(k); if (r.isEmpty()) return "📎 관련 첨부파일 없음"
        return "📎 검색 결과 ${r.size}건:\n" + r.joinToString("\n") { "  ${it.fileName} (${it.fileSize/1024}KB)" }
    }
    suspend fun getAnalysis(k: String): String { val r = dao.search(k); return r.firstOrNull()?.analysis ?: "분석 결과 없음" }
    suspend fun listRecent(): String { val r = dao.getRecent(10); if (r.isEmpty()) return "📎 첨부파일 없음"; return "📎 최근 파일:\n" + r.joinToString("\n") { "  ${it.fileName}" } }
}
