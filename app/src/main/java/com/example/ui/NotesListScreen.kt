package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.NoteEntity
import com.example.model.ChecklistItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    viewModel: NoteViewModel,
    onNoteClick: (Long, String) -> Unit,
    onProfileClick: () -> Unit,
    initialTab: String = "notes",
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f

    // Navbar selection state
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }

    // Dynamically aggregate default tags and any custom tags from notes
    val categories = remember(notes) {
        val defaultTags = listOf("All", "Work", "Study", "Personal", "Archive")
        val customTags = notes.map { it.category }.filter { it.isNotBlank() && it !in defaultTags }.distinct()
        defaultTags + customTags
    }

    val formattedDate = remember {
        val sdf = java.text.SimpleDateFormat("EEEE, MMMM dd", java.util.Locale.getDefault())
        sdf.format(java.util.Date())
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (isWideScreen) {
                LuminaNavigationRail(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onProfileClick = { selectedTab = "profile" },
                    isLight = isLight
                )
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                topBar = {
                    if (selectedTab == "notes" || selectedTab == "search") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        text = if (selectedTab == "search") "Search titles, content, or tags..." else "Search your notes...",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                            .clickable { selectedTab = "profile" }
                                            .testTag("profile_avatar"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Assignment,
                                            contentDescription = "User profile",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(24.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("search_input")
                            )
                        }
                    } else if (selectedTab == "daily") {
                        // Beautiful Daily Planner header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Daily Planner",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = formattedDate,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    .clickable { selectedTab = "profile" },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = "User profile",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    if (!isWideScreen) {
                        LuminaNavigationBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            onProfileClick = { selectedTab = "profile" },
                            isLight = isLight
                        )
                    }
                },
                floatingActionButton = {
                    if (selectedTab == "notes" || selectedTab == "daily") {
                        var isExpanded by remember { mutableStateOf(false) }
                        
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Sub-actions visible when expanded
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn(animationSpec = tween(200)) + slideInVertically(animationSpec = tween(200)) { 40 },
                                exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(animationSpec = tween(200)) { 40 }
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Option 1: Sketch / Draw Note
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card(
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(
                                                text = "Draw",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                        FloatingActionButton(
                                            onClick = {
                                                isExpanded = false
                                                onNoteClick(0, "Drawing")
                                            },
                                            modifier = Modifier.size(44.dp).testTag("add_draw_note_button"),
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            shape = CircleShape
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Brush,
                                                contentDescription = "Draw Note",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    // Option 2: Text Note
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Card(
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(
                                                text = "Text",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                        FloatingActionButton(
                                            onClick = {
                                                isExpanded = false
                                                onNoteClick(0, "Text")
                                            },
                                            modifier = Modifier.size(44.dp).testTag("add_text_note_button"),
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                            shape = CircleShape
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Assignment,
                                                contentDescription = "Text Note",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Main Toggle FAB with hover/focus animation!
                            val interactionSource = remember { MutableInteractionSource() }
                            val isHovered by interactionSource.collectIsHoveredAsState()
                            val isFocused by interactionSource.collectIsFocusedAsState()
                            
                            // Smooth scale on hover/focus
                            val fabScale by animateFloatAsState(
                                targetValue = if (isHovered || isFocused) 1.12f else 1.0f,
                                animationSpec = tween(300),
                                label = "fabScale"
                            )
                            
                            // Rotate + icon slightly when expanded
                            val rotationAngle by animateFloatAsState(
                                targetValue = if (isExpanded) 135f else 0f,
                                animationSpec = tween(300),
                                label = "rotationAngle"
                            )

                            FloatingActionButton(
                                onClick = { isExpanded = !isExpanded },
                                shape = RoundedCornerShape(16.dp),
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                interactionSource = interactionSource,
                                modifier = Modifier
                                    .padding(bottom = 12.dp)
                                    .graphicsLayer(
                                        scaleX = fabScale,
                                        scaleY = fabScale
                                    )
                                    .testTag("add_note_fab")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Options",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .graphicsLayer(rotationZ = rotationAngle)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { fullWidth -> fullWidth / 2 })
                            .togetherWith(fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { fullWidth -> -fullWidth / 2 })
                    },
                    label = "tab_transition",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) { targetTab ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        if (targetTab == "notes") {
                // 2. Category Filter Chips (Horizontal list row)
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory.equals(category, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else if (MaterialTheme.colorScheme.background.red > 0.5f) Color(0xFFDEE2EB)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.setSelectedCategory(category) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 3. Staggered/Masonry Note Listing Grid
                if (notes.isEmpty()) {
                    // Empty state graphic tips
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = "No Notes",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            modifier = Modifier.size(96.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No notes found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Create your very first note to get started!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp
                    ) {
                        items(notes, key = { it.id }) { note ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(
                                    initialOffsetY = { 50 }
                                )
                            ) {
                                NoteGridItem(note = note, onClick = { onNoteClick(note.id, note.category) })
                            }
                        }
                    }
                }
            } else if (targetTab == "search") {
                // Interactive Search Workspace View
                Text(
                    text = "SEARCH BY TAG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp)
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory.equals(category, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else if (MaterialTheme.colorScheme.background.red > 0.5f) Color(0xFFDEE2EB)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.setSelectedCategory(category) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (notes.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Notes",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "Search Notes, Checklists & Tags" else "No matching notes found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "Type query or select categories to browse items." else "Try modifying search or category filters.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp
                    ) {
                        items(notes, key = { it.id }) { note ->
                            NoteGridItem(note = note, onClick = { onNoteClick(note.id, note.category) })
                        }
                    }
                }
            } else if (targetTab == "daily") {
                // Beautiful Calendar strip at top
                val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                val currentDayIndex = remember {
                    val calendar = java.util.Calendar.getInstance()
                    val day = calendar.get(java.util.Calendar.DAY_OF_WEEK)
                    if (day == java.util.Calendar.SUNDAY) 6 else day - 2
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    daysOfWeek.forEachIndexed { index, day ->
                        val isToday = index == currentDayIndex
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.primary
                                    else Color.Transparent
                                )
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = day,
                                fontSize = 11.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = (calendarOffsetDay(index - currentDayIndex)).toString(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isToday) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Compile all checklists from active notes
                val dailyTasks = remember(notes) {
                    val list = mutableListOf<Pair<NoteEntity, ChecklistItem>>()
                    val moshi = Moshi.Builder().build()
                    val listType = Types.newParameterizedType(List::class.java, ChecklistItem::class.java)
                    val listAdapter = moshi.adapter<List<ChecklistItem>>(listType)

                    notes.forEach { note ->
                        try {
                            val items = listAdapter.fromJson(note.checklistJson) ?: emptyList()
                            items.forEach { item ->
                                list.add(note to item)
                            }
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                    list
                }

                Text(
                    text = "TODAY'S DAILY TASKS (${dailyTasks.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
                )

                if (dailyTasks.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "No Tasks",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "All Caught Up!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add checklist items inside any note to view them here.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dailyTasks) { (note, item) ->
                            val itemBgColor = if (MaterialTheme.colorScheme.background.red > 0.5f) Color(0xFFF1F3F9) else Color(0xFF1E1D24)
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = itemBgColor),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.toggleChecklistItemInNote(note, item.id)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Checkbox",
                                        tint = if (item.isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.text,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (item.isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (note.title.isNotBlank()) note.title else "Untitled Note",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            if (note.category.isNotBlank()) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = note.category,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
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
            } else if (targetTab == "profile") {
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                if (isLoggedIn) {
                    ProfileScreen(
                        viewModel = viewModel,
                        onBack = {},
                        onSignOut = {
                            selectedTab = "notes"
                        },
                        onLogIn = {},
                        showBackButton = false,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LoginScreen(
                        viewModel = viewModel,
                        onAuthSuccess = {
                            // Automatically switches to ProfileScreen on state update
                        },
                        onBack = {
                            selectedTab = "notes"
                        },
                        showBackButton = false,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
}
}
}

private fun calendarOffsetDay(offset: Int): Int {
    val cal = java.util.Calendar.getInstance()
    cal.add(java.util.Calendar.DAY_OF_YEAR, offset)
    return cal.get(java.util.Calendar.DAY_OF_MONTH)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteGridItem(
    note: NoteEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamically fetch equivalent pastel shades with custom transparency
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val baseCardColor = try {
        Color(android.graphics.Color.parseColor(note.colorHex))
    } catch (e: Exception) {
        if (isLight) Color(0xFFD3E2FF) else Color(0xFF102840)
    }

    // High contrast overlay based on light/dark modes
    val containerColor = if (isLight) {
        baseCardColor.copy(alpha = 1f)
    } else {
        // Safe dark equivalent overlay tinting
        when (note.colorHex.uppercase()) {
            "#EADDFF" -> Color(0xFF251840)
            "#F3E2D0" -> Color(0xFF3B2E1C)
            "#D3E2FF" -> Color(0xFF102840)
            "#DEE2EB" -> Color(0xFF202530)
            "#E2F1E3" -> Color(0xFF182A1B)
            else -> Color(0xFF1D1B20)
        }
    }

    val contentColor = if (isLight) {
        when (note.colorHex.uppercase()) {
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

    // Decorative Icon matching note category
    val categoryIcon: ImageVector = when (note.category.uppercase()) {
        "WORK" -> Icons.Default.Palette
        "STUDY" -> Icons.Default.ShoppingBag
        "PERSONAL" -> Icons.Default.FitnessCenter
        "DRAWING" -> Icons.Default.Brush
        else -> Icons.Default.EventNote
    }

    // Parse checklist snippet preview
    val moshi = Moshi.Builder().build()
    val listType = Types.newParameterizedType(List::class.java, ChecklistItem::class.java)
    val listAdapter = moshi.adapter<List<ChecklistItem>>(listType)
    val checklistItems = try {
        listAdapter.fromJson(note.checklistJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Minimal shadow
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("note_item_${note.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header elements
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = note.category,
                    tint = if (isLight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(20.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = if (isLight) Color.DarkGray else Color.LightGray,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = "1m ago", // Elegant shortened timestamp preview
                        fontSize = 10.sp,
                        color = subTextColor.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Note title
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Note body snippet text or drawing preview
            if (note.category.equals("Drawing", ignoreCase = true)) {
                val drawingLines = remember(note.content) { deserializeDrawing(note.content) }
                if (drawingLines.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isLight) Color(0xFFFAF9F6) else Color(0xFF1E1E24))
                            .border(1.dp, if (isLight) Color(0xFFECEAE4) else Color(0xFF33353C), RoundedCornerShape(16.dp))
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            var minX = Float.MAX_VALUE
                            var maxX = Float.MIN_VALUE
                            var minY = Float.MAX_VALUE
                            var maxY = Float.MIN_VALUE
                            var hasPoints = false
                            drawingLines.forEach { line ->
                                line.points.forEach { pt ->
                                    if (pt.x < minX) minX = pt.x
                                    if (pt.x > maxX) maxX = pt.x
                                    if (pt.y < minY) minY = pt.y
                                    if (pt.y > maxY) maxY = pt.y
                                    hasPoints = true
                                }
                            }
                            
                            if (hasPoints) {
                                val padding = 8f
                                val drawW = size.width - padding * 2
                                val drawH = size.height - padding * 2
                                val boundsW = maxX - minX
                                val boundsH = maxY - minY
                                
                                val scale = if (boundsW > 0 && boundsH > 0) {
                                    minOf(drawW / boundsW, drawH / boundsH)
                                } else 1f
                                
                                val safeScale = if (scale.isInfinite() || scale.isNaN()) 1f else scale
                                val centerX = minX + boundsW / 2
                                val centerY = minY + boundsH / 2
                                val canvasCenterX = size.width / 2
                                val canvasCenterY = size.height / 2
                                
                                drawingLines.forEach { line ->
                                    val drawColor = if (line.isEraser) {
                                        if (isLight) Color(0xFFFAF9F6) else Color(0xFF1E1E24)
                                    } else line.color
                                    
                                    val mappedPoints = line.points.map { pt ->
                                        Offset(
                                            canvasCenterX + (pt.x - centerX) * safeScale,
                                            canvasCenterY + (pt.y - centerY) * safeScale
                                        )
                                    }
                                    
                                    if (mappedPoints.size > 1) {
                                        val path = Path().apply {
                                            moveTo(mappedPoints.first().x, mappedPoints.first().y)
                                            for (i in 1 until mappedPoints.size) {
                                                lineTo(mappedPoints[i].x, mappedPoints[i].y)
                                            }
                                        }
                                        drawPath(
                                            path = path,
                                            color = drawColor,
                                            style = Stroke(
                                                width = maxOf(1f, line.strokeWidth * safeScale * 0.4f),
                                                cap = StrokeCap.Round,
                                                join = StrokeJoin.Round
                                            )
                                        )
                                    } else if (mappedPoints.size == 1) {
                                        drawCircle(
                                            color = drawColor,
                                            radius = maxOf(1f, (line.strokeWidth * safeScale * 0.4f) / 2f),
                                            center = mappedPoints.first()
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Empty Drawing",
                        fontSize = 13.sp,
                        color = subTextColor.copy(alpha = 0.5f)
                    )
                }
            } else if (note.content.isNotBlank()) {
                Text(
                    text = parseMarkdownToAnnotatedString(note.content, isLight),
                    fontSize = 13.sp,
                    color = subTextColor,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            // Render snippet list preview of checklists
            if (checklistItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    checklistItems.take(3).forEach { item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "checkbox",
                                tint = if (item.isChecked) MaterialTheme.colorScheme.primary else subTextColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.text,
                                fontSize = 12.sp,
                                color = if (item.isChecked) subTextColor.copy(alpha = 0.6f) else subTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                            )
                        }
                    }
                    if (checklistItems.size > 3) {
                        Text(
                            text = "+ ${checklistItems.size - 3} more items",
                            fontSize = 10.sp,
                            color = subTextColor.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 18.dp)
                        )
                    }
                }
            }

            // category badge tag
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.category.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .background(contentColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = note.category,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor.copy(alpha = 0.8f)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Decorative status indicator dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (note.isPinned) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun LuminaNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    isLight: Boolean,
    isVertical: Boolean = false,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Smooth hover/focus scale & translate transitions
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else if (isHovered || isFocused) 1.03f else 1.0f,
        animationSpec = tween(300),
        label = "scale"
    )
    val translationX by animateDpAsState(
        targetValue = if (isVertical && (isHovered || isFocused)) 4.dp else 0.dp,
        animationSpec = tween(300),
        label = "translationX"
    )
    val translationY by animateDpAsState(
        targetValue = if (!isVertical && (isHovered || isFocused)) (-4).dp else 0.dp,
        animationSpec = tween(300),
        label = "translationY"
    )

    val iconTint = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else if (isHovered || isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    }

    val iconToDraw = if (selected || isHovered || isFocused) {
        icon
    } else {
        when (icon) {
            Icons.Default.GridView -> Icons.Outlined.GridView
            Icons.Default.Search -> Icons.Outlined.Search
            Icons.Default.CalendarToday -> Icons.Outlined.CalendarToday
            Icons.Default.Person -> Icons.Outlined.Person
            else -> icon
        }
    }

    val fillBgColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else if (isHovered || isFocused) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(300),
        label = "fillBgColor"
    )

    val density = androidx.compose.ui.platform.LocalDensity.current
    val translationXPx = with(density) { translationX.toPx() }
    val translationYPx = with(density) { translationY.toPx() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
                this.translationX = translationXPx
                this.translationY = translationYPx
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                onClick = onClick
            )
            .hoverable(interactionSource = interactionSource)
            .focusable(interactionSource = interactionSource)
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(fillBgColor, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconToDraw,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun LuminaNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(82.dp)
            .background(if (isLight) Color(0xFFF3F4F9) else Color(0xFF1B1B1F))
            .border(
                width = 1.dp,
                color = if (isLight) Color(0xFFE1E2E9) else Color(0xFF2C2F36),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LuminaNavItem(
            selected = selectedTab == "notes",
            onClick = { onTabSelected("notes") },
            icon = Icons.Default.GridView,
            label = "Notes",
            isLight = isLight,
            modifier = Modifier.weight(1f).testTag("bottom_nav_notes")
        )
        LuminaNavItem(
            selected = selectedTab == "search",
            onClick = { onTabSelected("search") },
            icon = Icons.Default.Search,
            label = "Search",
            isLight = isLight,
            modifier = Modifier.weight(1f).testTag("bottom_nav_search")
        )
        LuminaNavItem(
            selected = selectedTab == "daily",
            onClick = { onTabSelected("daily") },
            icon = Icons.Default.CalendarToday,
            label = "Daily",
            isLight = isLight,
            modifier = Modifier.weight(1f).testTag("bottom_nav_daily")
        )
        LuminaNavItem(
            selected = selectedTab == "profile",
            onClick = onProfileClick,
            icon = Icons.Default.Person,
            label = "Profile",
            isLight = isLight,
            modifier = Modifier.weight(1f).testTag("bottom_nav_profile")
        )
    }
}

@Composable
fun LuminaNavigationRail(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    isLight: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(96.dp)
            .background(if (isLight) Color(0xFFF3F4F9) else Color(0xFF1B1B1F))
            .statusBarsPadding()
            .navigationBarsPadding()
            .border(
                width = 1.dp,
                color = if (isLight) Color(0xFFE1E2E9) else Color(0xFF2C2F36),
                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            )
            .padding(vertical = 24.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Reusable brand logo in Navigation Rail
            LuminaLogo(size = 40.dp, modifier = Modifier.padding(bottom = 16.dp))

            LuminaNavItem(
                selected = selectedTab == "notes",
                onClick = { onTabSelected("notes") },
                icon = Icons.Default.GridView,
                label = "Notes",
                isLight = isLight,
                isVertical = true,
                modifier = Modifier.fillMaxWidth().testTag("bottom_nav_notes")
            )
            LuminaNavItem(
                selected = selectedTab == "search",
                onClick = { onTabSelected("search") },
                icon = Icons.Default.Search,
                label = "Search",
                isLight = isLight,
                isVertical = true,
                modifier = Modifier.fillMaxWidth().testTag("bottom_nav_search")
            )
            LuminaNavItem(
                selected = selectedTab == "daily",
                onClick = { onTabSelected("daily") },
                icon = Icons.Default.CalendarToday,
                label = "Daily",
                isLight = isLight,
                isVertical = true,
                modifier = Modifier.fillMaxWidth().testTag("bottom_nav_daily")
            )
        }

        LuminaNavItem(
            selected = selectedTab == "profile",
            onClick = onProfileClick,
            icon = Icons.Default.Person,
            label = "Profile",
            isLight = isLight,
            isVertical = true,
            modifier = Modifier.fillMaxWidth().testTag("bottom_nav_profile")
        )
    }
}

fun parseMarkdownToAnnotatedString(text: String, isLight: Boolean): AnnotatedString {
    return buildAnnotatedString {
        val length = text.length
        var i = 0
        val bulletColor = if (isLight) Color(0xFF6750A4) else Color(0xFFD0BCFF)
        
        while (i < length) {
            // Bold & Italic: ***text***
            if (text.startsWith("***", i)) {
                val end = text.indexOf("***", i + 3)
                if (end != -1) {
                    val content = text.substring(i + 3, end)
                    val startOffset = this.length
                    append(content)
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                        startOffset,
                        this.length
                    )
                    i = end + 3
                    continue
                }
            }
            // Bold: **text**
            if (text.startsWith("**", i)) {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    val content = text.substring(i + 2, end)
                    val startOffset = this.length
                    append(content)
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        startOffset,
                        this.length
                    )
                    i = end + 2
                    continue
                }
            }
            // Italic: *text*
            if (text.startsWith("*", i)) {
                val end = text.indexOf("*", i + 1)
                if (end != -1) {
                    val content = text.substring(i + 1, end)
                    val startOffset = this.length
                    append(content)
                    addStyle(
                        SpanStyle(fontStyle = FontStyle.Italic),
                        startOffset,
                        this.length
                    )
                    i = end + 1
                    continue
                }
            }
            // Underscore Bold & Italic: ___text___
            if (text.startsWith("___", i)) {
                val end = text.indexOf("___", i + 3)
                if (end != -1) {
                    val content = text.substring(i + 3, end)
                    val startOffset = this.length
                    append(content)
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                        startOffset,
                        this.length
                    )
                    i = end + 3
                    continue
                }
            }
            // Underscore Bold: __text__
            if (text.startsWith("__", i)) {
                val end = text.indexOf("__", i + 2)
                if (end != -1) {
                    val content = text.substring(i + 2, end)
                    val startOffset = this.length
                    append(content)
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        startOffset,
                        this.length
                    )
                    i = end + 2
                    continue
                }
            }
            // Underscore Italic: _text_
            if (text.startsWith("_", i)) {
                val end = text.indexOf("_", i + 1)
                if (end != -1) {
                    val content = text.substring(i + 1, end)
                    val startOffset = this.length
                    append(content)
                    addStyle(
                        SpanStyle(fontStyle = FontStyle.Italic),
                        startOffset,
                        this.length
                    )
                    i = end + 1
                    continue
                }
            }
            // Bullets / lists
            if (text.startsWith("- ", i)) {
                val startOffset = this.length
                append("• ")
                addStyle(
                    SpanStyle(color = bulletColor, fontWeight = FontWeight.Bold),
                    startOffset,
                    this.length
                )
                i += 2
                continue
            }
            append(text[i])
            i++
        }
    }
}
