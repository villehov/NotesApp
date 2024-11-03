package com.example.projectnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectnotes.ui.theme.ProjectNotesTheme

data class NotesItem(
    val id: Int,
    var header: String,
    var description: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectNotesTheme {
                NotesApp()
            }
        }
    }
}

@Composable
fun NotesApp() {
    val navController = rememberNavController()
    val notesList = remember { mutableStateListOf<NotesItem>() }

    NavHost(navController = navController, startDestination = "notesList") {
        composable("notesList") { NotesListScreen(navController, notesList) }
        composable("addListItem") { AddListItemScreen(navController, notesList) }
        composable("detailedViewListItem/{noteId}") {
            DetailedViewListItem(navController, it.arguments?.getString("noteId")?.toInt() ?: 0, notesList)
        }
        composable("editListItem/{noteId}") {
            EditListItemScreen(navController, it.arguments?.getString("noteId")?.toInt() ?: 0, notesList)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(navController: NavController, notesList: MutableList<NotesItem>) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addListItem") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add note")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            items(notesList) { note ->
                Column(modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 5.dp,
                    )

                ) {
                    Text( text = note.header , style = MaterialTheme.typography.headlineMedium )
                    Divider(thickness = 2.dp)
                }
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                ) {

                    SmallFloatingActionButton(
                        onClick = {
                            notesList.remove( note )
                        }
                    ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete note")
                        }

                    SmallFloatingActionButton(
                        onClick = {
                            navController.navigate("detailedViewListItem/${note.id}")
                        }
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "Detailed view note")
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            navController.navigate("editListItem/${note.id}")
                        }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit view note")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditListItemScreen(navController: NavController, noteId: Int, notesList: MutableList<NotesItem>) {
    val note = notesList.find { it.id == noteId }

    if (note == null) {
        navController.popBackStack()
        return
    } else {
        val header = remember { mutableStateOf(note.header) }
        val description = remember { mutableStateOf(note.description) }
        val errorMsg = remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Edit Note",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.popBackStack() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(Icons.Filled.Home, "Redirect to home screen")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    OutlinedTextField(
                        value = header.value,
                        onValueChange = { header.value = it },
                        label = { Text("Header") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description.value,
                        onValueChange = { description.value = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = errorMsg.value,
                        color = MaterialTheme.colorScheme.error
                    )

                    Button(
                        onClick = {
                            if (header.value.isNotBlank() && description.value.isNotBlank() &&
                                header.value.length <= 20 && description.value.length <= 200 &&
                                header.value.length >= 3 && description.value.length >= 10) {

                                note.header = header.value
                                note.description = description.value

                                navController.popBackStack()
                            } else {
                                when {
                                    header.value.length < 3 -> errorMsg.value = "Header must be at least 3 characters"
                                    description.value.length < 10 -> errorMsg.value = "Description must be at least 10 characters"
                                    header.value.length > 20 -> errorMsg.value = "Header must be at most 20 characters"
                                    description.value.length > 100 -> errorMsg.value = "Description must be at most 100 characters"
                                    else -> errorMsg.value = "Both header and description is needed"
                                }
                            }
                        }
                    ) {
                        Text("Save changes")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedViewListItem(navController: NavController, noteId: Int, notesList: MutableList<NotesItem>) {

    val note = notesList.find { it.id == noteId }

    if (note == null) {
        navController.popBackStack()
        return
    }
    else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Detailed View Note",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.popBackStack() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Icon(Icons.Filled.Home, "Small floating action button.")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${note.header}      ID:${note.id}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Divider(thickness = 2.dp)
                    Text(
                        text = note.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListItemScreen(navController: NavController, notesList: MutableList<NotesItem>) {
    val header = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    var errorMsg = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Note",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.popBackStack() },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary,
            ) {
                Icon(Icons.Filled.Home, "Button to homepage.")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = header.value,
                    onValueChange = { header.value = it },
                    label = { Text("Header") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description.value,
                    onValueChange = { description.value = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = errorMsg.value,
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = {
                        if (header.value.isNotBlank() && description.value.isNotBlank() &&
                            header.value.length <= 20 && description.value.length <= 200 &&
                            header.value.length >= 3 && description.value.length >= 10
                        ) {

                            val newNote = NotesItem(
                                id = (notesList.size + 1),
                                header = header.value,
                                description = description.value
                            )
                            notesList.add(newNote)

                            header.value = ""
                            description.value = ""

                            navController.popBackStack()
                        } else {
                            // kotlin case loop; when {}
                            when {
                                header.value.length < 3 -> errorMsg.value =
                                    "Header must be at least 3 characters"

                                description.value.length < 10 -> errorMsg.value =
                                    "Description must be at least 10 characters"

                                header.value.length > 20 -> errorMsg.value =
                                    "Header must be at most 20 characters"

                                description.value.length > 100 -> errorMsg.value =
                                    "Description must be at most 100 characters"

                                else -> errorMsg.value = "Both header and description is needed"
                            }
                        }
                    },
                ) {
                    Text("Add Note")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Preview() {
    val navController = rememberNavController()
    val notesList = remember { mutableStateListOf<NotesItem>() }

    NavHost(navController = navController, startDestination = "notesList") {
        composable("notesList") { NotesListScreen(navController, notesList) }
        composable("addListItem") { AddListItemScreen(navController, notesList) }
        composable("detailedViewListItem/{noteId}") {
            DetailedViewListItem(navController, it.arguments?.getString("noteId")?.toInt() ?: 0, notesList)
        }
        composable("editListItem/{noteId}") {
            EditListItemScreen(navController, it.arguments?.getString("noteId")?.toInt() ?: 0, notesList)
        }
    }
}
