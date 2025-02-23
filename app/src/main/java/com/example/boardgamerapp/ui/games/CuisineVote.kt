package com.example.boardgamerapp.ui.games

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuisineSelectionDialog(
    currentCuisine: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val cuisines = listOf("Italienisch", "Griechisch", "Türkisch", "Chinesisch", "Indisch")
    var expanded by remember { mutableStateOf(false) }
    var selectedCuisine by remember { mutableStateOf(currentCuisine ?: "Küche auswählen") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lieblingsessensrichtung") },
        text = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCuisine,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    cuisines.forEach { cuisine ->
                        DropdownMenuItem(
                            text = { Text(cuisine) },
                            onClick = {
                                selectedCuisine = cuisine
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedCuisine != "Küche auswählen") {
                        onSave(selectedCuisine)
                    }
                    onDismiss()
                }
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
