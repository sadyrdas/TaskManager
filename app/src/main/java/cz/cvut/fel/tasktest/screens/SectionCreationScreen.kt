package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.data.Board
import cz.cvut.fel.tasktest.data.events.SectionEvent
import cz.cvut.fel.tasktest.data.repository.BoardDAO
import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.data.viewModels.SectionViewModel

@Composable
fun SectionCreationScreen(drawerState: DrawerState, boardId:Long, viewModel: BoardViewModel, viewSectionModel: SectionViewModel) {


    var boardState by remember { mutableStateOf<Board?>(null) }
    val sectionState by viewSectionModel.state.collectAsState()

    LaunchedEffect(boardId) {
        val board = viewModel.getBoardById(boardId)
        boardState = board
    }

    boardState?.let { board ->
        val deskName = board.title
    }

    Scaffold(
        topBar = {
            CustomAppBar(drawerState = drawerState, title = "Create section",
                backgroundColor = MaterialTheme.colorScheme.primaryContainer , imageVector = Icons.Default.Close )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(
                text = "Section of desk ${boardState?.title ?: ""}",
                modifier = Modifier
                    .padding(top = 40.dp)
                    .align(Alignment.CenterHorizontally),
            )
            sectionState.deskId = boardId
            TextField(
                value = sectionState.title,
                placeholder = { Text("Name of section") },
                onValueChange = {newNameOfSection -> viewSectionModel.onEvent(SectionEvent.SetSectionName(newNameOfSection))},
                modifier = Modifier
                    .padding(top = 16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
            )
            Button(
                onClick = {viewSectionModel.onEvent(SectionEvent.SaveSection)},
                modifier = Modifier
                    .padding(top = 560.dp)
            ) {
                Text("Create section")
            }
        }
    }

}
