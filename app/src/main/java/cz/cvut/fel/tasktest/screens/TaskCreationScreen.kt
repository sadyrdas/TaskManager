package cz.cvut.fel.tasktest.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.data.Board
import cz.cvut.fel.tasktest.data.BoardEvent
import cz.cvut.fel.tasktest.data.BoardViewModel
import cz.cvut.fel.tasktest.data.SectionViewModel
import cz.cvut.fel.tasktest.data.TagEvent
import cz.cvut.fel.tasktest.data.TaskEvent
import cz.cvut.fel.tasktest.data.TaskState
import cz.cvut.fel.tasktest.data.TaskViewModel
import cz.cvut.fel.tasktest.data.TaskifyDatabase

@Composable
fun TaskCreationScreen(drawerState: DrawerState, viewModel: BoardViewModel, taskViewModel: TaskViewModel, sectionViewModel: SectionViewModel) {

    val boardsState by viewModel.state.collectAsState()
    // Заполняем список items названиями бордов из состояния
    val items = boardsState.boards.map { it.title }
    val sectionList = boardsState.sections.map { it.title }

    val sectionState by sectionViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CustomAppBar(drawerState = drawerState, title = "Create Task",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer , imageVector = Icons.Default.Close )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(0.dp)
                .fillMaxWidth()
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                DropDown("Desk", items)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                DropDown("Section", sectionList)
            }

            Column(modifier = Modifier
                .padding(36.dp)

            )
            {
                androidx.compose.material3.TextField(
                    value = "AAAAAA",
                    onValueChange = { },
                    modifier = Modifier
                        .width(320.dp)
                        .padding(top = 16.dp)
                )
                androidx.compose.material3.TextField(
                    value = "BBBBBB",
                    onValueChange = { },
                    modifier = Modifier
                        .width(320.dp)
                        .padding(top = 16.dp)
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(3.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Filled.DateRange, contentDescription = "Tag Icon",
                        modifier = Modifier.padding(end = 16.dp),
                    )
                    Column {
                        Text(
                            text = "Starting..",
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                        Divider(modifier = Modifier
                            .padding(bottom = 4.dp)
                            .width(250.dp))
                        Text(text = "Date of end")
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(3.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 26.dp)
                ) {
                    Icon(
                        Icons.Filled.AddCircle, contentDescription = "Tag Icon",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Text(
                        text = "Add attachment",
                        modifier = Modifier.clickable { /*...*/ }
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(3.dp)
                )

                Button(onClick = { taskViewModel.onEvent(TaskEvent.SaveTask) },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 50.dp)) {
                    Text(text = "Save")
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(label:String, items: List<String>){
    var isExpanded by remember { mutableStateOf(false) }

    var selectedText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it })
    {
        TextField(
            value = selectedText,
            onValueChange = {},
            label = { Text(text =label,
                modifier = Modifier.padding(end = 8.dp))
                    },
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )

        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false},
        ) {
            items.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = { Text(text = text) },
                    onClick = {
                        selectedText = items[index]
                        isExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

