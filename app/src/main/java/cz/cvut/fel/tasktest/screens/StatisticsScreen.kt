package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.data.Section
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.states.SectionState
import cz.cvut.fel.tasktest.data.states.TagState
import cz.cvut.fel.tasktest.data.states.TaskState
import cz.cvut.fel.tasktest.data.viewModels.SectionViewModel
import cz.cvut.fel.tasktest.data.viewModels.TagViewModel
import cz.cvut.fel.tasktest.data.viewModels.TaskViewModel
import cz.cvut.fel.tasktest.ui.theme.Purple80
import cz.cvut.fel.tasktest.ui.theme.PurpleGrey80
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.Calendar
import java.util.TimeZone

const val FNSHD_STRING = "VERYUNLIKELYTOCALLTAGTHATWAYSOIT'SOKTOKEEPFINISHEDCOUNTINHERE"

@Composable
fun StatisticsScreen(navController: NavHostController, taskViewModel: TaskViewModel, tagViewModel: TagViewModel, sectionViewModel: SectionViewModel, drawerState: DrawerState) {
    // Define a coroutine scope
    val taskState by taskViewModel.state.collectAsState()
    val tagState by tagViewModel.state.collectAsState()
    val sectionState by sectionViewModel.state.collectAsState()
    val sections = sectionViewModel.state.collectAsState().value.sections
    val (selectedOption, onOptionSelected) = remember { mutableStateOf("Yearly") }

    var tagMap by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val currentPage = navController.currentDestination

    LaunchedEffect(key1 = true) {
        taskViewModel.fetchTasks()
        tagViewModel.fetchTags()
    }
    LaunchedEffect (key1 = sectionState, key2 = selectedOption) {
        tagMap = populateTagMap(taskViewModel, tagViewModel, taskState, tagState, sectionViewModel, sectionState, sections, selectedOption)

    }


    TagStatisticsPage(tagMap, drawerState, onOptionSelected, selectedOption)

}

fun populateTagMap(
    taskViewModel: TaskViewModel,
    tagViewModel: TagViewModel,
    taskState: TaskState,
    tagState: TagState,
    sectionViewmodel: SectionViewModel,
    sectionState: SectionState,
    sections: List<Section>,
    selectedOption: String
): Map<String, Int> {

    val tagMap = mutableMapOf<String, Int>()
    tagMap[FNSHD_STRING] = 0
    val tasks = filterTasks(taskState.tasks, selectedOption)
    sectionViewmodel.fetchSectionsByIds(tasks.map { it.sectionId })

    if (sections.isEmpty()){
        return tagMap;
    }
    tagViewModel.fetchTags()
    tagViewModel.state.value.tags.forEach {
        tagMap[it.name] = 0
    }

    val sectionMap = mutableMapOf<Long, Section>()

    sections.forEach { sectionMap[it.id] = it }

    tasks.forEach { task ->
        taskViewModel.fetchTagsForTask(task.id)

        val taskSection = sectionMap[task.sectionId]
        if (taskSection != null && taskFinished(taskSection)) {
            tagMap[FNSHD_STRING] = tagMap.getOrDefault(FNSHD_STRING, 0) + 1
        }
        taskViewModel.fetchTagsForTask(task.id)

        val tagsForTask = taskViewModel.tagsForTask.value
        tagsForTask.forEach { tag ->
            val tagName = tag.name
            tagMap[tagName] = tagMap.getOrDefault(tagName, 0) + 1
        }
    }

    return tagMap
}

fun filterTasks(tasks: List<Task>, selectedOption: String): List<Task> {
    val currentTimeMillis = Calendar.getInstance().timeInMillis
    val calendar = Calendar.getInstance()

    // Calculate cutoff time based on selected option
    val timeInMillis = when (selectedOption) {
        "Monthly" -> calendar.apply { add(Calendar.MONTH, -1) }.timeInMillis
        "Quarterly" -> calendar.apply { add(Calendar.MONTH, -3) }.timeInMillis
        "Semi-annual" -> calendar.apply { add(Calendar.MONTH, -6) }.timeInMillis
        "Yearly" -> calendar.apply { add(Calendar.YEAR, -1) }.timeInMillis
        else -> throw IllegalArgumentException("Invalid option")
    }

    // Filter tasks based on startDate
    val filteredTasks = tasks.filter { task ->
        val startDate = task.startDate
        if (startDate.isNullOrEmpty()) {
            selectedOption == "Yearly" // ehehee
        } else {
            val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy")
            dateFormat.timeZone = TimeZone.getTimeZone("GMT+01:00")
            val date = dateFormat.parse(startDate)

            (date?.time ?: 0) > System.currentTimeMillis() - timeInMillis
        }
    }

    return filteredTasks
}

fun taskFinished(byId: Section?): Boolean {
    return (byId?.title?.lowercase()?.replace("\\s".toRegex(), "") ?: "") == "done"
}


@Composable
fun TagStatisticsPage(
    tagMap: Map<String, Int>,
    drawerState:DrawerState,
    onOptionSelected: (String) -> Unit,
    selectedOption: String
) {
    val finished = tagMap[FNSHD_STRING] ?: 0

    val finEntry: Pair<String, Int> = Pair("Finished", finished);

    val tagsAmountList: List<Pair<String, Int>> = tagMap.filter { entry -> entry.key!= FNSHD_STRING }.toList()

    val column1Weight = .80f // %
    val column2Weight = .20f // %

    Scaffold(
        topBar = {
            CustomAppBar(
                drawerState = drawerState,
                title = "Boards",
                imageVector = Icons.Filled.Menu,
                backgroundColor = MaterialTheme.colorScheme.primary// Здесь указываем цвет
            )
        }) {
        Column {
            SelectorRow(it, onOptionSelected, selectedOption)
            Box(
                modifier = Modifier.background(Color.White, RoundedCornerShape(32))
            ) {
                // The LazyColumn will be our table. Notice the use of the weights below
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(PaddingValues(16.dp))
                        .clip(shape = RoundedCornerShape(12.dp))
                ) {
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
                    item {
                        StatisticRow(finEntry, column1Weight, column2Weight)
                    }
                }
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

@Composable
fun SelectorRow(paddingValues: PaddingValues, onOptionSelected: (String) -> Unit, selectedOption: String) {
    val options = listOf("Monthly", "Quarterly", "Semi-annual", "Yearly")

    val rows = 2 // Number of rows in the grid
    val columns = (options.size + rows - 1) / rows // Number of columns

    Column(Modifier.padding(paddingValues)) {
        Row(Modifier.padding(4.dp)) {
            Text(text = "Select period:", color = Color.Black, style = TextStyle(fontSize = 24.sp))
        }

        repeat(rows) { rowIndex ->
            Row(Modifier.padding(vertical = 4.dp)) {
                repeat(columns) { colIndex ->
                    val optionIndex = rowIndex * columns + colIndex
                    if (optionIndex < options.size) {
                        val option = options[optionIndex]
                        val isSelected = (option == selectedOption)
                        RadioButton(
                            selected = isSelected,
                            onClick = { onOptionSelected(option) }
                        )
                        Text(
                            text = option,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                                .align(Alignment.CenterVertically), // Align text vertically
                            color = if (isSelected) Color.Black else Color.Gray,
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
//@Preview
//@Composable
////fun prewiv(){
////    val tagMap = mutableMapOf<String, Int>()
////    val drawerState = DrawerState(DrawerValue.Closed)
////
////    TagStatisticsPage(tagMap, drawerState, (){inpt->"A"}, )
////}