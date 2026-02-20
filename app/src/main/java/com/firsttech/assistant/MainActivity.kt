package com.firsttech.assistant
import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.firsttech.assistant.data.local.AppDatabase
import com.firsttech.assistant.plugin.BuiltinPlugins
import com.firsttech.assistant.ui.chat.ChatScreen
import com.firsttech.assistant.ui.theme.FirstAssistTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltAndroidApp
class FirstAssistApp : Application()
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var db: AppDatabase
    private val perms = mutableListOf(
        Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CONTACTS
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
            add(Manifest.permission.READ_MEDIA_IMAGES)
        } else add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notGranted = perms.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (notGranted.isNotEmpty()) launcher.launch(notGranted.toTypedArray())
        lifecycleScope.launch {
            val dao = db.pluginDao()
            if (dao.getBuiltins().isEmpty()) BuiltinPlugins.getAll().forEach { dao.insert(it) }
        }
        setContent {
            FirstAssistTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { ChatScreen() }
            }
        }
    }
}
