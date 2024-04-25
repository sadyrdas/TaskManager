package cz.cvut.fel.tasktest.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.tasktest.CustomAppBar
import cz.cvut.fel.tasktest.data.Task
import cz.cvut.fel.tasktest.data.TaskDAO
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskScreen(drawerState: DrawerState) {
    var isEditingDescription by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isDrawerOpen by remember { mutableStateOf(false) }
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)



    Scaffold(
        topBar = {
            CustomAppBar(
                drawerState = drawerState,
                title = "Task",
                backgroundColor = MaterialTheme.colorScheme.primary,
                imageVector = Icons.Default.ArrowBack// Здесь указываем цвет
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Box(){
                AsyncImage(
                    model = "https://www.istockphoto.com/resources/images/Homepage/Hero/1204187829.jpg",
                    contentDescription = "Board Background",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                )

            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp) // Отступы слева и сверху
            ) {
                Text(
                    text = "Quick Actions",
                    modifier = Modifier.weight(1f) // Растягиваем текст на всю доступную ширину
                )
                Spacer(modifier = Modifier.width(8.dp)) // Отступ между текстом и иконкой
                Icon(
                    imageVector = if (isDropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Filter Icon",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable(onClick = { isDropdownExpanded = !isDropdownExpanded }) // Изменение состояния при нажатии
                )
            }
            if (isDropdownExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = { /* Действие при нажатии на первую кнопку */ },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f)
                            .width(150.dp)
                    ) {
                        Text("First Button")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = { /* Действие при нажатии на вторую кнопку */ },
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .weight(1f)
                            .width(150.dp)
                    ) {
                        Text("Second Button")
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(2.dp),
                color = Color.Red, // Цвет разделителя
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Icon(
                    Icons.Filled.List, contentDescription = "Description Icon",
                    modifier = Modifier.padding(end = 16.dp)
                )



                Column(modifier = Modifier.weight(1f)) {
                    if (isEditingDescription) {
                        TextField(
                            value = "",
                            onValueChange = {  },
                            modifier = Modifier
                                .fillMaxWidth()
                                .width(320.dp)
                        )
                    } else {
                        TextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier
                                .clickable { isEditingDescription = true }
                                .width(320.dp)
                        )
                    }
                }
            }
            Divider(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(2.dp),
                color = Color.Red, // Цвет разделителя
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp)
            ) {
                Icon(
                    Icons.Filled.Warning, contentDescription = "Tag Icon",
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text(
                    text = "Tags..",
                    modifier = Modifier.clickable { /*...*/ }
                )
            }
            Divider(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .height(2.dp),
                color = Color.Red, // Цвет разделителя
            )
            //TODO логика для даты начала и окончания(если они есть - показать)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.DateRange, contentDescription = "Tag Icon",
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = "Starting..",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Divider(modifier = Modifier
                        .padding(bottom = 4.dp)
                        .width(320.dp))
                    Text(text = "Date of end")
                }
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .weight(1f)
            ) {
                // Круглая маленькая аватарка
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    // Добавьте изображение вашего аватара сюда
                }

                // Текстовое поле с плейсхолдером "Add comment"
                TextField(
                    value = "", // Ваше значение комментария
                    onValueChange = { /* Обработчик изменения значения комментария */ },
                    placeholder = { Text("Add comment") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    shape = MaterialTheme.shapes.extraLarge
                )

                // Иконка отправки
                IconButton(
                    onClick = { /* Действие при отправке комментария */ }
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.Black // Цвет иконки
                    )
                }

                // Иконка вложения
                IconButton(
                    onClick = { isDrawerOpen = !isDrawerOpen }
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "Attachment",
                        tint = Color.Black // Цвет иконки
                    )
                }
            }
        }
    }
}
@Preview(showBackground =  true)
@Composable
fun TaskScreenPreview() {
    TaskScreen(drawerState = rememberDrawerState(DrawerValue.Closed))
}