package reservant_mobile.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import reservant_mobile.data.constants.PermissionStrings
import reservant_mobile.data.utils.BottomNavItem
import reservant_mobile.ui.activities.FilterOptionWithStars
import reservant_mobile.ui.viewmodels.RestaurantViewModel
import kotlin.math.floor
import kotlin.math.max
import kotlin.time.Duration

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
    errorText: String = "",
    formSent: Boolean = false
) {

    val onDismiss = { expanded.value = false }
    var beginValidation by remember { mutableStateOf(false) }

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
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .menuAnchor(),
                label = { Text(text = label) },
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                shape = RoundedCornerShape(8.dp),
                isError = isError && (beginValidation || formSent)
            )

            if (isError && (beginValidation || formSent)) {
                Text(text = errorText, color = MaterialTheme.colorScheme.error)
            }
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
    isLoading: Boolean = false,
    icon: ImageVector? = null // Opcjonalny parametr dla ikony
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
            // Wyświetlanie ikony, jeśli jest dostępna
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 8.dp) // Dodaj padding między ikoną a tekstem
                )
            }
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
    onReturnClick: () -> Unit = {},
    actions: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            if (showBackButton) {
                ReturnButton(
                    onReturnClick = onReturnClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .align(Alignment.Center)
            ) {
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
            if (actions != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    actions()
                }
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
) {
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

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    if (id != -1) {
        val msg = stringResource(id)
        LaunchedEffect(key1 = id) {
            println("[TOAST] '$msg'")
            showToast(context, msg)
        }
    }
}


@Composable
fun BottomNavigation(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    items: List<BottomNavItem>
) {
    var selectedItem by remember { mutableStateOf(items.first()) }
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    var outlineColor by remember { mutableStateOf(outlineVariant) }

    if (isSystemInDarkTheme()) {
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
    icon: ImageVector = Icons.Default.Add
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
                imageVector = icon,
                contentDescription = "Dodaj"
            )
        }
    )
}

@Composable
fun ProgressBar(currentStep: Int, maxStep: Int) {
    val progress = currentStep.toFloat() / maxStep

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
        items(tags) { tag ->
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
            if (removable) {
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

                if (bitmaps.isNotEmpty()) {
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
                } else {
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
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: ((String?) -> Unit)? = null, // Optional string filter
    onFilterSelectedInt: ((Int?) -> Unit)? = null, // Optional int filter
    currentFilter: String? = null, // Optional string filter
    currentFilterInt: Int? = null, // Optional int filter
    filterOptions: List<String>? = null, // Optional list for string filters
    filterOptionsInt: List<Int>? = null, // Optional list for int filters
    additionalButtonOnClick: (() -> Unit)? = null,
    additionalButtonIcon: ImageVector? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val labelAll = stringResource(id = R.string.label_all)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pole wyszukiwania
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
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
                        contentDescription = null
                    )
                }
            )

            // Ikona filtra
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = stringResource(id = R.string.label_filters),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            // Dodatkowy przycisk, jeśli parametry zostały podane
            if (additionalButtonOnClick != null && additionalButtonIcon != null) {
                Button(
                    onClick = additionalButtonOnClick,
                    modifier = Modifier
                        .height(56.dp),
                    colors = ButtonColors(
                        containerColor = FloatingActionButtonDefaults.containerColor,
                        contentColor = contentColorFor(FloatingActionButtonDefaults.containerColor),
                        disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor,
                        disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor
                    )
                ) {
//                    Text(text = additionalButtonLabel,
//                        fontSize = 20.sp)
                    Icon(
                        imageVector = additionalButtonIcon,
                        contentDescription = stringResource(id = R.string.label_add_review)
                    )
                }
            }
        }

        // DropdownMenu dla opcji filtra
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = labelAll) },
                onClick = {
                    onFilterSelected?.invoke(null)
                    onFilterSelectedInt?.invoke(null)
                    expanded = false
                }
            )

            // Opcje filtrowania dla stringów
            filterOptions?.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(text = filter) },
                    onClick = {
                        onFilterSelected?.invoke(filter)
                        expanded = false
                    }
                )
            }

            // Opcje filtrowania dla int
            filterOptionsInt?.forEach { filterInt ->
                DropdownMenuItem(
                    text = { FilterOptionWithStars(stars = filterInt) },
                    onClick = {
                        onFilterSelectedInt?.invoke(filterInt)
                        expanded = false
                    }
                )
            }
        }

        // Wyświetlanie aktualnego filtra (opcjonalnie)
        currentFilter?.let {
            if (it.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.label_current_filter, it),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        currentFilterInt?.let {
            if (it > 0) {
                Text(
                    text = stringResource(
                        id = R.string.label_current_filter,
                        "$it ${stringResource(id = R.string.label_stars)}"
                    ),
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
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
) {
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
) {
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
    modifier: Modifier = Modifier.fillMaxSize(),
    errorStringId: Int? = null,
    errorString: String = "",
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .height(100.dp)
                .width(100.dp),
            imageVector = Icons.Rounded.Error,
            contentDescription = "Missing page error",
            tint = MaterialTheme.colorScheme.secondary
        )

        var stringValue = errorString
        if (errorStringId != null) {
            stringValue = stringResource(id = errorStringId)
        }

        Text(
            modifier = Modifier.padding(16.dp),
            text = stringValue
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
    contentScale: ContentScale = ContentScale.Fit,
    placeholder: Int = R.drawable.unknown_image,
    getPhoto: suspend () -> Bitmap?,
) {
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
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "loaded_photo",
                    modifier = photoModifier,
                    contentScale = contentScale
                )
            } else {
                Image(
                    painterResource(placeholder),
                    contentDescription = "placeholder_photo",
                    modifier = placeholderModifier,
                    contentScale = contentScale
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageSheet(
    content: @Composable () -> Unit,
    onDismiss: () -> Unit = {},
    height: Dp = 600.dp,
    width: Dp = Dp.Unspecified,
    buttonLabelId: Int? = null,
    buttonLabel: String = "",
    buttonOnClick: () -> Unit = {}
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var buttonLabelValue by remember { mutableStateOf(buttonLabel) }
    val coroutineScope = rememberCoroutineScope()
    val hideModalBottomSheet: () -> Unit = {
        coroutineScope.launch {
            modalBottomSheetState.hide()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        modifier = Modifier.height(height),
        sheetMaxWidth = width
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
            ) {
                content()
            }
            if (buttonLabelId != null) {
                buttonLabelValue = stringResource(id = buttonLabelId)
            }

            if (buttonLabelValue.isNotEmpty()) {
                ButtonComponent(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    onClick = {
                        buttonOnClick()
                        hideModalBottomSheet()
                    },
                    label = buttonLabelValue
                )
            }
        }
    }
}

@Composable
fun LoadingScreenWithTimeout(
    timeoutMillis: Duration,
    afterTimeoutMessage: String = stringResource(id = R.string.error_not_found),
    modifier: Modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
) {
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        delay(timeoutMillis)
        loading = false
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator()
        } else {
            MissingPage(errorString = afterTimeoutMessage)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermission(
    permission: PermissionStrings,
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    val permissionState = rememberPermissionState(permission.string)

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(permissionState) {
        if (permission.string.isNotEmpty() && !permissionState.status.isGranted) {
            requestPermissionLauncher.launch(permission.string)
        }
    }
}

@Composable
fun SwitchWithLabel(
    label: String,
    checked: Boolean,
    onCheckedChange:  ((Boolean) -> Unit)?
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}