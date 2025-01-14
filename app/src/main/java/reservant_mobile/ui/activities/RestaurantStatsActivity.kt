package reservant_mobile.ui.activities

import android.graphics.Typeface
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.rounded.QueryStats
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reservant_mobile.R
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.compose.legend.verticalLegend
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.serialization.Serializable
import reservant_mobile.ui.components.IconWithHeader
import reservant_mobile.ui.components.MessageSheet
import reservant_mobile.ui.components.MissingPage
import reservant_mobile.ui.components.SwitchWithLabel
import reservant_mobile.ui.viewmodels.RestaurantStatsViewmodel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
enum class StatsType(val nameVal: Int){
    RESTAURANT(0),
    RESTAURANT_GROUP(1),
    ALL_RESTAURANTS(2)
}

@Composable
fun RestaurantStatsActivity(onReturnClick: () -> Unit, statsType: Int, queryId: Int? = null){
    val statsVM = viewModel<RestaurantStatsViewmodel>()

    // List of years and months
//    val tmpYears: List<String> = if(statsVM.years.size > 1) statsVM.years.map { it.toString() } else emptyList()
    val tmpYears: List<String> = statsVM.years.map { it.toString() }
//    val tmpYears: List<String> = listOf("2025", "2024", "2023")
    val years = listOf(stringResource(id = R.string.label_all_years)) + tmpYears
    val months = statsVM.months

    val selectedYear = remember { mutableStateOf(years[0]) }
    val selectedMonth = remember { mutableStateOf(months[0]) }
    val showYearDialog = remember { mutableStateOf(false) }
    val showStatsFilter = remember { mutableStateOf(false) }

    val filterCustomers = remember { mutableStateOf(true) }
    val filterRevenue = remember { mutableStateOf(true) }
    val filterReviews = remember { mutableStateOf(true) }
    val filterAvgReviews = remember { mutableStateOf(true) }



    fun getData(){
        statsVM.setDatePeriod(selectedYear.value, selectedMonth.value)
        when(statsType){
            StatsType.RESTAURANT.nameVal -> {
                statsVM.getStatistics(queryId!!)
            }
            StatsType.RESTAURANT_GROUP.nameVal -> {
                statsVM.getStatisticsGroup(queryId!!)
            }
            StatsType.ALL_RESTAURANTS.nameVal -> {
                statsVM.getAllStatistics()
            }
            else -> {
                statsVM.getAllStatistics()
            }
        }
    }

    LaunchedEffect(key1 = null) {
        getData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        IconWithHeader(
            icon = Icons.Rounded.QueryStats,
            text = stringResource(R.string.label_restaurant_stats),
            showBackButton = true,
            onReturnClick = onReturnClick
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        if(tmpYears.isNotEmpty()) showYearDialog.value = true
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = selectedYear.value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                if(tmpYears.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "year arrow",
                    )
                }

            }
            Icon(
                modifier = Modifier
                    .clickable { showStatsFilter.value = true },
                imageVector = Icons.Default.FilterList,
                contentDescription = "stats filter",
            )
        }

        if(selectedYear.value != years[0]){
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                months.forEach { month ->
                    FilterChip(
                        selected = selectedMonth.value == month,
                        onClick = {
                            selectedMonth.value = month
                            getData() },
                        label = {
                            Text(
                                text = month,
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }

        if (statsVM.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                CircularProgressIndicator()
            }

        } else if (statsVM.loadingError) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                MissingPage(errorStringId = R.string.error_loading_statistics)
            }

        } else {

            Spacer(modifier = Modifier.height(24.dp))

            val stats = statsVM.statistics!!

            val customerStat = stats.customerCount!!.sortedBy { LocalDate.parse(it.date).toEpochDay().toFloat() }
            if(filterCustomers.value){
                ChartCard(
                    label = stringResource(id = R.string.label_stats_customers),
                    entryData = customerStat.associate { (dateString, yValue) ->
                        LocalDate.parse(dateString) to yValue!!.toFloat()
                    },
                    columnLabels = listOf(
                        stringResource(id = R.string.label_stats_date),
                        stringResource(id = R.string.label_stats_count)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }



            val revenueStat = stats.revenue!!.sortedBy { LocalDate.parse(it.date).toEpochDay().toFloat() }
            if(filterRevenue.value) {
                ChartCard(
                    label = stringResource(id = R.string.label_stats_revenue),
                    entryData = revenueStat.associate { (dateString, yValue) ->
                        LocalDate.parse(dateString) to yValue!!.toFloat()
                    },
                    columnLabels = listOf(
                        stringResource(id = R.string.label_stats_date),
                        stringResource(id = R.string.label_stats_amount)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }


            val reviewsStat = stats.reviews!!.sortedBy { LocalDate.parse(it.date).toEpochDay().toFloat() }
            val reviewsAvgData = reviewsStat.map { it.average!!.toFloat() }
            if(filterReviews.value) {
                ChartCard(
                    label = stringResource(id = R.string.label_stats_reviews),
                    entryData = reviewsStat.associate { (dateString, yValue) ->
                        LocalDate.parse(dateString) to yValue!!.toFloat()
                    },
                    optionalData = reviewsAvgData,
                    columnLabels = listOf(
                        stringResource(id = R.string.label_stats_date),
                        stringResource(id = R.string.label_stats_count),
                        stringResource(id = R.string.label_stats_avg_reviews)
                    ),
                    columnChart = true
                )
                Spacer(modifier = Modifier.height(24.dp))
            }


            if(filterAvgReviews.value) {
                ChartCard(
                    label = stringResource(id = R.string.label_stats_avg_reviews),
                    entryData = reviewsStat.associate { (dateString, yValue, avg) ->
                        LocalDate.parse(dateString) to avg!!.toFloat()
                    },
                    columnLabels = listOf(
                        stringResource(id = R.string.label_stats_date),
                        stringResource(id = R.string.label_stats_avg_reviews)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }


    if (showYearDialog.value) {
        Dialog(onDismissRequest = { showYearDialog.value = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    years.forEach { year ->
                        Text(
                            text = year,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    selectedYear.value = year
                                    getData()
                                    showYearDialog.value = false
                                },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    if(showStatsFilter.value){
        val tmpFilterCustomers = remember { mutableStateOf(filterCustomers.value) }
        val tmpFilterRevenue = remember { mutableStateOf(filterRevenue.value) }
        val tmpFilterReviews = remember { mutableStateOf(filterReviews.value) }
        val tmpFilterAvgReviews = remember { mutableStateOf(filterAvgReviews.value) }

        MessageSheet(
            height = 400.dp,
            buttonLabelId = R.string.label_apply,
            onDismiss = {showStatsFilter.value = false},
            buttonOnClick = {
                getData()
                filterCustomers.value = tmpFilterCustomers.value
                filterRevenue.value = tmpFilterRevenue.value
                filterReviews.value = tmpFilterReviews.value
                filterAvgReviews.value = tmpFilterAvgReviews.value

            },
            content = {
//                SearchFilterComponent()
//                HorizontalDivider()
                SwitchWithLabel(
                    stringResource(id = R.string.label_stats_customers),
                    tmpFilterCustomers.value
                ) { tmpFilterCustomers.value = it }
                HorizontalDivider()
                SwitchWithLabel(
                    stringResource(id = R.string.label_stats_revenue),
                    tmpFilterRevenue.value
                ) { tmpFilterRevenue.value = it }
                HorizontalDivider()
                SwitchWithLabel(
                    stringResource(id = R.string.label_stats_reviews),
                    tmpFilterReviews.value
                ) { tmpFilterReviews.value = it }
                HorizontalDivider()
                SwitchWithLabel(
                    stringResource(id = R.string.label_stats_avg_reviews),
                    tmpFilterAvgReviews.value
                ) { tmpFilterAvgReviews.value = it }
            }
        )
    }
}

@Composable
fun SearchFilterComponent() {
    // State to hold the value
    var value by remember { mutableIntStateOf(10) }

    // Layout for the component
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Test value",
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .clickable { value-- },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$value",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .clickable { value++ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.background,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ChartCard(
    label: String,
    entryData : Map<LocalDate, Float>,
    optionalData:  List<Float>? = null,
    columnLabels: List<String>,
    columnChart: Boolean = false
){
    val showChartDialog = remember { mutableStateOf(false) }


    val xValuesToDates = entryData.keys.associateBy { it.toEpochDay().toFloat() }
    val chartEntryModel: ChartEntryModel = if(optionalData != null && optionalData.size == entryData.keys.size){
        entryModelOf(
            xValuesToDates.keys.zip(entryData.values, ::entryOf),
            optionalData.mapIndexed { index, item ->
                FloatEntry(
                    x = xValuesToDates.keys.elementAt(index),
                    y = item
                )}
        )
    }
    else{
        entryModelOf(xValuesToDates.keys.zip(entryData.values, ::entryOf))
    }

    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")
    val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        (xValuesToDates[value] ?: LocalDate.ofEpochDay(value.toLong())).format(dateTimeFormatter)
    }

    val chartComponent = @Composable{
        val legends = listOfNotNull(
            legendItem(
                icon = shapeComponent(color = currentChartStyle.axis.axisLineColor),
                label = textComponent(
                    color = currentChartStyle.axis.axisLabelColor,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = columnLabels[1],
            )
        ).toMutableList()

        if(optionalData != null && optionalData.size == entryData.keys.size && columnLabels.size == 3){
            legends += legendItem(
                icon = shapeComponent(),
                label = textComponent(
                    color = currentChartStyle.axis.axisLabelColor,
                    textSize = 12.sp,
                    typeface = Typeface.MONOSPACE,
                ),
                labelText = columnLabels[2]
            )
        }
        
        Chart(
            chart = if(columnChart) columnChart() else lineChart(),
            model = chartEntryModel,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisValueFormatter),
            legend = if(optionalData != null && columnLabels.size == 3) verticalLegend(items = legends, iconSize = 8.dp, iconPadding = 10.dp) else null
        )
    }
    val labelComponent = @Composable{
        Text(
            modifier = Modifier.padding(10.dp),
            text = label,
            fontSize = 20.sp,
        )
    }

    Card(onClick = { showChartDialog.value = true }) {
        Column {
            labelComponent()
            chartComponent()
            Spacer(modifier = Modifier.height(5.dp))
        }

    }
    if (showChartDialog.value) {
        Dialog(onDismissRequest = { showChartDialog.value = false }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    labelComponent()
                    chartComponent()
                    Spacer(modifier = Modifier.height(24.dp))
                    TableScreen(
                        tableData = entryData.map{ ( date, value) ->
                            val dateVal = date.format(DateTimeFormatter.ofPattern("dd-MM-yy"))
                            listOfNotNull(dateVal, value, optionalData?.get(entryData.keys.indexOf(date)))
                        },
                        labels = columnLabels
                    )

                }
            }
        }
    }
}

@Composable
fun TableScreen(
    tableData: List<List<Any>>,
    labels: List<String>
) {

    val equalWeight = 1f/tableData[0].size
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        item {
            Row(Modifier.background(MaterialTheme.colorScheme.primary)) {
                labels.forEach { label ->
                    TableCell(
                        text = label,
                        textColor = MaterialTheme.colorScheme.background,
                        weight = equalWeight
                    )
                }
            }
        }
        items(tableData.size) {
            val item = tableData[it]
            Row(Modifier.fillMaxWidth()) {
                item.forEach { cellVal ->
                    TableCell(text = cellVal.toString(), weight = equalWeight)
                }
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline
            )
            .weight(weight)
            .padding(8.dp),
        style = textStyle,
        color = textColor
    )
}