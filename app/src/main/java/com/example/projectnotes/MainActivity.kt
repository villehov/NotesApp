package com.example.projectnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val notesList = remember {
        mutableStateListOf(
            NotesItem(1, "Note1aaaaa", "do something"),
            NotesItem(2, "Note2", "do something else")
        )
    }

    NavHost(navController = navController, startDestination = "notesList") {
        composable("notesList") { NotesListScreen(navController, notesList) }
        composable("addListItem") { AddListItemScreen(navController, notesList) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(navController: NavController, notesList: MutableList<NotesItem>) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Notes")
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
            modifier = Modifier.padding(paddingValues)
        ) {
            items(notesList) { note ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "${note.header}: ${note.description}")
                    Divider(thickness = 2.dp)
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Add Note")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = header.value,
                onValueChange = { header.value = it },
                label = { Text("Header") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (header.value.isNotBlank() && description.value.isNotBlank()) {
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
                        // Handle empty fields
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Note")
            }
        }
    }
}
/*
@Composable
@Preview(showBackground = true)
fun Preview() {
    val navController = rememberNavController()
    val notesList = remember {
        mutableStateListOf(
            NotesItem(1, "Note1", "do something"),
            NotesItem(2, "Note2", "do something else")
        )
    }

    NavHost(navController = navController, startDestination = "notesList") {
        composable("notesList") { NotesListScreen(navController, notesList) }
        composable("addListItem") { AddListItemScreen(navController, notesList) }
    }
}
*/