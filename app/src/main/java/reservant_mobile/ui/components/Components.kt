package reservant_mobile.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.reservant_mobile.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.Roles
import reservant_mobile.data.services.UserService
import reservant_mobile.data.utils.BottomNavItem
import reservant_mobile.ui.viewmodels.RestaurantViewModel
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    modifier: Modifier = Modifier,
    expanded: MutableState<Boolean>,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    label: String,
    isError: Boolean = false,
    errorText: String = ""
){

    val onDismiss = { expanded.value = false }
    var beginValidation by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
            beginValidation = true
        }
    ) {
        Column {
            OutlinedTextField(
                modifier = modifier
                    .padding(vertical = 8.dp)
                    .menuAnchor(),
                label = { Text(text = label) },
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                shape = RoundedCornerShape(8.dp),
                isError = isError && beginValidation
            )

            if (isError && beginValidation) Text(text = errorText, color = MaterialTheme.colorScheme.error)
        }

        ExposedDropdownMenu(
            modifier = Modifier.exposedDropdownSize(matchTextFieldWidth = false),
            expanded = expanded.value,
            onDismissRequest = onDismiss
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(text = it) },
                    onClick = {
                        onValueChange(it)
                        onDismiss()
                    }
                )
            }
        }
    }
}


@Composable
fun ButtonComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    label: String = "",
    isLoading: Boolean = false
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(gradientBrush, RoundedCornerShape(16.dp)),  // Gradient tła
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,  // Transparentny, by pokazać gradient
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text = label)
        }
    }
}


@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "Logo",
        modifier = modifier.size(120.dp)
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TagsDetailView(tags: List<String>) {
    DetailItem(
        label = stringResource(R.string.label_restaurant_tags),
        value = tags.joinToString(separator = ", ")
    )
}

@Composable
fun IconWithHeader(
    icon: ImageVector,
    text: String,
    showBackButton: Boolean = false,
    onReturnClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            if (showBackButton){
                ReturnButton(
                    onReturnClick = onReturnClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Row (
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .align(Alignment.Center)
            ){
                Icon(
                    imageVector = icon,
                    contentDescription = icon.name,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
        }

        HorizontalDivider(thickness = 2.dp)
    }
}

@Composable
fun LogoWithReturn(
    onReturnClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        ReturnButton(
            onReturnClick = onReturnClick,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Logo(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ReturnButton(
    onReturnClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onReturnClick,
        contentPadding = PaddingValues(2.dp),
        colors = ButtonColors(
            Color.Transparent, Color.Black,
            Color.Transparent, Color.Black
        ),
        shape = RectangleShape,
        modifier = modifier
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "back",
            modifier = Modifier.size(35.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ShowErrorToast(context: Context, id: Int) {
    if (id != -1) {
        val msg = stringResource(id)
        println("[TOAST] '$msg'")
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun BottomNavigation(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>
) {

    val items = listOfNotNull(
        BottomNavItem.Home,
        BottomNavItem.Landing,
        BottomNavItem.Management.takeIf { Roles.RESTAURANT_OWNER in UserService.UserObject.roles },
        BottomNavItem.Profile
    )

    var selectedItem by remember { mutableStateOf(items.first()) }
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    var outlineColor by remember { mutableStateOf(outlineVariant) }

    if (isSystemInDarkTheme()){
        outlineColor = MaterialTheme.colorScheme.outline
    }


    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.drawBehind {
                drawLine(
                    color = outlineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.5f
                )
                }
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.route.toString()) },
                        label = { Text(stringResource(id = item.label)) },
                        selected = selectedItem == item,
                        alwaysShowLabel = true,
                        onClick = {
                            if (selectedItem != item) {
                                navController.navigate(item.route)
                                selectedItem = item
                            }
                        }
                    )    

                }
            }
        }
    )
}


fun Modifier.shimmer(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition(label = "Loading animation")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = "Loading animation"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0F),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        ),
        shape = ShapeDefaults.Medium
    )
        .onGloballyPositioned {
            size = it.size
        }
}


@Composable
fun SecondaryButton(
    modifier: Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String,
    contentPadding: PaddingValues = PaddingValues(6.dp),
) {

    val secondaryButtonColors = ButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )

    Button(
        onClick = onClick,
        shape = CircleShape,
        contentPadding = contentPadding,
        colors = secondaryButtonColors,
        modifier = modifier
    ) {
        Icon(
            imageVector,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            contentDescription = contentDescription
        )
    }
}

