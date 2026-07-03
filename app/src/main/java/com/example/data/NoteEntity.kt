package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val colorHex: String = "#E8DDFF",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val checklistJson: String = "[]"
)
