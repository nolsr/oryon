package com.example.oryon.ui.screens.challenge

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.oryon.R
import com.example.oryon.data.ChallengeData
import com.example.oryon.data.getChallengeTypeText
import com.example.oryon.ui.screens.activity.ActivityViewModel
import com.mapbox.maps.extension.style.expressions.dsl.generated.color

@Composable
fun ChallengeScreen( viewModel: ChallengeViewModel, navController: NavController) {
    val challenges by viewModel.challenges.collectAsState()

    //State ob Dialog angezeigt wird oder nicht
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ChallengeList(challenges, navController)

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Mitglied hinzufügen")
        }

        if (showAddDialog) {
            AddChallengeDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, type, target ->
                    viewModel.addChallenge(name, type, target)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ChallengeList(challenges: List<ChallengeData>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(challenges) { challenge ->
            ChallengeCard(
                imageRes = R.drawable.challenge_hero_1,
                title = challenge.name,
                subtitle = challenge.type
            ) {
                Text(text = getChallengeTypeText(challenge.type), style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(8.dp))

                if (challenge.participants.size > 1) {
                    Text(text = "${challenge.participants.size} Teilnehmer", style = MaterialTheme.typography.bodySmall)
                }else{
                    Text(text = "Solo Challenge", style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            navController.navigate("challengeDetail/${challenge.id}")
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_up_right),
                            contentDescription = "Öffnen",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Öffnen")
                    }
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Required for ExposedDropdownMenuBox
@Composable
fun AddChallengeDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, type: String, target: Float) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("distance") }
    var target by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // State for dropdown menu
    val options = listOf("distance", "duration", "runcount", "days")

    var nameError by remember { mutableStateOf(false) }
    var targetError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Neue Challenge hinzufügen") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isBlank()
                    },
                    isError = nameError,
                    label = { Text("Gruppen Name") },
                    supportingText = {
                        if (nameError) Text("Name darf nicht leer sein", color = MaterialTheme.colorScheme.error)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = getChallengeTypeText(type),
                        onValueChange = {  },
                        label = { Text("Challenge Typ") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor() // Important for positioning
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(getChallengeTypeText(selectionOption)) },
                                onClick = {
                                    type = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = target,
                    onValueChange = {
                        target = it.filter { c -> c.isDigit() || c == '.' }
                        targetError = target.isBlank() || target.toFloatOrNull() == null || target.toFloat() == 0f
                    },
                    label = { Text("Challenge Ziel") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = targetError,
                    supportingText = {
                        if (targetError) Text("Bitte gib ein gültiges Ziel > 0 an", color = MaterialTheme.colorScheme.error)
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                nameError = name.isBlank()
                targetError = target.isBlank() || target.toFloatOrNull() == null || target.toFloat() == 0f

                if (!nameError && !targetError) {
                    val targetFloat = target.toFloatOrNull() ?: 0f
                    onAdd(name, type, targetFloat)
                    onDismiss()
                }
            }) {
                Text("Hinzufügen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}


@Composable
fun ChallengeCard(
    imageRes: Int,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.background(Color(0xFF21211F))) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayMedium.copy(color = Color.White)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}
