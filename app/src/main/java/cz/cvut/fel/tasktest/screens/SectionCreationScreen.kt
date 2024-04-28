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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.tasktest.CustomAppBar

@Composable
fun SectionCreationScreen(drawerState: DrawerState) {

    val deskName = "Desk name"

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
                text = "Section of desk ${deskName}",
                modifier = Modifier
                    .padding(top = 40.dp)
                    .align(Alignment.CenterHorizontally),
            )
            TextField(
                value = "Name of section",
                onValueChange = {},
                modifier = Modifier
                    .padding(top = 16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
            )
            Button(
                onClick = {},
                modifier = Modifier
                    .padding(top = 560.dp)
            ) {
                Text("Create section")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun SectionCreationScreenPreview() {
    SectionCreationScreen(drawerState = DrawerState(DrawerValue.Closed))
}