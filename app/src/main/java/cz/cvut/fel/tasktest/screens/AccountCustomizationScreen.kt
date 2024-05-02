package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.data.events.UserEvent
import cz.cvut.fel.tasktest.data.viewModels.UserViewModel


@Composable
fun AccountCustomizationScreen(navController: NavHostController, drawerState: DrawerState, viewModel : UserViewModel) {
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = { CustomAppBar(drawerState = drawerState, title = "YourAccount",
            backgroundColor = MaterialTheme.colorScheme.primary ,
            imageVector = Icons.Default.ArrowBack,
            navigationAction = {navController.popBackStack()} )})
    { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                modifier = Modifier.size(250.dp),
                imageVector = Icons.Filled.AccountCircle,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Text(modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .height(50.dp)
                .width(400.dp),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                text = "Customize your account"
            )
            Text(modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .height(40.dp)
                .width(400.dp)
                .padding(start = 10.dp, top = 10.dp),
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
                text = "Change your username",
            )
            TextField(
                value = state.userName,
                onValueChange = { newText -> viewModel.onEvent(UserEvent.SetUsername(newText)) },
                placeholder = { Text("Write your username")},
                modifier = Modifier
                    .height(70.dp)
                    .width(400.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
            )
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .height(60.dp)
                    .padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
            ) {
                Text(
                    text = "Change your profile photo",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 300.dp)
                )
                IconButton(onClick = { /*TODO*/ },
                    Modifier
                        .size(30.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            ShapeDefaults.ExtraLarge
                        )){
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add profile photo",
                    )
                }
            }
            Button(onClick = { viewModel.clearAllUsers()},) {
                Text(text = "Save")
            }
        }
    }

}