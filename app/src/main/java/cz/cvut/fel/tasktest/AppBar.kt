package cz.cvut.fel.tasktest

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import cz.cvut.fel.tasktest.ui.theme.JetpackComposeDrawerNavigationTheme
import kotlinx.coroutines.launch

@Composable
fun CustomAppBar(drawerState: DrawerState?, title: String, backgroundColor: Color, imageVector: ImageVector,navigationAction: (() -> Unit)? = null) {
    val coroutineScope = rememberCoroutineScope()

    Surface(
        color = backgroundColor,
    ) {
        CenterAlignedTopAppBar(
            backgroundColor = backgroundColor,
            navigationIcon = {
                if (navigationAction != null) {
                    IconButton(onClick = navigationAction) {
                        Icon(
                            imageVector = imageVector,
                            contentDescription = ""
                        )
                    }
                } else {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState?.open()
                        }
                    }) {
                        Icon(
                            imageVector = imageVector,
                            contentDescription = ""
                        )
                    }
                }
            },
            title = { Text(text = title) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit
) {
    if (navigationIcon != null) {
        TopAppBar(
            modifier = modifier,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.contentColorFor(Color.Green)
            ),
            navigationIcon = navigationIcon,
            actions = actions,
            title = title
        )
    }
}
@Preview(widthDp = 300)
@Composable
fun PreviewCustomAppBar() {
    JetpackComposeDrawerNavigationTheme {
        CustomAppBar(drawerState = null, title = "Title", backgroundColor = MaterialTheme.colorScheme.primary, imageVector = Icons.Filled.Menu)
    }
}
