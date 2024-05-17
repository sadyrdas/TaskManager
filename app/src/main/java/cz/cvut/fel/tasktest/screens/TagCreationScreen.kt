package cz.cvut.fel.tasktest.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.data.Tag
import cz.cvut.fel.tasktest.data.events.TagEvent
import cz.cvut.fel.tasktest.data.viewModels.TagViewModel


@Composable
fun TagCreationScreen(navController: NavHostController, drawerState: DrawerState, viewModel: TagViewModel){
    val state by viewModel.state.collectAsState()
    val backgroundColor = remember { mutableStateOf(Color.White) }
    var showConfirmDialogAboutDeleteBoard by remember { mutableStateOf(false) }
    var tagToBeDeleted by remember { mutableStateOf<Tag?>(null) }

    if (state.background.isNotBlank()) {
        try {
            backgroundColor.value = Color(android.graphics.Color.parseColor(state.background))
        } catch (e: IllegalArgumentException) {
            Log.e("TagCreationScreen", "Invalid color format: ${state.background}")
            // Optionally set a default color if parsing fails
            backgroundColor.value = Color.White
        }
    }

    LaunchedEffect(key1 = true) { // key1 = true ensures this only runs once when the composable enters the composition
        viewModel.fetchTags() // Call fetch boards if not automatically handled in ViewModel init
    }

    var showColorPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CustomAppBar(drawerState = drawerState,
            title = "Create Tag",
            backgroundColor = MaterialTheme.colorScheme.primary ,
            imageVector = Icons.Default.ArrowBack,
            navigationAction = {navController.popBackStack()} )}
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .height(50.dp)
                .width(400.dp),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                text = "Create your tags"
            )
            Spacer(modifier = Modifier.height(60.dp))
            Box(modifier =
            Modifier
                .border(
                    5.dp,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.extraLarge
                )
                .height(40.dp)
                .width(200.dp)
                .background(backgroundColor.value, MaterialTheme.shapes.extraLarge),
                contentAlignment = Alignment.Center){
                Text(
                    text = state.name,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                    textAlign = TextAlign.Center,
                    )

            }
            Spacer(modifier = Modifier.height(60.dp))
            TextField(
                value = state.name,
                onValueChange = { newName ->
                    viewModel.onEvent(TagEvent.SetTagName(newName))},
                placeholder = { Text("Tag Name")},
                modifier = Modifier
                    .height(60.dp)
                    .width(400.dp)
                    .align(Alignment.CenterHorizontally),
                textStyle = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(60.dp))
            if (!showColorPicker){
                Button(onClick = { showColorPicker = true }, modifier = Modifier.height(70.dp)) {
                    Text("Select color of background", color = Color.Black)
                }
            } else {
                Button(onClick = { showColorPicker = false }, modifier = Modifier.height(70.dp)) {
                    Text("Close colorPicker")
                }
            }

            if (showColorPicker){
                ColorPicker(viewModel)
            }
            Spacer(modifier = Modifier.height(60.dp))
            TextButton(
                onClick = {viewModel.onEvent(TagEvent.SaveTag)},
                modifier = Modifier
                    .background(Color(0xFF59D47B), shape = CircleShape)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Save Tag",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Divider(
                modifier = Modifier.padding(top = 12.dp),
                color = Color.Red,
            )
            Text(
                text = "Your Tags",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = MaterialTheme.typography.headlineMedium.fontWeight,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Column(modifier = Modifier.verticalScroll(rememberScrollState(), reverseScrolling = true)) {
                state.tags.forEach { tag ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    {
                        Text(
                            text = tag.name,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.shapes.extraLarge,
                                )
                                .height(40.dp)
                                .width(200.dp)
                                .background(Color(android.graphics.Color.parseColor(tag.background)))
                        )
                        IconButton(onClick = { showConfirmDialogAboutDeleteBoard = true; tagToBeDeleted = tag },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(30.dp)){
                            Icon(imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Tag Icon",
                            )
                        }
                    }

                    if (showConfirmDialogAboutDeleteBoard && tagToBeDeleted != null) {
                        AlertDialog(
                            onDismissRequest = {
                                showConfirmDialogAboutDeleteBoard = false
                                tagToBeDeleted = null
                            },
                            title = { Text("Confirmation") },
                            text = { Text("Are you sure you want to delete this tag?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        tagToBeDeleted?.let { tag ->
                                            showConfirmDialogAboutDeleteBoard = false
                                            viewModel.onEvent(TagEvent.DeleteTag(tag))
                                            tagToBeDeleted = null
                                        }
                                    }
                                ) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = {
                                        showConfirmDialogAboutDeleteBoard = false
                                        tagToBeDeleted = null
                                    }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPicker(viewModel: TagViewModel){
    val controller = rememberColorPickerController()
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 30.dp)
    ){
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlphaTile(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp)),
                controller = controller
            )
        }
        HsvColorPicker(modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .padding(10.dp)
            ,
            controller = controller,
            onColorChanged = { colorEnvelope ->
                viewModel.onEvent(TagEvent.SetTagBackground(colorEnvelope.hexCode))
            }
        )
    }

}
