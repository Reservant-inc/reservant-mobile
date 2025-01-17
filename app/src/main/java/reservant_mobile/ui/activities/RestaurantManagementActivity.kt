package reservant_mobile.ui.activities

import WarehouseActivity
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Dining
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.HideImage
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material.icons.outlined.TableBar
import androidx.compose.material.icons.outlined.TableRestaurant
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.reservant_mobile.R
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.RestaurantGroupDTO
import reservant_mobile.ui.components.ButtonComponent
import reservant_mobile.ui.components.ComboBox
import reservant_mobile.ui.components.DeleteCountdownPopup
import reservant_mobile.ui.components.DetailItem
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.ImageCard
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.components.MyFloatingActionButton
import reservant_mobile.ui.components.ReturnButton
import reservant_mobile.ui.components.ShowErrorToast
import reservant_mobile.ui.components.TagsDetailView
import reservant_mobile.ui.navigation.RegisterRestaurantRoutes
import reservant_mobile.ui.navigation.RestaurantManagementRoutes
import reservant_mobile.ui.navigation.RestaurantRoutes
import reservant_mobile.ui.viewmodels.RestaurantManagementViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun RestaurantManagementActivity(navControllerHome: NavHostController) {

    val restaurantManageVM = viewModel<RestaurantManagementViewModel>()
    val navController = rememberNavController()
    val groups = restaurantManageVM.groups
    var selectedGroup: RestaurantGroupDTO? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    var showDeleteRestaurantPopup by remember { mutableStateOf(false) }
    var showDeleteGroupPopup by remember { mutableStateOf(false) }
    var showEditGroupPopup by remember { mutableStateOf(false) }
    var editFormSent by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }

    if(showDeleteRestaurantPopup)  {
        val restaurant = restaurantManageVM.selectedRestaurant
        if(restaurant!= null){
            val confirmText = stringResource(R.string.delete_restaurant_message) +
                    "\n" + restaurant.name + " ?";
            DeleteCountdownPopup(
                icon = Icons.Default.Delete,
                title = stringResource(R.string.delete_restaurant_title),
                text = confirmText,
                confirmText = stringResource(R.string.label_yes_capital),
                dismissText = stringResource(R.string.label_cancel),
                onDismissRequest = { showDeleteRestaurantPopup = false },
                onConfirm = {
                    restaurantManageVM.viewModelScope.launch {
                        if(navController.currentBackStackEntry?.destination?.route == RestaurantManagementRoutes.RestaurantPreview::class.qualifiedName)
                            navController.navigate(RestaurantManagementRoutes.Restaurant)
                        restaurantManageVM.deleteRestaurant(restaurant.restaurantId)
                        showDeleteRestaurantPopup = false
                    }
                }
            )
        }
    }

    if (showDeleteGroupPopup) {
        val group = restaurantManageVM.selectedGroup
        if (group != null) {
            val confirmText = stringResource(R.string.delete_group_message) +
                    "\n" + group.name + " ?"

            val deleteText = stringResource(R.string.label_delete_group) +" "+ group.name
            DeleteCountdownPopup(
                icon = Icons.Default.Delete,
                title = stringResource(R.string.label_delete_group),
                text = confirmText,
                confirmText = stringResource(R.string.label_yes_capital),
                dismissText = stringResource(R.string.label_cancel),
                onDismissRequest = { showDeleteGroupPopup = false },
                onConfirm = {
                    restaurantManageVM.viewModelScope.launch {
                        group.restaurantGroupId?.let {
                            val result = restaurantManageVM.deleteGroup(it)
                            if(result){
                                Toast.makeText(
                                    context,
                                    deleteText,
                                    Toast.LENGTH_SHORT
                                ).show()
                                restaurantManageVM.selectedGroup = null
                                restaurantManageVM.isGroupSelected = false
                            }
                        }
                        showDeleteGroupPopup = false
                    }
                }
            )
        }
    }

    if(showEditGroupPopup){
        AlertDialog(
            onDismissRequest = {
                showEditGroupPopup = false
            },
            title = {
                Text(
                    text = stringResource(R.string.label_new_group_name),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newGroupName,
                        onValueChange = { newGroupName = it },
                        label = { stringResource(R.string.label_group) },
                        modifier = Modifier.fillMaxWidth(),
                        isError = restaurantManageVM.isGroupNameInvalid() && editFormSent
                    )
                }
            },
            confirmButton = {
                val groupChangedText = stringResource(R.string.label_group_name_changed)
                TextButton(
                    onClick = {
                        restaurantManageVM.viewModelScope.launch {
                            editFormSent = true
                            val editSucceed = restaurantManageVM.editGroupName(newGroupName)

                            if(editSucceed){
                                restaurantManageVM.selectedGroup = null
                                selectedGroup = null
                                Toast.makeText(
                                    context,
                                    groupChangedText,
                                    Toast.LENGTH_SHORT
                                ).show()
                                showEditGroupPopup = false
                            }

                        }
                    }
                ) {
                    Text(
                        stringResource(R.string.label_save)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGroupPopup = false }) {
                    Text(
                        stringResource(R.string.label_cancel)
                    )
                }
            },
        )
    }

    NavHost(navController = navController, startDestination = RestaurantManagementRoutes.Restaurant) {
        composable<RestaurantManagementRoutes.Restaurant> {
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {

                    IconWithHeader(
                        icon = Icons.Rounded.RestaurantMenu,
                        text = stringResource(R.string.label_management_manage),
                        actions = {
                            if (restaurantManageVM.isGroupSelected) {
                                IconButton(onClick = {
                                    showDeleteGroupPopup = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.label_delete),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (groups != null) {
                        if (groups.size > 1) {
                            ComboBox(
                                expanded = remember {
                                    mutableStateOf(false)
                                },
                                label = stringResource(R.string.label_group),
                                value = selectedGroup?.name ?: stringResource(R.string.label_management_choose_group),
                                options = groups.map { it.name },
                                onValueChange = { name ->
                                    selectedGroup = groups.find { it.name == name }
                                    restaurantManageVM.viewModelScope.launch {
                                        selectedGroup = selectedGroup?.let { group ->
                                            group.restaurantGroupId?.let { it1 ->
                                                restaurantManageVM.getGroup(it1)
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp, end = 4.dp)
                            )
                        } else if (groups.size == 1) {
                            LaunchedEffect(key1 = Unit) {
                                selectedGroup = groups.first().restaurantGroupId?.let { it1 ->
                                    restaurantManageVM.getGroup(it1)
                                }
                            }
                        } else if (restaurantManageVM.isLoading){
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            MissingPage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(alignment = Alignment.CenterHorizontally)
                                    .padding(vertical = 40.dp),
                                errorStringId = R.string.label_no_restaurants_found
                            )
                        }
                    }

                    if(selectedGroup != null) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            ButtonComponent(
                                onClick = {
                                    navController.navigate(
                                        RestaurantManagementRoutes.Stats(
                                            statsType = StatsType.RESTAURANT_GROUP.nameVal,
                                            queryId = selectedGroup!!.restaurantGroupId ?: -1
                                        )
                                    )
                                },
                                label = stringResource(id = R.string.label_stats_show_group_stats),
                                icon = Icons.Outlined.BarChart,
                                fullWidth = false
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            ButtonComponent(
                                onClick = {
                                    showEditGroupPopup = true
                                },
                                label = stringResource(id = R.string.label_edit_group_name),
                                icon = Icons.Outlined.Edit,
                                fullWidth = false
                            )
                        }
                    }

                    selectedGroup?.restaurants?.forEach { restaurant ->
                        var img by remember { mutableStateOf<Bitmap?>(null) }
                        LaunchedEffect(key1 = true) {
                            if(restaurant.logo!=null){
                                img = restaurantManageVM.getPhoto(restaurant)
                            }
                        }
                        val restaurantNotVerifiedMsg = stringResource(id = R.string.error_restaurant_status_not_verified)
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            onClick = {
                                if(restaurant.isVerified){
                                    restaurantManageVM.selectedRestaurant = restaurant
                                    restaurantManageVM.selectedRestaurantLogo = img
                                    navController.navigate(RestaurantManagementRoutes.RestaurantPreview)
                                }
                                else{
                                    Toast.makeText(context, restaurantNotVerifiedMsg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .heightIn(80.dp, 150.dp)
                        ) {
                            Box() {
                                if(img!=null){
                                    Image(
                                        bitmap = img!!.asImageBitmap(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.2f,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = restaurant.name+ " - "+ restaurant.restaurantType,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = restaurant.address,
                                        fontSize = 14.sp,
                                    )
                                    val cityText = restaurant.city
                                    val postalText = restaurant.postalIndex
                                    Text(
                                        text = "$cityText $postalText",
                                        fontSize = 14.sp,
                                    )
                                    val restaurantVerifiedText = stringResource(id = R.string.label_restaurant_status_verified)
                                    val restaurantNotVerifiedText = stringResource(id = R.string.label_restaurant_status_not_verified)
                                    val restaurantStatus = if(restaurant.isVerified) restaurantVerifiedText else restaurantNotVerifiedText
                                    Text(
                                        text = stringResource(id = R.string.status_label, restaurantStatus),
                                        fontSize = 14.sp,
                                    )
                                }
                                IconButton(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(24.dp),
                                    onClick = {
                                        restaurantManageVM.selectedRestaurant = restaurant
                                        showDeleteRestaurantPopup = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = "Delete",

                                    )
                                }
                            }
                        }
                    }

                }


                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    MyFloatingActionButton(
                        onClick = {
                            navControllerHome.navigate(RegisterRestaurantRoutes.Register)
                        }
                    )
                }
            }
        }
        composable<RestaurantManagementRoutes.RestaurantPreview> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                restaurantManageVM.selectedRestaurant?.let { restaurant ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        val logo:Bitmap? = restaurantManageVM.selectedRestaurantLogo
                        if (logo != null)
                            Image(
                                bitmap = logo.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(175.dp),
                                contentScale = ContentScale.Crop
                            )
                        else
                            Icon(
                                imageVector = Icons.Outlined.HideImage,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(175.dp)
                            )

                        ReturnButton(
                            onReturnClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.TopStart)
                        )
                    }
                    Text(
                        text = "${restaurant.name} - ${restaurant.restaurantType}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 25.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    navController.navigate(
                                        RestaurantManagementRoutes.Edit(restaurantId = restaurant.restaurantId)
                                    )},
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(25.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit restaurant")
                            }
                            Column {
                                DetailItem(
                                    label = stringResource(R.string.label_restaurant_nip),
                                    value = restaurant.nip
                                )
                                DetailItem(
                                    label = stringResource(R.string.label_restaurant_address),
                                    value = "${restaurant.address}, ${restaurant.postalIndex}"
                                )
                                DetailItem(
                                    label = stringResource(R.string.label_restaurant_city),
                                    value = restaurant.city
                                )
                                DetailItem(
                                    label = stringResource(R.string.label_restaurant_delivery),
                                    value =
                                    if (restaurant.provideDelivery)
                                        stringResource(R.string.label_restaurant_delivery_available)
                                    else
                                        stringResource(R.string.label_restaurant_delivery_not_available)
                                )
                                DetailItem(
                                    label = stringResource(R.string.label_restaurant_description),
                                    value = restaurant.description
                                )
                                if (restaurant.tags.isNotEmpty()) {
                                    TagsDetailView(tags = restaurant.tags)
                                }
                                DetailItem(
                                    label = stringResource(R.string.label_restaurant_tables),
                                    value = "${restaurant.tables.size}"
                                )

                                HorizontalDivider(
                                    modifier = Modifier
                                        .padding(top = 8.dp),
                                    color = MaterialTheme.colorScheme.outline,
                                    thickness = 2.dp
                                )

                                Text(
                                    text = stringResource(id = R.string.label_gallery),
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                )
                                LaunchedEffect(key1 = true) {
                                    restaurantManageVM.getGallery(restaurant)
                                }
                                if(restaurantManageVM.isGalleryLoading){
                                    CircularProgressIndicator(modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                    )
                                }
                                else{
                                    val gallery = restaurantManageVM.restaurantGallery
                                    if(gallery.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.label_no_gallery),
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 5.dp)
                                        )
                                    }
                                    else{
                                        Row(
                                            modifier = Modifier
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            gallery.forEach {
                                                ImageCard(
                                                    it.asImageBitmap()
                                                )
                                            }
                                        }
                                    }
                                }


                            }

                        }
                    }

                    val options:List<Option> = listOf(
                        Option(
                            onClick = { navController.navigate(
                                RestaurantManagementRoutes.Employee(restaurantId = restaurant.restaurantId)
                            )},
                            icon = Icons.Outlined.People,
                            titleStringId = R.string.label_employees
                        ),
                        Option(
                            onClick = {  navController.navigate(
                                RestaurantManagementRoutes.Menu(restaurantId = restaurant.restaurantId)
                            )},
                            icon = Icons.Outlined.RestaurantMenu,
                            titleStringId = R.string.label_menu
                        ),
                        Option(
                            onClick = { navController.navigate(
                                RestaurantRoutes.ManageOrders(restaurantId = restaurant.restaurantId)
                            )},
                            icon = Icons.Outlined.Dining,
                            titleStringId = R.string.label_orders
                        ),
                        Option(
                            onClick = { navController.navigate(
                                RestaurantManagementRoutes.Stats(
                                    statsType = StatsType.RESTAURANT.nameVal,
                                    queryId = restaurant.restaurantId
                                )                               
                            )},
                            icon = Icons.Outlined.BarChart,
                            titleStringId = R.string.label_stats
                        ),
                        Option(
                            onClick = { navController.navigate(
                                RestaurantRoutes.Reviews(restaurantId = restaurant.restaurantId)
                            )},
                            icon = Icons.Outlined.Reviews,
                            titleStringId = R.string.label_reviews
                        ),
                        Option(
                            onClick = { navController.navigate(
                                RestaurantRoutes.Warehouse(restaurantId = restaurant.restaurantId)
                            )},
                            icon = Icons.Outlined.Warehouse,
                            titleStringId = R.string.label_warehouse
                        ),
                        Option(
                            onClick = { navController.navigate(
                                RestaurantRoutes.Tables(restaurantId = restaurant.restaurantId)
                            )},
                            icon = Icons.Outlined.TableBar,
                            titleStringId = R.string.label_restaurant_tables
                        ),
                        Option(
                            onClick = {
                                showDeleteRestaurantPopup = true
                            },
                            icon = Icons.Outlined.Delete,
                            titleStringId = R.string.label_delete
                        ),
                    )
                    LazyVerticalGrid(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .heightIn(200.dp, 500.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        columns = GridCells.Adaptive(minSize = 108.dp)
                    ) {
                        items(options) {option ->
                            OptionItem(option = option)
                        }
                    }
                }
            }
        }
        composable<RestaurantManagementRoutes.Menu> {
            MenuManagementActivity(
                onReturnClick = { navController.popBackStack() },
                restaurantId = it.toRoute<RestaurantManagementRoutes.Menu>().restaurantId
            )
        }

        composable<RestaurantManagementRoutes.Employee> {
            EmployeeManagementActivity(
                onReturnClick = { navController.popBackStack() },
                restaurantId = it.toRoute<RestaurantManagementRoutes.Employee>().restaurantId
            )
        }
        composable<RestaurantManagementRoutes.Stats> {
            RestaurantStatsActivity(
                onReturnClick = { navController.popBackStack() },
                statsType = it.toRoute<RestaurantManagementRoutes.Stats>().statsType,
                queryId = it.toRoute<RestaurantManagementRoutes.Stats>().queryId
            )
        }

        composable<RestaurantManagementRoutes.Edit> {
            RegisterRestaurantActivity(
                onReturnClick = { navController.popBackStack() },
                navControllerHome = navControllerHome,
                group = selectedGroup,
                restaurantId = it.toRoute<RestaurantManagementRoutes.Edit>().restaurantId
            )
        }
        composable<RestaurantRoutes.Warehouse> {
            WarehouseActivity(
                onReturnClick = { navController.popBackStack() },
                restaurantId = it.toRoute<RestaurantRoutes.Warehouse>().restaurantId,
                navHostController = navControllerHome,
                isEmployee = true
            )
        }
        composable<RestaurantRoutes.Reviews> {
            ReviewsActivity(
                navController = navController,
                restaurantId = it.toRoute<RestaurantRoutes.Reviews>().restaurantId,
                isOwner = true
            )
        }
        composable<RestaurantRoutes.ManageOrders> {
            OrderManagementScreen(
                onReturnClick = { navController.popBackStack() },
                navHostController = navControllerHome,
                restaurantId = it.toRoute<RestaurantRoutes.ManageOrders>().restaurantId
            )
        }
        composable<RestaurantRoutes.Tables> {
            EmployeeTablesActivity(
                restaurantId = it.toRoute<RestaurantRoutes.ManageOrders>().restaurantId,
                onReturnClick = { navController.popBackStack() }
            )
        }
    }
}
data class Option(
    val onClick: ()->Unit,
    val icon:ImageVector,
    val titleStringId: Int
)

@Composable
fun OptionItem(
    option: Option,
    modifier:Modifier = Modifier
        .padding(horizontal = 10.dp, vertical = 25.dp)
        .size(100.dp)
){
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = option.onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = option.titleStringId),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
