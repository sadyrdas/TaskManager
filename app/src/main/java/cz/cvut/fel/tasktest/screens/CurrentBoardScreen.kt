package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.MainRoute
import cz.cvut.fel.tasktest.R
import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.data.viewModels.SectionViewModel

@Composable
fun CurrentBoardScreen(navController: NavHostController, drawerState: DrawerState, boardViewModel: BoardViewModel, sectionViewModel: SectionViewModel, boardId:Long) {


    val boardState by boardViewModel.boardState


    // Fetch the board state when the screen is first composed
    LaunchedEffect(boardId) {
        boardViewModel.getBoardState(boardId)
    }

    LaunchedEffect(boardId) {
        sectionViewModel.fetchSections(boardId)
    }



    val title = boardState?.title ?: ""
    val sections = sectionViewModel.state.collectAsState().value.sections

    Scaffold(
        topBar = {
            CustomAppBar(drawerState = drawerState, title = title,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer , imageVector = Icons.Default.Close )
        },
        floatingActionButton = {
            FloatingButton(navController, boardId)
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()

        ) {
        sections.forEach(){
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)

            ) {
                Column{
                    Text(text = it.title)
                }
            }
        }


        }
    }

}

@Composable
fun FloatingButton(navController: NavHostController, boardId: Long) {
    FloatingActionButton(
        onClick = {
            navController.navigate("${MainRoute.SectionCreation.name}/$boardId")
        },
        modifier = Modifier
            .size(56.dp)
            .background(color = Color(0xFF59D47B), ShapeDefaults.ExtraLarge)
            .padding(16.dp, 16.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.sections),
            contentDescription = "Add Section Icon",
            modifier = Modifier.background(color = Color(0xFF59D47B))
        )
    }
}