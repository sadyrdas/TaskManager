package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute

@Composable
fun SettingsScreen(navController: NavHostController, drawerState: DrawerState) {
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
                text = AnnotatedString("Enable notifications"),
                modifier = Modifier
                    .height(70.dp)
                    .width(400.dp)
                    .padding(start = 10.dp, top = 20.dp),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                ),
                onClick = {
                    // Handle click
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
                    // Handle click
                }
            )
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

