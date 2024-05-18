package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.R
import android.content.SharedPreferences
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import cz.cvut.fel.tasktest.notification.TaskifyNotificationService

@Composable
fun SettingsScreen(navController: NavHostController, drawerState: DrawerState) {
    var showAboutWindow by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    var isNotificationEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
    var isNotificationEnabledString by remember {
        mutableStateOf(if (!isNotificationEnabled) "Enable" else "Disable"
    )}
    fun hideAboutWindow() {
        showAboutWindow = false
    }
    Scaffold(
        topBar = { CustomAppBar(drawerState = drawerState,
            title = "Settings",
            backgroundColor = MaterialTheme.colorScheme.primary,
            imageVector = Icons.Default.ArrowBack)}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
           Text(modifier = Modifier
               .background(Color.LightGray)
               .height(50.dp)
               .width(400.dp)
               .padding(start = 10.dp, top = 10.dp),
               text = "Notifications",
               style = TextStyle(
                     color = Color.Black,
                     fontSize = MaterialTheme.typography.bodyLarge.fontSize,
               )
           )
            ClickableText(
                text = AnnotatedString("$isNotificationEnabledString notifications"),
                modifier = Modifier
                    .height(70.dp)
                    .width(400.dp)
                    .padding(start = 10.dp, top = 20.dp),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                ),
                onClick = {
                    handleNotificationsClick(context, sharedPreferences, isNotificationEnabled) { newStatus, newString ->
                        isNotificationEnabled = newStatus
                        isNotificationEnabledString = newString
                    }
                }
            )
            Text(modifier = Modifier
                .background(Color.LightGray)
                .height(50.dp)
                .width(400.dp)
                .padding(start = 10.dp, top = 10.dp),
                text = "Tags Settings",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                )
            )
            ClickableText(
                text = AnnotatedString("Create your Tags"),
                modifier = Modifier
                    .height(70.dp)
                    .width(400.dp)
                    .padding(start = 10.dp, top = 20.dp),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                ),
                onClick = {
                    navController.navigate(MainRoute.TagCreation.name)
                }
            )
            Text(modifier = Modifier
                .background(Color.LightGray)
                .height(50.dp)
                .width(400.dp)
                .padding(start = 10.dp, top = 10.dp),
                text = "General Settings",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                )
            )
            ClickableText(
                text = AnnotatedString("About Taskify"),
                modifier = Modifier
                    .height(70.dp)
                    .width(400.dp)
                    .padding(start = 10.dp, top = 20.dp),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                ),
                onClick = {
                    showAboutWindow = true
                }
            )
            if (showAboutWindow) {
                AboutWindow(::hideAboutWindow)
            }
            Text(modifier = Modifier
                .background(Color.LightGray)
                .height(50.dp)
                .width(400.dp)
                .padding(start = 10.dp, top = 10.dp),
                text = "Account Settings",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                )
            )
            ClickableText(
                text = AnnotatedString("Customize your account"),
                modifier = Modifier
                    .height(70.dp)
                    .width(400.dp)
                    .padding(start = 10.dp, top = 20.dp),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                ),
                onClick = {
                    navController.navigate(MainRoute.AccountCustomization.name)
                }
            )
        }
    }
}

fun handleNotificationsClick(
    context: Context,
    sharedPreferences: SharedPreferences,
    isNotificationEnabled: Boolean,
    updateState: (Boolean, String) -> Unit
) {
    val newStatus = !isNotificationEnabled
    val newString = if (newStatus) "Disable" else "Enable"
    with(sharedPreferences.edit()) {
        putBoolean("notifications_enabled", newStatus)
        apply()
    }


    if (newStatus) {
        // Start the notification service
        context.startService(Intent(context, TaskifyNotificationService::class.java))
    } else {
        // Stop the notification service
        context.stopService(Intent(context, TaskifyNotificationService::class.java))
    }

    updateState(newStatus, newString)
}
@Composable
fun AboutWindow(onDismiss: () -> Unit){
    val uriHandler = LocalUriHandler.current
    Dialog(onDismissRequest = { onDismiss()}) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = "About Taskify",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Magenta,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                )
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Taskify is a simple task manager app that helps you to organize your tasks and keep track of your progress.",
                modifier = Modifier
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                )
            )
            Spacer(modifier = Modifier.height(60.dp))
            Text(text = "Version: 1.0",
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                )
            )
            Spacer(modifier = Modifier.height(60.dp))
            Box(
                modifier = Modifier
                    .size(height = 150.dp, width = 300.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { uriHandler.openUri("https://gitlab.fel.cvut.cz/azizoram/taskmanager") },
                    modifier = Modifier
                        .size(100.dp)
                        .border(2.dp, Color.Magenta)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.github__2_),
                        contentDescription = "Github",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }
    }
}




