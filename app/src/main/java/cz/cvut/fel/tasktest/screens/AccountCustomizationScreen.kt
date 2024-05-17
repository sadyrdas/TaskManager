package cz.cvut.fel.tasktest.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.data.events.UserEvent
import cz.cvut.fel.tasktest.data.viewModels.UserViewModel


@Composable
fun AccountCustomizationScreen(navController: NavHostController, drawerState: DrawerState, viewModel: UserViewModel) {
    val userState by viewModel.userState.collectAsState()

    LaunchedEffect(key1 = null) {
        viewModel.fetchUser()
    }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.handleImageSelection(context, uri)
            }
        }
    )

    Scaffold(
        topBar = {
            CustomAppBar(
                drawerState = drawerState,
                title = "Your Account",
                backgroundColor = MaterialTheme.colorScheme.primary,
                imageVector = Icons.Default.ArrowBack,
                navigationAction = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            userState?.let { user ->
                val imageUri = user.background
                val userName = user.userName

                if (imageUri.isNotBlank()) {
                    // Display user profile picture or default icon
                    Image(
                        modifier = Modifier.size(250.dp),
                        painter = rememberAsyncImagePainter(imageUri),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                } else {
                    // Display default icon
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User profile picture",
                        modifier = Modifier.size(250.dp)
                    )
                }
                Text(
                    modifier = Modifier
                        .height(50.dp)
                        .width(500.dp),
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    text = "Customize your account"
                )
                Text(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .height(40.dp)
                        .width(500.dp)
                        .padding(start = 10.dp, top = 10.dp),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = FontWeight.Bold,
                    text = "Change your username",
                )

                // Text field to change username
                TextField(
                    value = userName,
                    onValueChange = { newText -> viewModel.onEvent(UserEvent.SetUsername(newText)) },
                    placeholder = { Text("Write your username") },
                    modifier = Modifier
                        .height(50.dp)
                        .width(500.dp)
                )

                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
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
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        Modifier
                            .size(30.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add profile photo"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))


                // Button to save changes
                TextButton(
                    onClick = { viewModel.onEvent(UserEvent.SaveUser);navController.navigate(MainRoute.Boards.name) },
                    modifier = Modifier
                        .background(Color(0xFF59D47B), shape = CircleShape)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Save",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