@Composable
fun DeletePopup(
    icon: ImageVector,
    title: String,
    text: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit,
    enabled: Boolean = true,
    deleteButtonContent: @Composable (RowScope.() -> Unit) = {
        Text(
            confirmText,
            color = MaterialTheme.colorScheme.error
        )
    }
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            OutlinedButton(
                onClick = onConfirm,
                enabled = enabled,
                content = deleteButtonContent
            )
        },
        dismissButton = {
            FilledTonalButton(
                onClick = onDismissRequest
            ) {
                Text(dismissText)
            }
        }
    )
}

@Composable
fun DeleteCountdownPopup(
    countDownTimer: Int = 5,
    icon: ImageVector,
    title: String,
    text: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit,
    isSaving: Boolean = false
) {

    var allowConfirm by remember {
        mutableStateOf(false)
    }

    var timer by remember {
        mutableIntStateOf(countDownTimer)
    }

    if (timer > 0) {
        LaunchedEffect(key1 = timer) {
            delay(1000)
            timer -= 1
            allowConfirm = timer == 0
        }
    }

    DeletePopup(
        icon = icon,
        title = title,
        text = text,
        dismissText = dismissText,
        onDismissRequest = onDismissRequest,
        onConfirm = {
            if (allowConfirm) onConfirm()
        },
        enabled = allowConfirm
    ) {
        if (!allowConfirm) {
            Text(timer.toString())
        } else if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(25.dp)
            )
        } else {
            Text(confirmText, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun MyFloatingActionButton(
    onClick: () -> Unit,
    allPadding: Dp = 16.dp,
    topPadding: Dp = 16.dp,
    bottomPadding: Dp = 16.dp,
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .padding(allPadding)
            .padding(
                top = topPadding,
                bottom = bottomPadding,
                start = startPadding,
                end = endPadding
            ),
        content = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Dodaj"
            )
        }
    )
}

@Composable
fun ProgressBar(currentStep: Int) {
    val progress = when (currentStep) {
        1 -> 0.33f
        2 -> 0.66f
        3 -> 1f
        else -> 0f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)

    ) {
        Row {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
    }
}

@Composable
fun RatingBar(rating: Float) {
    val fullStars = floor(rating).toInt()
    val halfStars = if (rating - fullStars >= 0.5) 1 else 0
    val emptyStars = 5 - fullStars - halfStars

    Row {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Filled Star"
            )
        }
        repeat(halfStars) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = "Half Star"
            )
        }
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Filled.StarBorder,
                contentDescription = "Empty Star"
            )
        }
    }
}

@Composable
fun TagSelectionScreen(
    vm: RestaurantViewModel,
    onDismiss: () -> Unit,
    onTagSelected: (String, Boolean) -> Unit,
) {
    val selectedTags = vm.selectedTags
    val tags = vm.tags

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wybierz tagi") },
        text = {
            LazyColumn {
                items(tags) { tag ->
                    val isChecked = selectedTags.contains(tag)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isSelected ->
                                onTagSelected(tag, isSelected)
                            }
                        )
                        Text(
                            text = tag,
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .clickable { onTagSelected(tag, !isChecked) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        }
    )
}

@Composable
fun TagList(tags: List<String>, onRemoveTag: (String) -> Unit) {
    LazyRow(
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        items(tags){ tag ->
            TagItem(tag = tag, onRemove = { onRemoveTag(tag) })
        }
    }
}

@Composable
fun TagItem(
    tag: String,
    onRemove: () -> Unit = {},
    removable: Boolean = true
) {
    InputChip(
        onClick = { onRemove() },
        label = { Text(tag) },
        trailingIcon = {
            if(removable){
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove tag"
                )
            }
        },
        shape = RoundedCornerShape(50),
        modifier = Modifier.padding(4.dp),
        selected = false
    )
}

