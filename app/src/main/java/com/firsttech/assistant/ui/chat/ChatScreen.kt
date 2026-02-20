package com.firsttech.assistant.ui.chat
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
val typeColors = mapOf("schedule" to Color(0xFF3B82F6),"expense" to Color(0xFFF59E0B),"fixed_expense" to Color(0xFFEF4444),"call" to Color(0xFF8B5CF6),"todo" to Color(0xFFEC4899),"memo" to Color(0xFF6366F1),"summary" to Color(0xFFF97316),"help" to Color(0xFF6B7280),"greeting" to Color(0xFF10B981),"error" to Color(0xFFEF4444),"attachment" to Color(0xFF14B8A6),"question" to Color(0xFFA78BFA),"alert" to Color(0xFFF97316),"plugin" to Color(0xFF06B6D4),"ai_search" to Color(0xFFD946EF),"ai_chat" to Color(0xFFA78BFA))
val typeLabels = mapOf("schedule" to "📅 일정","expense" to "💰 지출","fixed_expense" to "💳 고정지출","call" to "📞 통화","todo" to "📋 할일","memo" to "📝 메모","summary" to "📋 브리핑","help" to "도움말","greeting" to "인사","error" to "오류","attachment" to "📎 파일","question" to "💬 질문","alert" to "🔔 알림","plugin" to "🔧 기능","ai_search" to "🔍 AI검색","ai_chat" to "🤖 AI답변","schedule_add" to "✅ 일정","todo_add" to "✅ 할일","memo_add" to "✅ 메모")
fun getFileName(uri: Uri, ctx: Context): String { var n = "파일"; ctx.contentResolver.query(uri,null,null,null,null)?.use { val i = it.getColumnIndex(OpenableColumns.DISPLAY_NAME); if(it.moveToFirst()&&i>=0) n=it.getString(i) }; return n }
@Composable fun ChatScreen(vm: ChatViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsState(); val listState = rememberLazyListState(); val ctx = LocalContext.current
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { it?.let { vm.onFileAttached(it, getFileName(it,ctx)) } }
    val imgPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { it?.let { vm.onFileAttached(it, getFileName(it,ctx)) } }
    LaunchedEffect(state.messages.size) { if(state.messages.isNotEmpty()) listState.animateScrollToItem(state.messages.size-1) }
    Column(Modifier.fillMaxSize().background(Color(0xFF0F1117))) {
        Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF1E3A5F),Color(0xFF2D1B69)))).padding(16.dp,40.dp,16.dp,16.dp)) { Text("FirstAssist",color=Color.White,fontSize=20.sp,fontWeight=FontWeight.Bold) }
        LazyColumn(Modifier.weight(1f).padding(horizontal=16.dp),listState,verticalArrangement=Arrangement.spacedBy(12.dp),contentPadding=PaddingValues(vertical=12.dp)) {
            items(state.messages) { MessageBubble(it) }
            if(state.isTyping) { item { Text("⏳ 처리중...",color=Color(0xFF6B7280),fontSize=13.sp) } }
        }
        LazyRow(Modifier.padding(horizontal=16.dp,vertical=4.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            items(listOf("오늘 브리핑","일정 알려줘","이번달 지출","할일 보여줘")) { a -> OutlinedButton(onClick={vm.sendQuickAction(a)},shape=RoundedCornerShape(20.dp)) { Text(a,color=Color(0xFF93C5FD),fontSize=12.sp) } }
        }
        if(state.pendingFileName!=null) { Row(Modifier.fillMaxWidth().background(Color(0xFF1A1D2E)).padding(16.dp,8.dp),verticalAlignment=Alignment.CenterVertically) { Text("📎 ${state.pendingFileName}",color=Color(0xFF93C5FD),fontSize=13.sp,modifier=Modifier.weight(1f)); Text("✕",color=Color(0xFFEF4444),modifier=Modifier.clickable{vm.clearAttachment()}.padding(8.dp)) } }
        Row(Modifier.fillMaxWidth().background(Color(0xFF161825)).padding(12.dp),verticalAlignment=Alignment.CenterVertically) {
            var menu by remember { mutableStateOf(false) }
            Box { IconButton(onClick={menu=!menu},Modifier.size(36.dp)) { Text(if(menu)"✕" else "+",color=Color(0xFF93C5FD),fontSize=22.sp,fontWeight=FontWeight.Bold) }
                DropdownMenu(menu,{menu=false}) { DropdownMenuItem(text={Text("📷 사진")},onClick={menu=false;imgPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))}); DropdownMenuItem(text={Text("📎 파일")},onClick={menu=false;filePicker.launch(arrayOf("*/*"))}) } }
            Spacer(Modifier.width(4.dp))
            TextField(state.inputText,{vm.onInputChanged(it)},Modifier.weight(1f),placeholder={Text("자연어로 입력하세요...",color=Color(0xFF6B7280))},shape=RoundedCornerShape(24.dp),colors=TextFieldDefaults.colors(unfocusedContainerColor=Color(0xFF1E2030),focusedContainerColor=Color(0xFF1E2030),focusedTextColor=Color.White,unfocusedTextColor=Color.White,cursorColor=Color(0xFF3B82F6),focusedIndicatorColor=Color.Transparent,unfocusedIndicatorColor=Color.Transparent),keyboardOptions=KeyboardOptions(imeAction=ImeAction.Send),keyboardActions=KeyboardActions(onSend={vm.sendMessage()}),singleLine=true)
            Spacer(Modifier.width(8.dp))
            val active = state.inputText.isNotBlank()||state.pendingFileUri!=null
            IconButton(onClick={vm.sendMessage()},Modifier.size(44.dp).clip(CircleShape).background(if(active)Brush.linearGradient(listOf(Color(0xFF3B82F6),Color(0xFF2563EB)))else Brush.horizontalGradient(listOf(Color(0xFF2A2D3E),Color(0xFF2A2D3E)))),enabled=active) { Icon(Icons.Default.KeyboardArrowUp,"전송",tint=if(active)Color.White else Color(0xFF6B7280)) }
        }
    }
}
@Composable fun MessageBubble(msg: ChatMessage) {
    val isUser = msg.role=="user"
    Row(Modifier.fillMaxWidth(),if(isUser)Arrangement.End else Arrangement.Start) {
        if(!isUser) { Box(Modifier.size(30.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(listOf(Color(0xFF3B82F6),Color(0xFF8B5CF6)))),contentAlignment=Alignment.Center){Text("🤖",fontSize=14.sp)}; Spacer(Modifier.width(8.dp)) }
        Column(Modifier.widthIn(max=300.dp)) {
            if(!isUser&&msg.type!=null) { val c=typeColors[msg.type]?:Color(0xFF9CA3AF); Surface(shape=RoundedCornerShape(6.dp),color=c.copy(alpha=0.1f)){Text(typeLabels[msg.type]?:msg.type,Modifier.padding(8.dp,2.dp),color=c,fontSize=10.sp,fontWeight=FontWeight.SemiBold)};Spacer(Modifier.height(4.dp)) }
            Surface(shape=if(isUser)RoundedCornerShape(18.dp,18.dp,4.dp,18.dp)else RoundedCornerShape(18.dp,18.dp,18.dp,4.dp),color=if(isUser)Color(0xFF3B82F6)else Color(0xFF1E2030)) { Text(msg.text,Modifier.padding(12.dp,10.dp),color=if(isUser)Color.White else Color(0xFFE5E7EB),fontSize=14.sp,lineHeight=22.sp) }
            Text(msg.time,Modifier.padding(4.dp),color=Color(0xFF6B7280),fontSize=10.sp)
        }
    }
}
