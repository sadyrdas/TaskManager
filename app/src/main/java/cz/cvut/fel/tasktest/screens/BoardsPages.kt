package cz.cvut.fel.tasktest.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.R
import cz.cvut.fel.tasktest.data.events.BoardEvent
import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel


@Composable
fun ArticlesScreen(navController: NavHostController, viewModel: BoardViewModel, drawerState: DrawerState){
    val state by viewModel.state.collectAsState()
    var showButtons by remember { mutableStateOf(false) }
    var showConfirmDialogAboutDeleteBoard by remember { mutableStateOf(false) }
    var isAscendingOrder by remember { mutableStateOf(true) }


    LaunchedEffect(key1 = true) { // key1 = true ensures this only runs once when the composable enters the composition
        viewModel.fetchBoards() // Call fetch boards if not automatically handled in ViewModel init
    }
    Scaffold(
        topBar = {
            CustomAppBar(
                drawerState = drawerState,
                title = "Boards",
                imageVector = Icons.Filled.Menu,
                backgroundColor = MaterialTheme.colorScheme.primary

            )
        },
        floatingActionButton = {
            ExpandableFloatingActionButton(showButtons, onToggle = { showButtons = it }, navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Text(
                    text = "Recent boards",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (isAscendingOrder) {
                            viewModel.sortBoardsByTitleAsc()
                        } else {
                            viewModel.sortBoardsByTitleDesc()
                        }
                        isAscendingOrder = !isAscendingOrder
                    }
                ) {
                    // Display appropriate icon based on sorting direction
                    val iconResourceId = if (isAscendingOrder) R.drawable.arrowup else R.drawable.arrowdown
                    Icon(
                        painter = painterResource(id = iconResourceId),
                        contentDescription = if (isAscendingOrder) "Sort by name ASC" else "Sort by name DESC",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(top = 12.dp),
                color = Color.DarkGray,
            )
            state.boards.forEach { board ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { navController.navigate("${MainRoute.CurrentBoard.name}/${board.id}") }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(board.background),
                        contentDescription = "Board Background",
                        modifier = Modifier.width(100.dp).height(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = board.title,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))

                    if (showConfirmDialogAboutDeleteBoard){
                        AlertDialog(
                            onDismissRequest = {
                                showConfirmDialogAboutDeleteBoard = false
                            },
                            title = { Text("Confirmation") },
                            text = { Text("Are you sure you want to delete this board?.") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        showConfirmDialogAboutDeleteBoard = false
                                        viewModel.onEvent(BoardEvent.DeleteBoard(board))
                                    }
                                ) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showConfirmDialogAboutDeleteBoard = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    IconButton(onClick = {
                        showConfirmDialogAboutDeleteBoard = true
                    },
                        modifier = Modifier
                            .size(48.dp),
                        ) {
                        Icon(imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Board Icon",
                        )
                    }
                }
                Divider(
                    modifier = Modifier.padding(top = 12.dp),
                    color = Color.DarkGray,
                )

            }

        }
    }
}


@Composable
fun ExpandableFloatingActionButton(showButtons: Boolean, onToggle: (Boolean) -> Unit, navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        // Animated Visibility for additional icons
        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top), // Animates in from the top
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top) // Animates out towards the top
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(onClick = { navController.navigate(MainRoute.BoardCreation.name) },
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = Color(0xFF59D47B), ShapeDefaults.ExtraLarge)
                        .padding(16.dp, 16.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.boardsicon),
                        contentDescription = "Create Board"
                    )
                }

                 // Space between buttons

                IconButton(onClick = { navController.navigate("${MainRoute.TaskCreation.name}/null") },
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = Color(0xFF59D47B), ShapeDefaults.ExtraLarge)
                        .padding(16.dp, 16.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.taskicon),
                        contentDescription = "Other Action"
                    )
                }
            }
        }

        // Main FloatingActionButton always visible at the bottom
        FloatingActionButton(
            onClick = { onToggle(!showButtons) },
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = Color(0xFF59D47B), ShapeDefaults.ExtraLarge)
                        .padding(16.dp, 16.dp),
        ) {
            if (showButtons){
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Add Board Icon",
                    modifier = Modifier.background(color = Color(0xFF59D47B))
                )
            }else{
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Board Icon",
                    modifier = Modifier.background(color = Color(0xFF59D47B))
                )
            }
        }
    }
}