@Composable
fun FullscreenGallery(
    onDismiss: () -> Unit,
    bitmaps: List<Bitmap>
) {

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 64.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                if(bitmaps.isNotEmpty()){
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(bitmaps.size) { index ->
                            Card(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(1f),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(8.dp)
                            ) {
                                Image(
                                    bitmap = bitmaps[index].asImageBitmap(),
                                    contentDescription = "Image $index",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Gray),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                else{
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    icon: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .background(color, CircleShape)
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Text(
            text = icon,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// TODO: verify colors and add vars
@Composable
fun SearchBarWithFilter(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (String?) -> Unit,
    currentFilter: String?,
    filterOptions: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val labelAll = stringResource(id = R.string.label_all)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    singleLine = true,
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.label_search),
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 4.dp)
                        )
                    }
                )
            }
            Box {
                IconButton(
                    onClick = { expanded = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(id = R.string.label_filters),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = labelAll) },
                        onClick = {
                            onFilterSelected("")
                            expanded = false
                        }
                    )
                    filterOptions.forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(text = filter) },
                            onClick = {
                                onFilterSelected(if (filter == labelAll) "" else filter)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        currentFilter?.let {
            if (it.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.label_current_filter, it),
                    style = TextStyle(color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingTabSwitch(
    pages: List<Pair<String, @Composable () -> Unit>>,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    paneScroll: Boolean = true
) {

    @Composable
    fun CustomIndicator(tabPositions: List<TabPosition>, pagerState: PagerState) {
        val transition = updateTransition(pagerState.currentPage, label = "")
        val indicatorStart by transition.animateDp(
            transitionSpec = {
                if (initialState < targetState) {
                    spring(dampingRatio = 1f, stiffness = 400f)
                } else {
                    spring(dampingRatio = 1f, stiffness = 1000f)
                }
            }, label = ""
        ) {
            tabPositions[it].left
        }

        val indicatorEnd by transition.animateDp(
            transitionSpec = {
                if (initialState < targetState) {
                    spring(dampingRatio = 1f, stiffness = 1000f)
                } else {
                    spring(dampingRatio = 1f, stiffness = 400f)
                }
            }, label = ""
        ) {
            tabPositions[it].right
        }

        Box(
            Modifier
                .offset(x = indicatorStart)
                .wrapContentSize(align = Alignment.BottomStart)
                .width(indicatorEnd - indicatorStart)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                .zIndex(5f)
        )
    }

    val pagerState = rememberPagerState(
        pageCount = { pages.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val cornerShape = RoundedCornerShape(50)


    val indicator = @Composable { tabPositions: List<TabPosition> ->
        CustomIndicator(tabPositions, pagerState)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = paneScroll
        ) { page ->
            pages[page].second.invoke()
        }
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = color,
            modifier = Modifier
                .padding(20.dp)
                .clip(cornerShape),
            indicator = indicator,
            divider = {}
        ) {
            pages.forEachIndexed { index, tabItem ->
                val selected = pagerState.currentPage == index
                Tab(
                    modifier = Modifier.zIndex(6f),
                    text = {
                        if (selected) {
                            Text(text = tabItem.first, color = MaterialTheme.colorScheme.background)
                        } else {
                            Text(text = tabItem.first)
                        }
                    },
                    selected = selected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun ImageCard(
    image: Painter
){
    Card(
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Image(
            painter = image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ImageCard(
    image: ImageBitmap
){
    Card(
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MissingPage(
    errorStringId: Int,
    modifier:Modifier = Modifier
        .fillMaxSize()
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(
            modifier = Modifier
                .height(100.dp)
                .width(100.dp),
            imageVector = Icons.Rounded.Error,
            contentDescription = "Missing page error",
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = if (errorStringId != -1) stringResource(id = errorStringId) else ""
        )
    }
}

@Composable
fun UnderlinedItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(32.dp)
            )
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
    HorizontalDivider()
}

@Composable
fun FilterOption(status: String, onFilterSelected: (String) -> Unit) {
    TextButton(onClick = { onFilterSelected(status) }) {
        Text(text = status)
    }
}

@Composable
fun LoadedPhotoComponent(
    photoModifier: Modifier = Modifier,
    placeholderModifier: Modifier = Modifier,
    getPhoto: suspend () -> Bitmap?,
){
    var isLoading by remember {
        mutableStateOf(true)
    }

    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(key1 = Unit) {
        bitmap = getPhoto()
        isLoading = false
    }

    when {
        isLoading -> {
            CircularProgressIndicator(
                modifier = placeholderModifier
            )
        }

        !isLoading -> {
            if (bitmap != null){
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "loaded_photo",
                    modifier = photoModifier
                )
            } else {
                Image(
                    painterResource(id = R.drawable.unknown_image),
                    contentDescription = "placeholder_photo",
                    modifier = placeholderModifier
                )
            }
        }

    }

}