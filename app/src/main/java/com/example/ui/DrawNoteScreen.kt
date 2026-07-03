package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NoteEntity

data class DrawLine(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

fun serializeDrawing(lines: List<DrawLine>): String {
    return lines.joinToString(separator = "\n") { line ->
        val colorHex = String.format("#%06X", (0xFFFFFF and line.color.toArgb()))
        val pointsStr = line.points.joinToString(separator = "|") { "${it.x},${it.y}" }
        "$colorHex;${line.strokeWidth};${line.isEraser};$pointsStr"
    }
}

fun deserializeDrawing(content: String): List<DrawLine> {
    if (content.isBlank() || !content.contains(";")) return emptyList()
    val lines = mutableListOf<DrawLine>()
    content.split("\n").forEach { lineStr ->
        try {
            val parts = lineStr.split(";")
            if (parts.size >= 3) {
                val colorHex = parts[0]
                val strokeWidth = parts[1].toFloatOrNull() ?: 5f
                // Support older serializations where isEraser was not present
                val isEraser = parts[2].toBooleanStrictOrNull() ?: false
                val pointsStr = if (parts.size > 3) parts[3] else parts[2]
                
                val points = pointsStr.split("|").mapNotNull { pStr ->
                    val coords = pStr.split(",")
                    if (coords.size == 2) {
                        val x = coords[0].toFloatOrNull()
                        val y = coords[1].toFloatOrNull()
                        if (x != null && y != null) {
                            Offset(x, y)
                        } else null
                    } else null
                }
                if (points.isNotEmpty()) {
                    lines.add(
                        DrawLine(
                            points = points,
                            color = Color(android.graphics.Color.parseColor(colorHex)),
                            strokeWidth = strokeWidth,
                            isEraser = isEraser
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return lines
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawNoteScreen(
    viewModel: NoteViewModel,
    noteId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Select note on startup
    remember(noteId) {
        if (noteId == 0L) {
            viewModel.selectNewDrawingNote()
        } else {
            viewModel.selectNoteForEditing(noteId)
        }
        true
    }

    val note by viewModel.currentEditingNote.collectAsState()
    
    if (note == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading canvas...", color = MaterialTheme.colorScheme.onBackground)
        }
        return
    }

    val currentNote = note!!
    
    var titleText by remember(currentNote.id) { mutableStateOf(currentNote.title) }
    var lines by remember(currentNote.id) { 
        mutableStateOf(deserializeDrawing(currentNote.content)) 
    }
    
    var currentLine by remember { mutableStateOf<DrawLine?>(null) }
    
    val colors = listOf(
        Color(0xFF1B1B1F), // Dark
        Color(0xFFBA1A1A), // Red
        Color(0xFF005FAF), // Blue
        Color(0xFF1F8A43), // Green
        Color(0xFF8B6B00), // Gold/Yellow
        Color(0xFF7F00FF), // Violet
        Color(0xFFD65D0E), // Orange
        Color(0xFFD01B79)  // Pink
    )
    
    var selectedColor by remember { mutableStateOf(colors.first()) }
    var selectedWidth by remember { mutableStateOf(10f) }
    var isEraserMode by remember { mutableStateOf(false) }

    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val canvasBgColor = if (isLight) Color(0xFFFDFBF7) else Color(0xFF1F2024)
    val canvasBorderColor = if (isLight) Color(0xFFE1E2E9) else Color(0xFF2C2F36)

    Scaffold(
        modifier = modifier.fillMaxSize().navigationBarsPadding(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("draw_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    TextField(
                        value = titleText,
                        onValueChange = {
                            titleText = it
                            viewModel.updateCurrentNoteTitle(it)
                        },
                        placeholder = {
                            Text(
                                "Sketch Note",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("draw_title_input")
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentNote {
                                onBack()
                            }
                        },
                        modifier = Modifier.testTag("draw_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Drawing",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (currentNote.id != 0L) {
                        IconButton(
                            onClick = {
                                viewModel.deleteCurrentNote {
                                    onBack()
                                }
                            },
                            modifier = Modifier.testTag("draw_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Note",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Drawing Canvas Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(canvasBgColor)
                    .border(1.dp, canvasBorderColor, RoundedCornerShape(24.dp))
                    .pointerInput(isEraserMode, selectedColor, selectedWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentLine = DrawLine(
                                    points = listOf(offset),
                                    color = if (isEraserMode) canvasBgColor else selectedColor,
                                    strokeWidth = selectedWidth,
                                    isEraser = isEraserMode
                                )
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentLine?.let { line ->
                                    val updatedPoints = line.points + change.position
                                    currentLine = line.copy(points = updatedPoints)
                                }
                            },
                            onDragEnd = {
                                currentLine?.let { line ->
                                    if (line.points.isNotEmpty()) {
                                        lines = lines + line
                                        viewModel.updateCurrentNoteContent(serializeDrawing(lines))
                                    }
                                }
                                currentLine = null
                            }
                        )
                    }
                    .testTag("drawing_canvas")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw lines
                    lines.forEach { line ->
                        val drawColor = if (line.isEraser) canvasBgColor else line.color
                        if (line.points.size > 1) {
                            val path = Path().apply {
                                moveTo(line.points.first().x, line.points.first().y)
                                for (i in 1 until line.points.size) {
                                    lineTo(line.points[i].x, line.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = drawColor,
                                style = Stroke(
                                    width = line.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        } else if (line.points.size == 1) {
                            drawCircle(
                                color = drawColor,
                                radius = line.strokeWidth / 2f,
                                center = line.points.first()
                            )
                        }
                    }

                    // Draw current line
                    currentLine?.let { line ->
                        val drawColor = if (line.isEraser) canvasBgColor else line.color
                        if (line.points.size > 1) {
                            val path = Path().apply {
                                moveTo(line.points.first().x, line.points.first().y)
                                for (i in 1 until line.points.size) {
                                    lineTo(line.points[i].x, line.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = drawColor,
                                style = Stroke(
                                    width = line.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        } else if (line.points.size == 1) {
                            drawCircle(
                                color = drawColor,
                                radius = line.strokeWidth / 2f,
                                center = line.points.first()
                            )
                        }
                    }
                }
                
                // Overlay Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEraserMode) "Eraser Active" else "Sketch Pad",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Controls Rack
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Stroke width slider and quick clear buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Brush,
                                contentDescription = "Brush Size",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Slider(
                                value = selectedWidth,
                                onValueChange = { selectedWidth = it },
                                valueRange = 2f..50f,
                                modifier = Modifier.weight(1f).testTag("stroke_slider")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${selectedWidth.toInt()}px",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(36.dp)
                            )
                        }

                        // Action buttons: Undo & Clear
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    if (lines.isNotEmpty()) {
                                        lines = lines.dropLast(1)
                                        viewModel.updateCurrentNoteContent(serializeDrawing(lines))
                                    }
                                },
                                enabled = lines.isNotEmpty(),
                                modifier = Modifier.size(36.dp).testTag("undo_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Undo,
                                    contentDescription = "Undo",
                                    tint = if (lines.isNotEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    lines = emptyList()
                                    viewModel.updateCurrentNoteContent("")
                                },
                                enabled = lines.isNotEmpty(),
                                modifier = Modifier.size(36.dp).testTag("clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Canvas",
                                    tint = if (lines.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))

                    // Color palette selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            colors.forEach { color ->
                                val isSelected = selectedColor == color && !isEraserMode
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            selectedColor = color
                                            isEraserMode = false
                                        }
                                        .testTag("color_${color.toArgb()}")
                                )
                            }
                        }

                        // Tool Selector (Pen vs Eraser)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pen Select Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 11.dp, bottomStart = 11.dp))
                                    .background(if (!isEraserMode) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .clickable { isEraserMode = false }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("pen_select"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Brush,
                                        contentDescription = "Pen",
                                        tint = if (!isEraserMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Pen",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isEraserMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))
                            )

                            // Eraser Select Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topEnd = 11.dp, bottomEnd = 11.dp))
                                    .background(if (isEraserMode) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .clickable { isEraserMode = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .testTag("eraser_select"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Eraser",
                                        tint = if (isEraserMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Eraser",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isEraserMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
