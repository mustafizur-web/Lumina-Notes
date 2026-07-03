package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.NoteEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditNoteScreen(
    viewModel: NoteViewModel,
    noteId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Select note on startup
    remember(noteId) {
        viewModel.selectNoteForEditing(noteId)
        true
    }

    val note by viewModel.currentEditingNote.collectAsState()
    val checklistItems by viewModel.checklistItems.collectAsState()

    var showColorPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var newChecklistText by remember { mutableStateOf("") }

    if (note == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading note...", color = MaterialTheme.colorScheme.onBackground)
        }
        return
    }

    val currentNote = note!!

    // Manage content with TextFieldValue to support selection and cursor formatting!
    var contentTextFieldValue by remember(currentNote.id) {
        mutableStateOf(
            TextFieldValue(
                text = currentNote.content,
                selection = TextRange(currentNote.content.length)
            )
        )
    }

    val onFormattingClick = { type: String ->
        val newValue = applyFormattingHelper(contentTextFieldValue, type)
        contentTextFieldValue = newValue
        viewModel.updateCurrentNoteContent(newValue.text)
    }

    // Dynamic background canvas color live-updates with animated transitions!
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val baseColor = try {
        Color(android.graphics.Color.parseColor(currentNote.colorHex))
    } catch (e: Exception) {
        if (isLight) Color(0xFFD3E2FF) else Color(0xFF102840)
    }

    val animatedBgColor by animateColorAsState(
        targetValue = if (isLight) baseColor else {
            when (currentNote.colorHex.uppercase()) {
                "#EADDFF" -> Color(0xFF251840)
                "#F3E2D0" -> Color(0xFF3B2E1C)
                "#D3E2FF" -> Color(0xFF102840)
                "#DEE2EB" -> Color(0xFF202530)
                "#E2F1E3" -> Color(0xFF182A1B)
                else -> Color(0xFF111318)
            }
        },
        label = "bgColorAnim"
    )

    val contentColor = if (isLight) {
        when (currentNote.colorHex.uppercase()) {
            "#EADDFF" -> Color(0xFF21005D)
            "#F3E2D0" -> Color(0xFF3E2D16)
            "#D3E2FF" -> Color(0xFF001D35)
            "#DEE2EB" -> Color(0xFF1B1B1F)
            "#E2F1E3" -> Color(0xFF0F2D13)
            else -> Color(0xFF1B1B1F)
        }
    } else {
        Color(0xFFE2E2E9)
    }

    val subTextColor = contentColor.copy(alpha = 0.8f)

    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        val isCurrentBgLight = (animatedBgColor.red * 0.299f + animatedBgColor.green * 0.587f + animatedBgColor.blue * 0.114f) > 0.5f
        androidx.compose.runtime.SideEffect {
            val window = (view.context as android.app.Activity).window
            androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isCurrentBgLight
        }
    }

    val paletteOptions = listOf("#EADDFF", "#F3E2D0", "#D3E2FF", "#DEE2EB", "#E2F1E3")
    val categoryOptions = listOf("Work", "Study", "Personal", "Archive")

    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = animatedBgColor,
        topBar = {
            // Header: Arrow Back, Title, Save Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(Color.Transparent)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentNote(onBack)
                        },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edit Note",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Avatar Note Icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(animatedBgColor)
        ) {
            // Note Editor Canvas Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Metadata and Tags Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Selection Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { showCategoryPicker = !showCategoryPicker }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("category_badge")
                    ) {
                        Text(
                            text = if (currentNote.category.isBlank()) "Add Tag" else currentNote.category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // Formatted creation date
                    val dateStr = remember(currentNote.timestamp) {
                        val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                        sdf.format(Date(currentNote.timestamp))
                    }
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subTextColor
                    )
                }

                // Category selection dropdown mock list with custom option tag creator
                if (showCategoryPicker) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Choose a category or create a custom tag:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categoryOptions.forEach { category ->
                                val isSelected = currentNote.category.equals(category, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable {
                                            viewModel.updateCurrentNoteCategory(category)
                                            showCategoryPicker = false
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = category,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Tag input field for option tag creator
                        var customTagText by remember { mutableStateOf("") }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customTagText,
                                onValueChange = { customTagText = it },
                                placeholder = { Text("Custom tag...", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                textStyle = TextStyle(fontSize = 12.sp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (customTagText.isNotBlank()) {
                                        viewModel.updateCurrentNoteCategory(customTagText.trim())
                                        customTagText = ""
                                        showCategoryPicker = false
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(36.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("+ Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Note Title Field
                TextField(
                    value = currentNote.title,
                    onValueChange = { viewModel.updateCurrentNoteTitle(it) },
                    placeholder = {
                        Text(
                            "Ideas for the next app",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor.copy(alpha = 0.4f)
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Note Content/Body Rich Text Field
                TextField(
                    value = contentTextFieldValue,
                    onValueChange = {
                        contentTextFieldValue = it
                        viewModel.updateCurrentNoteContent(it.text)
                    },
                    placeholder = {
                        Text(
                            "Write something more...",
                            fontSize = 16.sp,
                            color = contentColor.copy(alpha = 0.4f)
                        )
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = contentColor
                    ),
                    visualTransformation = MarkdownVisualTransformation(isLight),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_content_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Checklist System Interface
                Text(
                    text = "CHECKLIST ITEMS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = subTextColor.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                )

                // Render Checklist Items
                checklistItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleChecklistItem(item.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "checkbox",
                                    tint = if (item.isChecked) MaterialTheme.colorScheme.primary else contentColor.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item.text,
                                fontSize = 15.sp,
                                color = if (item.isChecked) contentColor.copy(alpha = 0.5f) else contentColor,
                                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                            )
                        }

                        IconButton(
                            onClick = { viewModel.removeChecklistItem(item.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RemoveCircleOutline,
                                contentDescription = "Remove Item",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Add checklist item input row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newChecklistText,
                        onValueChange = { newChecklistText = it },
                        placeholder = { Text("Add checklist item...", color = subTextColor.copy(alpha = 0.6f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = contentColor,
                            unfocusedTextColor = contentColor,
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            unfocusedBorderColor = contentColor.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("checklist_item_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newChecklistText.isNotBlank()) {
                                viewModel.addChecklistItem(newChecklistText)
                                newChecklistText = ""
                            }
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .size(40.dp)
                            .testTag("add_checklist_item_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Item",
                            tint = Color.White
                        )
                    }
                }

                // Spacing to avoid overlap with bottom navigation bar
                Spacer(modifier = Modifier.height(180.dp))
            }

            // Floating Custom Color Picker Palette Options list
            if (showColorPicker) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 138.dp, start = 20.dp, end = 20.dp)
                        .background(
                            if (isLight) Color.White.copy(alpha = 0.95f) else Color(0xFF211F26).copy(alpha = 0.95f),
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isLight) Color.LightGray.copy(alpha = 0.5f) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        paletteOptions.forEach { hex ->
                            val circleColor = if (isLight) {
                                Color(android.graphics.Color.parseColor(hex))
                            } else {
                                when (hex.uppercase()) {
                                    "#EADDFF" -> Color(0xFF251840)
                                    "#F3E2D0" -> Color(0xFF3B2E1C)
                                    "#D3E2FF" -> Color(0xFF102840)
                                    "#DEE2EB" -> Color(0xFF202530)
                                    "#E2F1E3" -> Color(0xFF182A1B)
                                    else -> Color(0xFF1E1E24)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(circleColor, CircleShape)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (currentNote.colorHex.equals(hex, ignoreCase = true)) 3.dp else 1.dp,
                                        color = if (currentNote.colorHex.equals(hex, ignoreCase = true)) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            if (isLight) Color.LightGray else Color.White.copy(alpha = 0.3f)
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        viewModel.updateCurrentNoteColor(hex)
                                        showColorPicker = false
                                    }
                            )
                        }
                    }
                }
            }

            // 5. Bottom Action Toolbar Glass Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 62.dp, start = 20.dp, end = 20.dp)
                    .background(
                        if (isLight) Color.White.copy(alpha = 0.95f) else Color(0xFF211F26).copy(alpha = 0.9f),
                        RoundedCornerShape(32.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isLight) Color.LightGray.copy(alpha = 0.5f) else Color.Transparent,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 16.dp)
            ) {
                val toolbarIconTint = if (isLight) Color(0xFF4A4A4A) else Color.LightGray
                val toolbarLabelColor = if (isLight) Color(0xFF6A6A6A) else Color.Gray

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Color palette button
                    IconButton(
                        onClick = { showColorPicker = !showColorPicker },
                        modifier = Modifier.testTag("palette_button")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Palette, contentDescription = "Color", tint = toolbarIconTint)
                            Text("COLOR", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = toolbarLabelColor)
                        }
                    }

                    // Pin button
                    IconButton(
                        onClick = { viewModel.toggleCurrentNotePin() },
                        modifier = Modifier.testTag("pin_button")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pin",
                                tint = if (currentNote.isPinned) MaterialTheme.colorScheme.primary else toolbarIconTint
                            )
                            Text("PIN", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = toolbarLabelColor)
                        }
                    }

                    // Archive button
                    IconButton(
                        onClick = { viewModel.toggleCurrentNoteArchive() },
                        modifier = Modifier.testTag("archive_button")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = "Archive",
                                tint = if (currentNote.isArchived) MaterialTheme.colorScheme.primary else toolbarIconTint
                            )
                            Text("ARCHIVE", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = toolbarLabelColor)
                        }
                    }

                    // Alarm (Reminder) Mock Trigger
                    IconButton(onClick = {}) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Alarm, contentDescription = "Alarm", tint = toolbarIconTint)
                            Text("ALARM", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = toolbarLabelColor)
                        }
                    }

                    // Share Mock Trigger
                    IconButton(onClick = {}) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = toolbarIconTint)
                            Text("SHARE", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = toolbarLabelColor)
                        }
                    }

                    // Delete button
                    IconButton(
                        onClick = {
                            viewModel.deleteCurrentNote(onBack)
                        },
                        modifier = Modifier.testTag("delete_button")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            Text("DELETE", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            // 6. Floating Formatting Bar (Always accessible layout at the absolute bottom)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .background(
                        if (isLight) Color.White.copy(alpha = 0.95f) else Color(0xFF2C2C2C).copy(alpha = 0.95f),
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isLight) Color.LightGray.copy(alpha = 0.5f) else Color.Transparent,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(vertical = 4.dp, horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onFormattingClick("bold") }) {
                        Icon(
                            imageVector = Icons.Default.FormatBold,
                            contentDescription = "Bold",
                            tint = if (isLight) Color(0xFF212121) else Color.LightGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = { onFormattingClick("italic") }) {
                        Icon(
                            imageVector = Icons.Default.FormatItalic,
                            contentDescription = "Italic",
                            tint = if (isLight) Color(0xFF212121) else Color.LightGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = { onFormattingClick("bullet") }) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = "Bullet List",
                            tint = if (isLight) Color(0xFF212121) else Color.LightGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(onClick = { onFormattingClick("numbered") }) {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = "Numbered List",
                            tint = if (isLight) Color(0xFF212121) else Color.LightGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun applyFormattingHelper(value: TextFieldValue, type: String): TextFieldValue {
    val text = value.text
    val selection = value.selection
    val start = selection.start
    val end = selection.end
    val min = minOf(start, end).coerceIn(0, text.length)
    val max = maxOf(start, end).coerceIn(0, text.length)

    return when (type) {
        "bold" -> {
            if (min != max) {
                val selectedText = text.substring(min, max)
                val isBold = selectedText.startsWith("**") && selectedText.endsWith("**") && selectedText.length >= 4
                if (isBold) {
                    val unwrapped = selectedText.substring(2, selectedText.length - 2)
                    val newText = text.substring(0, min) + unwrapped + text.substring(max)
                    TextFieldValue(
                        text = newText,
                        selection = TextRange(min, min + unwrapped.length)
                    )
                } else {
                    val newText = text.substring(0, min) + "**$selectedText**" + text.substring(max)
                    TextFieldValue(
                        text = newText,
                        selection = TextRange(min + 2, max + 2)
                    )
                }
            } else {
                val newText = text.substring(0, min) + "****" + text.substring(min)
                TextFieldValue(
                    text = newText,
                    selection = TextRange(min + 2)
                )
            }
        }
        "italic" -> {
            if (min != max) {
                val selectedText = text.substring(min, max)
                val isItalic = selectedText.startsWith("*") && selectedText.endsWith("*") && !selectedText.startsWith("**") && selectedText.length >= 2
                if (isItalic) {
                    val unwrapped = selectedText.substring(1, selectedText.length - 1)
                    val newText = text.substring(0, min) + unwrapped + text.substring(max)
                    TextFieldValue(
                        text = newText,
                        selection = TextRange(min, min + unwrapped.length)
                    )
                } else {
                    val newText = text.substring(0, min) + "*$selectedText*" + text.substring(max)
                    TextFieldValue(
                        text = newText,
                        selection = TextRange(min + 1, max + 1)
                    )
                }
            } else {
                val newText = text.substring(0, min) + "**" + text.substring(min)
                TextFieldValue(
                    text = newText,
                    selection = TextRange(min + 1)
                )
            }
        }
        "bullet" -> {
            var lineStart = min
            while (lineStart > 0 && text[lineStart - 1] != '\n') {
                lineStart--
            }
            val hasBullet = text.substring(lineStart).startsWith("- ")
            if (hasBullet) {
                val newText = text.substring(0, lineStart) + text.substring(lineStart + 2)
                TextFieldValue(
                    text = newText,
                    selection = TextRange(
                        (min - 2).coerceAtLeast(lineStart),
                        (max - 2).coerceAtLeast(lineStart)
                    )
                )
            } else {
                val newText = text.substring(0, lineStart) + "- " + text.substring(lineStart)
                TextFieldValue(
                    text = newText,
                    selection = TextRange(min + 2, max + 2)
                )
            }
        }
        "numbered" -> {
            var lineStart = min
            while (lineStart > 0 && text[lineStart - 1] != '\n') {
                lineStart--
            }
            val hasNumbered = text.substring(lineStart).startsWith("1. ")
            if (hasNumbered) {
                val newText = text.substring(0, lineStart) + text.substring(lineStart + 3)
                TextFieldValue(
                    text = newText,
                    selection = TextRange(
                        (min - 3).coerceAtLeast(lineStart),
                        (max - 3).coerceAtLeast(lineStart)
                    )
                )
            } else {
                val newText = text.substring(0, lineStart) + "1. " + text.substring(lineStart)
                TextFieldValue(
                    text = newText,
                    selection = TextRange(min + 3, max + 3)
                )
            }
        }
        else -> value
    }
}

class MarkdownVisualTransformation(private val isLight: Boolean) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val originalLength = originalText.length
        val builder = AnnotatedString.Builder()
        
        val originalToTransformed = IntArray(originalLength + 1)
        val transformedToOriginal = ArrayList<Int>()
        
        val bulletColor = if (isLight) Color(0xFF6750A4) else Color(0xFFD0BCFF)
        
        var i = 0
        while (i < originalLength) {
            var tokenLength = 0
            var style: SpanStyle? = null
            var matchEnd = -1
            
            if (originalText.startsWith("***", i)) {
                val end = originalText.indexOf("***", i + 3)
                if (end != -1) {
                    tokenLength = 3
                    style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                    matchEnd = end
                }
            } else if (originalText.startsWith("**", i)) {
                val end = originalText.indexOf("**", i + 2)
                if (end != -1) {
                    tokenLength = 2
                    style = SpanStyle(fontWeight = FontWeight.Bold)
                    matchEnd = end
                }
            } else if (originalText.startsWith("*", i)) {
                val end = originalText.indexOf("*", i + 1)
                if (end != -1) {
                    tokenLength = 1
                    style = SpanStyle(fontStyle = FontStyle.Italic)
                    matchEnd = end
                }
            } else if (originalText.startsWith("___", i)) {
                val end = originalText.indexOf("___", i + 3)
                if (end != -1) {
                    tokenLength = 3
                    style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                    matchEnd = end
                }
            } else if (originalText.startsWith("__", i)) {
                val end = originalText.indexOf("__", i + 2)
                if (end != -1) {
                    tokenLength = 2
                    style = SpanStyle(fontWeight = FontWeight.Bold)
                    matchEnd = end
                }
            } else if (originalText.startsWith("_", i)) {
                val end = originalText.indexOf("_", i + 1)
                if (end != -1) {
                    tokenLength = 1
                    style = SpanStyle(fontStyle = FontStyle.Italic)
                    matchEnd = end
                }
            }
            
            if (tokenLength > 0 && matchEnd != -1) {
                val currentTransformedStart = builder.length
                for (offset in 0 until tokenLength) {
                    originalToTransformed[i + offset] = currentTransformedStart
                }
                
                val insideContent = originalText.substring(i + tokenLength, matchEnd)
                val insideStart = builder.length
                for (offset in insideContent.indices) {
                    val origIdx = i + tokenLength + offset
                    originalToTransformed[origIdx] = builder.length
                    transformedToOriginal.add(origIdx)
                    builder.append(insideContent[offset])
                }
                val insideEnd = builder.length
                if (style != null) {
                    builder.addStyle(style, insideStart, insideEnd)
                }
                
                for (offset in 0 until tokenLength) {
                    originalToTransformed[matchEnd + offset] = insideEnd
                }
                
                i = matchEnd + tokenLength
            } else {
                if (originalText.startsWith("- ", i)) {
                    val startTransformed = builder.length
                    originalToTransformed[i] = startTransformed
                    originalToTransformed[i + 1] = startTransformed + 1
                    
                    transformedToOriginal.add(i)
                    builder.append('•')
                    transformedToOriginal.add(i + 1)
                    builder.append(' ')
                    
                    builder.addStyle(
                        SpanStyle(color = bulletColor, fontWeight = FontWeight.Bold),
                        startTransformed,
                        builder.length
                    )
                    i += 2
                } else {
                    originalToTransformed[i] = builder.length
                    transformedToOriginal.add(i)
                    builder.append(originalText[i])
                    i++
                }
            }
        }
        
        val finalTransformedLength = builder.length
        originalToTransformed[originalLength] = finalTransformedLength
        transformedToOriginal.add(originalLength)
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, originalLength)
                return originalToTransformed[clamped].coerceIn(0, finalTransformedLength)
            }
            
            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, finalTransformedLength)
                return transformedToOriginal[clamped].coerceIn(0, originalLength)
            }
        }
        
        return TransformedText(builder.toAnnotatedString(), offsetMapping)
    }
}
