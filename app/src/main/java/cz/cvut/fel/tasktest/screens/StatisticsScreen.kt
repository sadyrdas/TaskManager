package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.data.Section
import cz.cvut.fel.tasktest.data.TaskifyDatabase
import cz.cvut.fel.tasktest.data.viewModels.BoardViewModel
import cz.cvut.fel.tasktest.ui.theme.Purple80
import cz.cvut.fel.tasktest.ui.theme.PurpleGrey80

@Composable
fun StatisticsScreen(navController: NavHostController, viewModel: BoardViewModel, drawerState: DrawerState){
//fun StatisticsScreen(navController: NavHostController, viewModel: BoardViewModel, tagViewModel: TagViewModel, taskViewModel: TaskViewModel, drawerState: DrawerState){
    val state by viewModel.state.collectAsState()
    var showButtons by remember { mutableStateOf(false) }
    val (drawerStateForFilter, setDrawerStateForFilter) = remember { mutableStateOf(false) }
//    val db = TaskifyDatabase.getDatabase(LocalContext.current)
//    val taskdao=db.taskDao()
//    val sectiondao=db.sectionDAO()
//    val tasks = db.taskDao().getAllTasks()
    val tagMap = mutableMapOf<String,Int>()
//    tagMap["Finished"] = 0
//    tasks.forEach {
//        val tags = taskdao.getTagsForTask(it.id)
//
//        if (taskFinished(sectiondao.getById(it.sectionId))){
//            tagMap["Finished"] = tagMap.getOrDefault("Finished", 0) + 1
//        }
//
//        tags.forEach {tag ->
//            tagMap[tag.name] = tagMap.getOrDefault(tag.name, 0) + 1
//        }
//    }

    LaunchedEffect(key1 = true) { // key1 = true ensures this only runs once when the composable enters the composition
        viewModel.fetchBoards() // Call fetch boards if not automatically handled in ViewModel init
    }
    Scaffold(
        topBar = {
            CustomAppBar(
                drawerState = drawerState,
                title = "Statistics",
                imageVector = Icons.Filled.Menu,
                backgroundColor = MaterialTheme.colorScheme.primary// Здесь указываем цвет
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

            }

            TagStatisticsPage(tagMap)

        }


    }
}

fun taskFinished(byId: Section): Boolean {
    return (byId.title.lowercase().replace("\\s".toRegex(), "")
            == "done")
}

@Preview
@Composable
fun StatisticsScreenPreviewGoessBrr(){
    val context = LocalContext.current
    val db by lazy{
        Room.databaseBuilder(
            context,
            TaskifyDatabase::class.java,
            "tasktest-db"
        ).build()
    }
    val tagsAmountMap = mapOf("All" to 3, "Some" to 2, "Mb" to 1)

    val boardViewModel = BoardViewModel(db.boardDao())
    StatisticsScreen(navController = rememberNavController(), boardViewModel, rememberDrawerState(
        initialValue = DrawerValue.Closed
    ))
}

@Composable
fun TagStatisticsPage(tagMap: MutableMap<String, Int>) {
    val tagsAmountList: List<Pair<String, Int>> = tagMap.toList()


    val column1Weight = .80f // %
    val column2Weight = .20f // %
    Box(
        modifier = Modifier.background(Color.White, RoundedCornerShape(32))
    ){
    // The LazyColumn will be our table. Notice the use of the weights below
        LazyColumn(Modifier.fillMaxSize().padding(16.dp).clip(shape = RoundedCornerShape(12.dp))) {
            // Here is the header
            item {
                Row(Modifier.background(Purple80)) {
                    TableCell(text = "Tag", weight = column1Weight)
                    TableCell(text = "Count#", weight = column2Weight)
                }


            }
            items(tagsAmountList) { item ->
                StatisticRow(item, column1Weight, column2Weight)
            }
        }
    }


}

@Composable
fun StatisticRow(item: Pair<String, Int>, column1Weight: Float, column2Weight: Float){
    val (tag, amount) = item
    Row(Modifier.fillMaxWidth()) {
        TableCell(text = tag, weight = column1Weight )
        TableCell(text = amount.toString(), weight = column2Weight)
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
//    height: Dp
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, PurpleGrey80)
            .weight(weight)
            .padding(8.dp)
//            .height(height)
    )
}