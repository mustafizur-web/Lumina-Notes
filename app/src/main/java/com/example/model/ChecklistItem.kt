package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChecklistItem(
    val id: String,
    val text: String,
    val isChecked: Boolean
)
