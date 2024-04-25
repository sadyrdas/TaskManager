package cz.cvut.fel.tasktest.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.data.BoardEvent
import cz.cvut.fel.tasktest.data.BoardViewModel
import kotlinx.coroutines.launch

@Composable
fun BoardCreationScreen(drawerState: DrawerState, viewModel: BoardViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showPermissionDialog by remember { mutableStateOf(false) }
    var showNotValidFormDialog by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                viewModel.handleImageSelection(context, uri)
            }
        }
    )
    fun handleConfirmAboutValidForm(){
        if (state.title.isBlank() || state.background.isBlank()){
            showNotValidFormDialog = true
        }else{
            showNotValidFormDialog = false
            viewModel.onEvent(BoardEvent.SaveBoard)
            navController.navigate(MainRoute.BoardCreation.name)
        }

    }

    if (showNotValidFormDialog) {
        AlertDialog(
            onDismissRequest = { showNotValidFormDialog = false },
            title = { Text("Incomplete Form") },
            text = { Text("Please complete all fields before saving.") },
            confirmButton = {
                Button(onClick = { showNotValidFormDialog = false }) {
                    Text("OK")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            CustomAppBar(drawerState = drawerState, title = "Create Desk",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer, imageVector = Icons.Default.Close)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = state.title,
                onValueChange = { newTitle ->
                    viewModel.onEvent(BoardEvent.SetBoardTitle(newTitle))
                },
                placeholder = { Text("Desk name") },
                modifier = Modifier
                    .height(100.dp)
                    .width(300.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 40.dp),
                textStyle = MaterialTheme.typography.bodyLarge
            )
            if (showPermissionDialog) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user taps outside or on the back button
                        showPermissionDialog = false
                    },
                    title = { Text("Permission Needed") },
                    text = { Text("This app requires access to your photos to select a background image.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showPermissionDialog = false
                                imagePickerLauncher.launch("image/*") // Launch image picker after confirmation
                            }
                        ) {
                            Text("Continue")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showPermissionDialog = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
            val imageUri= state.background
            if (imageUri.isEmpty()){
                TextButton(onClick = { showPermissionDialog = true }) {
                    Text(text = "Select Background")
                }
            }else{
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Background",
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp)
                        .clickable { imagePickerLauncher.launch("image/**") }
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 40.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurface,
                            MaterialTheme.shapes.extraLarge
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            Button(onClick = {
                handleConfirmAboutValidForm()
            },
                modifier = Modifier.padding(top = 50.dp)) {
                Text(text = "Save")
            }
        }
    }
}
