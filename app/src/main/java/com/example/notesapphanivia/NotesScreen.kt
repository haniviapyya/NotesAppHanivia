package com.example.notesapphanivia

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapphanivia.ui.theme.NotesAppHaniviaTheme

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    vm: NotesViewModel = viewModel()
) {
    // Mengamati data catatan secara otomatis dari ViewModel
    val notes by vm.notes.collectAsState()

    // State untuk input catatan baru
    var input by rememberSaveable { mutableStateOf("") }

    // State untuk mode edit inline (menyimpan ID & teks catatan yang sedang diedit)
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingText by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Catatan Saya", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // 1. FORM INPUT (CREATE)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Catatan baru") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                vm.addNote(input)
                input = "" // Kosongkan kolom setelah ditambah
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text("Tambah")
            }
        }

        Text(
            text = "Jumlah catatan: ${notes.size}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 2. DAFTAR CATATAN (READ, UPDATE, DELETE)
        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada catatan", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notes, key = { it.id }) { note ->
                    // Cek apakah baris ini sedang dalam mode edit
                    if (editingId == note.id) {
                        NoteItemEditing(
                            text = editingText,
                            onTextChange = { editingText = it },
                            onSave = {
                                vm.updateNote(note, editingText)
                                editingId = null
                            },
                            onCancel = { editingId = null }
                        )
                    } else {
                        NoteItem(
                            note = note,
                            onEdit = {
                                editingId = note.id
                                editingText = note.title
                            },
                            onDelete = { vm.deleteNote(note) }
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

// Tampilan satu baris catatan (Mode Normal)
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(note.title) },
        supportingContent = { Text("ID: ${note.id}") },
        trailingContent = {
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = Color(0xFF2196F3)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Hapus",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    )
}

// Tampilan satu baris catatan saat sedang diedit (Mode Edit Inline)
@Composable
fun NoteItemEditing(
    text: String,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("Ubah catatan") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSave) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "Simpan",
                tint = Color(0xFF4CAF50)
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Batal"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    NotesAppHaniviaTheme {
        NoteItem(note = Note(id = 1, title = "Belajar Room Database"))
    }
}