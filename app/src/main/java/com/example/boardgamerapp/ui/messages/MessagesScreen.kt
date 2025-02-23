package com.example.boardgamerapp.ui.messages

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.boardgamerapp.data.model.Message
import com.example.boardgamerapp.data.model.User
import com.example.boardgamerapp.ui.events.getOrCreateUserId
import com.example.boardgamerapp.ui.main.PageTitle
import com.example.boardgamerapp.viewmodel.GameViewModel


@Composable
fun MessagesScreen(
    gameViewModel: GameViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(LocalContext.current.applicationContext as Application)
    )
) {
    var showMessageDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userId = getOrCreateUserId(context, gameViewModel)

    val messages by gameViewModel.getMessagesForUser(userId).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        PageTitle("Posteingang")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showMessageDialog = true }) {
            Text("Neue Nachricht")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(messages) { message ->
                val senderName by produceState(initialValue = "Lädt...") {
                    value = gameViewModel.getUserById(message.senderId)?.userName ?: "Unbekannt"
                }
                val receiverName by produceState(initialValue = "Lädt...") {
                    value = gameViewModel.getUserById(message.receiverId)?.userName ?: "Unbekannt"
                }

                MessageCard(message, userId, senderName, receiverName)
            }
        }

        if (showMessageDialog) {
            NewMessageDialog(
                onDismiss = { showMessageDialog = false },
                onSend = { receiver, text ->
                    gameViewModel.sendMessage(userId, receiver.userId, text)
                    showMessageDialog = false
                },
                gameViewModel = gameViewModel
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMessageDialog(
    onDismiss: () -> Unit,
    onSend: (User, String) -> Unit,
    gameViewModel: GameViewModel
) {
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var messageText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val users = gameViewModel.getAllUsers.collectAsState(initial = emptyList()).value

    println(users)

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Neue Nachricht senden") },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    selectedUser?.let {
                        OutlinedTextField(
                            value = it.userName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown öffnen"
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        users!!.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(user.userName) },
                                onClick = {
                                    selectedUser = user
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Nachricht") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedUser?.let { onSend(it, messageText) }
                },
                enabled = selectedUser != null && messageText.isNotBlank()
            ) {
                Text("Senden")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Abbrechen")
            }
        }
    )
}

@Composable
fun MessageCard(message: Message, currentUserId: String, senderName: String, receiverName: String) {
    val isSentByUser = message.senderId == currentUserId
    val alignment = if (isSentByUser) Arrangement.End else Arrangement.Start
    val backgroundColor = if (isSentByUser) Color(0xFFBB86FC) else Color(0xFFEDEDED)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = alignment
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = if (isSentByUser) "Gesendet an: $receiverName" else "Von: $senderName",
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = message.messageText)
        }
    }
}
