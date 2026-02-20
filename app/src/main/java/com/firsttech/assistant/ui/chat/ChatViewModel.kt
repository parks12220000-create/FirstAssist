package com.firsttech.assistant.ui.chat
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firsttech.assistant.domain.usecase.AssistantResponse
import com.firsttech.assistant.domain.usecase.ProcessInputUseCase
import com.firsttech.assistant.plugin.AttachmentManager
import com.firsttech.assistant.plugin.ChatContextManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
data class ChatMessage(val id: Long = System.currentTimeMillis(), val role: String, val text: String, val type: String? = null, val time: String = java.time.LocalTime.now().let { "%02d:%02d".format(it.hour, it.minute) })
data class ChatUiState(val messages: List<ChatMessage> = listOf(ChatMessage(role="assistant", text="안녕하세요! 🙂\nFirstAssist 개인비서입니다.\n\n\"오늘 브리핑 해줘\"로 시작해보세요!", type="greeting", time="09:00")), val isTyping: Boolean = false, val inputText: String = "", val pendingFileUri: Uri? = null, val pendingFileName: String? = null)
@HiltViewModel
class ChatViewModel @Inject constructor(private val processInput: ProcessInputUseCase, private val chatContext: ChatContextManager, private val attachmentManager: AttachmentManager) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState
    fun onInputChanged(t: String) { _uiState.value = _uiState.value.copy(inputText = t) }
    fun onFileAttached(uri: Uri, name: String) { _uiState.value = _uiState.value.copy(pendingFileUri = uri, pendingFileName = name) }
    fun clearAttachment() { _uiState.value = _uiState.value.copy(pendingFileUri = null, pendingFileName = null) }
    fun sendMessage() {
        val text = _uiState.value.inputText.trim(); val uri = _uiState.value.pendingFileUri; val fname = _uiState.value.pendingFileName
        if (text.isEmpty() && uri == null) return
        val display = when { uri != null && text.isNotEmpty() -> "📎 $fname\n$text"; uri != null -> "📎 $fname"; else -> text }
        _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + ChatMessage(role="user", text=display), inputText="", pendingFileUri=null, pendingFileName=null, isTyping=true)
        viewModelScope.launch {
            delay(300)
            try {
                val r = if (uri != null) { val a = attachmentManager.attachAndAnalyze(uri, text.ifEmpty{null}); AssistantResponse("attachment", a) } else processInput.process(text)
                _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + ChatMessage(role="assistant", text=r.text, type=r.type), isTyping=false)
                chatContext.recordChat(display, r.text, r.type)
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(messages = _uiState.value.messages + ChatMessage(role="assistant", text="⚠️ 오류: ${e.message}", type="error"), isTyping=false) }
        }
    }
    fun sendQuickAction(t: String) { _uiState.value = _uiState.value.copy(inputText = t); sendMessage() }
}
